<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <stepper ref="paymentStepper" v-model="activeStep" :steps="stepsList">

    <!--      INFORMATIVA SULLA PRIVACY     -->
    <!-- -------------------------------------------------------------------------->
    <template v-slot:[STEPS.PRIVACY]>
      <cf-page-section class="my-4">
        <cf-inner-loading :showing="isLoadingPolicy" class="my-4"/>

        <div v-show="!isLoadingPolicy">
          <div class="privacy-terms pb-2" v-html="privacyTermsHtml"></div>

          <input-checkbox
              id="__privacy-check"
              v-model="isPrivacyAcepted"
              labelText="Ho letto e compreso l'informativa sulla privacy"
              required

          >
            <template #error>{{ privacyErrorMsg }}</template>
          </input-checkbox>


          <div class="d-grid gap-2 d-md-block ">
            <button class="btn btn-primary fw-bold me-4 btn-next col-12 col-md-4"
                    @click="stepPrivacyNext">Avanti
            </button>
          </div>

        </div>

      </cf-page-section>
    </template>
    <!--      TIPOLOGIA DI PAGAMENTO     -->
    <!-- -------------------------------------------------------------------------->
    <template v-slot:[STEPS.PAYMENT_TYPE]>
      <div class="mt-5 mx-1">
        <small> I campi contraddistinti dal simbolo asterisco (*) sono obbligatori</small>
        <form id="payment-type-form" ref="paymentTypeForm" class="needs-validation"
              @submit.prevent="onNextTypeSelection">
          <form-bg title="Cosa intendi pagare?">
            <cf-select
                id="PaymentType"
                v-model="paymentTypeSelected"
                :option-list="paymentTypeList"
                label-text="Scegli un pagamento"
                required
                option-value="codiceVersamento"
                option-label="descrizioneVersamento"
            />

          </form-bg>
          <cf-alert v-if="paymentTypeNotes && paymentTypeNotes !== ''"  class="mt-4" title="Attenzione!">
            <p class="mb-0">Il campo "NOTE" deve essere compilato come segue:</p>
            <p>{{ paymentTypeNotes }}</p>
          </cf-alert>

          <form-bg title="Specifiche sul pagamento" class="mt-4">
            <input-textarea
                id="notes"
                v-model="notes"
                cols="600"
                label-text="Note"
                maxlength="600"
                more-info="Inserire massimo 600 caratteri"
                :required="istypeNoteRequired"
                type="textarea"
            />
          </form-bg>

          <stepper-navigation form-id="payment-type-form" submit @prev="stepPrev"/>
        </form>

      </div>
    </template>

    <!--      DATI PERSONALI    -->
    <!-- -------------------------------------------------------------------------->
    <template v-slot:[STEPS.USER_DATA]>
      <div class="mt-5 mx-1">
        <small> I campi contraddistinti dal simbolo asterisco (*) sono obbligatori</small>
        <form id="user-data-form" ref="userDataForm" class="needs-validation" @submit.prevent="onNextUserData">
          <form-bg title="Effettuato da *">
            <cf-select
                v-if="!isUserAuth"
                id="LegalEntity"
                v-model="legalEntitySelected"
                :option-list="legalEntityList"
                label-text="Seleziona"
                more-info="Seleziona la tipologia del soggetto giuridico che usufruirà del servizio"
                required
            />
            <div v-if="isNaturalPerson">
              <input-text
                  id="name"
                  v-model="name"
                  label-text="Nome"
                  more-info="Inserisci il tuo nome"
                  required

              />

              <input-text
                  id="surname"
                  v-model="surname"
                  label-text="Cognome"
                  more-info="Inserisci il tuo cognome"
                  required
              />

            </div>
            <div v-if="isLegalPerson">
              <input-text
                  id="businessName"
                  v-model="businessName"
                  label-text="Ragione sociale"
                  more-info="Inserisci la tua Ragione sociale"
                  required

              />
            </div>

            <input-text
                id="email1"
                v-model="email"
                label-text="Email"
                more-info="Inserisci la tua email"
                required
                type="email"
                @input="onInputEmail"
            />
            <input-text
                id="email2"
                v-model="verifiedEmail"
                label-text="Ripeti email"
                more-info="Inserisci nuovamente la tua email"
                required
                type="email"
            />
            <input-text
                id="taxCode"
                v-model="taxCode"
                :disabled="noItalianId"
                is-uppercase
                label-text="Codice fiscale / P.Iva"
                more-info="Inserisci il tuo codice fiscale"
                required
            />
              <template v-if="!user">
                <input-checkbox id="__noItalianId"
                            v-model="noItalianId"
                            labelText="Qualora non si disponesse di un identificativo fiscale italiano valido, contrassegnare questa casella"
                            small-text
                            @input="onNoItalianId"
            />
              </template>  
            


          </form-bg>

          <form-bg no-padding title="Importo *">
            <input-text
                id="amount"
                v-model="amount"
                class="pt-3"
                label-text="Inserisci importo"
                min="0"
                more-info="Inserisci l'importo che desideri versare adesso"
                prepend
                required
                step="0.01"
                type="number"
            >
              <template #prepend>
                <strong>€</strong>
              </template>
            </input-text>
          </form-bg>

          <stepper-navigation form-id="user-data-form" submit @prev="stepPrev"/>
        </form>


      </div>
    </template>

    <!--    STEP RIEPILOGO  -->
    <!-- -------------------------------------------------------------------  -->
    <template v-slot:[STEPS.SUMMARY]>

      <div class="mx-1">

        <div class="bg-gray pb-2 my-2">
          <div class="row m-4">
            <div class="col-12 px-0 py-3">
              <h3>Riepilogo</h3>
            </div>

            <!--     PAGAMENTO     -->
            <div class="col-12  bg-white p-3 mb-4">
              <div class="it-list-wrapper">
                <ul class="it-list">
                  <li>
                    <div class="list-item ">
                      <div class="it-right-zone">
                        <h4 class="fw-bold mb-0 h4 text-black">Pagamento</h4>
                        <a class="fw-bold small" href="#pagamento"
                           @click.prevent.stop="activeStep  = STEPS.PAYMENT_TYPE">Modifica</a>
                      </div>

                    </div>
                  </li>
                  <li>
                    <div class="list-item">
                      <div class="py-2">
                        <small>Tipologia pagamento</small>
                        <div class="fw-bold">{{ paymentType ? paymentType.name : '-' }}</div>
                      </div>

                    </div>
                  </li>
                  <li>
                    <div class="list-item">
                      <div class="py-2">
                        <small>Note (specifiche sul pagamento)</small>
                        <div class="fw-bold">{{ notes || '-' }}</div>
                      </div>

                    </div>
                  </li>
                </ul>
              </div>
            </div>

            <!--     DATI UTENTE     -->
            <div class="col-12  bg-white p-3 mb-4">
              <div class="it-list-wrapper">
                <ul class="it-list">
                  <li>
                    <div class="list-item ">
                      <div class="it-right-zone">
                        <h4 class="fw-bold mb-0 h4 text-black">Effettuato da</h4>
                        <a class="fw-bold small" href="#dati-utente"
                           @click.prevent.stop="activeStep  = STEPS.USER_DATA">Modifica</a>
                      </div>

                    </div>
                  </li>
                  <template v-if="isNaturalPerson">
                    <li>
                      <div class="list-item">
                        <div class="py-2">
                          <small>Nome</small>
                          <div class="fw-bold">{{ name || '-' }}</div>
                        </div>

                      </div>
                    </li>
                    <li>
                      <div class="list-item">
                        <div class="py-2">
                          <small>Cognome</small>
                          <div class="fw-bold">{{ surname || '-' }}</div>
                        </div>
                      </div>
                    </li>
                  </template>
                  <template v-else-if="isLegalPerson">
                    <li>
                      <div class="list-item">
                        <div class="py-2">
                          <small>Ragione sociale</small>
                          <div class="fw-bold">{{ businessName || '-' }}</div>
                        </div>
                      </div>
                    </li>
                  </template>

                  <li>
                    <div class="list-item">
                      <div class="py-2">
                        <small>Email</small>
                        <div class="fw-bold">{{ email || '-' }}</div>
                      </div>
                    </div>
                  </li>
                  <li>
                    <div class="list-item">
                      <div class="py-2">
                        <small>Codice fiscale / P.Iva </small>
                        <div class="fw-bold">{{ taxCode || '-' }}</div>
                      </div>
                    </div>
                  </li>

                </ul>
              </div>
            </div>

            <!--     IMPORTO     -->
            <div class="col-12  bg-white p-3 mb-4">
              <div class="it-list-wrapper">
                <ul class="it-list">
                  <li>
                    <div class="list-item ">
                      <div class="it-right-zone">
                        <h4 class="fw-bold mb-0 h4 text-black">Importo</h4>
                      </div>

                    </div>
                  </li>
                  <li>
                    <div class="list-item">
                      <div class="py-2">
                        <small>Importo dovuto</small>
                        <div class="fw-bold">{{ amount || '-' }} €</div>
                      </div>

                    </div>
                  </li>

                </ul>
              </div>
            </div>

          </div>
        </div>

        <stepper-navigation :nextLabel="lastStepTitle" @next="goToPayment" @prev="stepPrev" :loading="loading"/>
      </div>

    </template>
  </stepper>
</template>

<script>
import CfInnerLoading from "../../components/generic/CfInnerLoading.vue";
import InputCheckbox from "../../components/form/InputCheckbox.vue";
import Stepper from "../../components/stepper/Stepper.vue";
import FormBg from "../../components/form/FormBg.vue";
import CfSelect from "../../components/form/CfSelect.vue";
import StepperNavigation from "../../components/stepper/StepperNavigation.vue";
import JustValidate from "just-validate";
import InputText from "../../components/form/InputText.vue";
import InputTextarea from "../../components/form/InputTextarea.vue";
import {isValidCF} from "../../services/utils";

import CfAlert from "../../components/generic/CfAlert.vue";
import InputTextArea from "../../components/form/InputTestualeArea.vue";
import CfPageSection from "../../components/generic/CfPageSection.vue";

import { getpaymentPolicy } from "../../services/api";
import { NOT_ITALIAN_CF_PAYMENTS } from "../../configuration";


const STEPS = {
  PRIVACY: 1,
  PAYMENT_TYPE: 2,
  USER_DATA: 3,
  SUMMARY: 4,
  LAST_STEP: 5
}

const STEPS_LIST = [
  {id: STEPS.PRIVACY, name: "INFORMATIVA PRIVACY"},
  {id: STEPS.PAYMENT_TYPE, name: "TIPOLOGIA DI PAGAMENTO"},
  {id: STEPS.USER_DATA, name: "DATI PERSONALI"},
  {id: STEPS.SUMMARY, name: "RIEPILOGO"},
]
const LEGAL_ENTITY_MAP = {
  NATURAL_PERSON: 'F',
  LEGAL_PERSON: 'G'
}
export default {
  name: "PaymentSpontaneousStepper",
  components: {
    CfPageSection,
    InputTextArea,
    CfAlert,
    InputTextarea,
    InputText,
    StepperNavigation,
    CfSelect,
    FormBg,
    Stepper,
    InputCheckbox,
    CfInnerLoading,

  },
  props: {
    lastStepTitle: String,
    loading:Boolean
  },
  data() {
    return {
      STEPS,
      STEPS_LIST,
      stepsList:[],
      activeStep: 1,
      isLoadingPolicy: false,
      privacyTermsHtml: '',
      isPrivacyAcepted: false,
      privacyErrorMsg: '',
      paymentTypeSelected: null,
      paymentNotes: '',
      paymentTypeValidation: null,
      userDataValidation: null,
      legalEntitySelected: null,
      legalEntityList: [{
        name: "Persona fisica",
        id: LEGAL_ENTITY_MAP.NATURAL_PERSON
      },
        {
          name: "Persona giuridica",
          id: LEGAL_ENTITY_MAP.LEGAL_PERSON
        }
      ],
      name: '',
      businessName: '',
      surname: '',
      email: '',
      verifiedEmail: '',
      taxCode: '',
      noItalianId: false,
      notes: '',
      amount: null,


    }
  },
  watch: {
    isPrivacyAcepted() {
      let isValid = this.setPrivacyValidation()
    },

  },
 async  mounted() {
    scroll(0, 0);
    this.isLoadingPolicy = true
    try {
      let {data: terms} = await getpaymentPolicy()
      this.privacyTermsHtml = terms
    } catch (e) {
      await this.$store.dispatch("setNotificationInfo", {body:"Non è stato possibile recuperare l'informativa, si prega di riprovare"})

    } finally {
      this.isLoadingPolicy = false

    }
  },
  created() {
    let lastStep= [{id: STEPS.LAST_STEP, name: this.lastStepTitle}]
    this.stepsList = [...STEPS_LIST, ...lastStep]
    if (this.isUserAuth) {
      this.name = this.user.nome
      this.surname = this.user.cognome
      this.email = this.user.contatti?.email
      this.verifiedEmail =  this.user.contatti?.email
      this.taxCode = this.user.codFisc
      this.legalEntitySelected =  LEGAL_ENTITY_MAP.NATURAL_PERSON
    }
  },
  computed: {
    user() {
      return this.$store.getters['getUser']
    },
    paymentType() {
      return this.paymentTypeList.find(item => item.id === this.paymentTypeSelected)
    },
    getDevLogout() {
      return this.$store.getters["getDevLogout"];
    },
    isUserAuth() {
      return this.user && !this.getDevLogout
    },

    isNaturalPerson() {
      return this.legalEntitySelected === LEGAL_ENTITY_MAP.NATURAL_PERSON
    },
    isLegalPerson() {
      return this.legalEntitySelected === LEGAL_ENTITY_MAP.LEGAL_PERSON
    },
 
    paymentTypeList (){
    return  this.$store.getters["getPaymentTypeList"]
    },
    paymentTypeNotes(){
     return this.paymentType?.notePerIlPagatore ?? ''
    },
    istypeNoteRequired(){
      return !!this.paymentType?.noteObbligatorie
    }
  },
  methods: {

    setPrivacyValidation() {
      const checkBox = document.getElementById('inputCheckbox__privacy-check')
      const validityState = checkBox.validity;

      if (this.onClickStepPrivacyNext) {
        checkBox.reportValidity();

        if (validityState.valueMissing) {
          this.privacyErrorMsg = "Dichiara di aver letto l'informativa"
          checkBox.setCustomValidity(this.privacyErrorMsg);
          return false
        } else {
          this.privacyErrorMsg = ""
          checkBox.setCustomValidity(this.privacyErrorMsg);
          return true
        }
      }

    },
    stepNext() {
      this.activeStep += 1

    },
    stepPrev() {
      this.activeStep -= 1

    },
    stepPrivacyNext() {
      this.onClickStepPrivacyNext = true

      let isValid = this.setPrivacyValidation()
      if (isValid) {
        this.stepNext()
        
        this.$nextTick(() => {
          this.paymentTypeValidation = new JustValidate("#payment-type-form", {validateBeforeSubmitting: true});

        });
      }

    },
    onNextTypeSelection() {
      this.setPaymentTypeFormValidation()
    },
    onNextUserData() {
      this.setUserDataFormValidation()
    },
    setPaymentTypeFormValidation() {
      let validation = this.paymentTypeValidation
      
      
        validation.addField("#inputSelectPaymentType", [
        {
          rule: "required",
          errorMessage: "Campo obbligatorio",
        },
      ]);
      
  
      if(this.istypeNoteRequired){
        validation.addField("#inputTextareanotes", [
        {
          rule: "required",
          errorMessage: "Campo obbligatorio",
        }
      ])
      }
     

      //SE LA VALIDAZIONE HA SUCCESSO
      validation.revalidate().then(isValid => {
        if (isValid) {
          this.stepNext()
          this.$nextTick(() => {
            this.userDataValidation = new JustValidate("#user-data-form", {validateBeforeSubmitting: true});
          });
        }
      })

    },
    setUserDataFormValidation() {
      this.userDataValidation = new JustValidate("#user-data-form", {validateBeforeSubmitting: true});
      let validation = this.userDataValidation
      if(!this.isUserAuth){
      validation.addField("#inputSelectLegalEntity", [
        {
          rule: "required",
          errorMessage: "Campo obbligatorio",
        },
      ]);
      }
      if (!!this.legalEntitySelected) {
        if (this.isNaturalPerson) {
          validation.addField("#inputTextname", [
            {
              rule: "required",
              errorMessage: "Campo obbligatorio",
            },
          ])
          validation.addField("#inputTextsurname", [
            {
              rule: "required",
              errorMessage: "Campo obbligatorio",
            },
          ])
        } else if (this.isLegalPerson) {
          validation.addField("#inputTextbusinessName", [
            {
              rule: "required",
              errorMessage: "Campo obbligatorio",
            },
          ])
        }

        validation.addField("#inputTextemail1", [
          {
            rule: "required",
            errorMessage: "Campo obbligatorio",
          },
          {
            rule: "email",
            errorMessage: "Deve essere un'email",
          },
        ])
        validation.addField("#inputTextemail2", [
          {
            rule: "required",
            errorMessage: "Campo obbligatorio",
          },
          {
            rule: "email",
            errorMessage: "Deve essere un'email",
          },
          {
            validator: (value, fields) => {
              if (fields['#inputTextemail1'] && fields['#inputTextemail1'].elem) {

                const emailValue = fields['#inputTextemail1'].elem.value;

                return value === emailValue;
              }

              return true;
            },
            errorMessage: 'Deve essere uguale all\'email',
          }
        ])
        validation.addField("#inputTexttaxCode", [
          {
            rule: "required",
            errorMessage: "Campo obbligatorio",
          },
          {
            validator: (value) => isValidCF(value),
            errorMessage: " Inserire un codice fiscale valido",
          }
        ])


        validation.addField("#inputTextamount", [
          {
            rule: "required",
            errorMessage: "Campo obbligatorio",
          },
          {
            validator: (value) => value > 0,
            errorMessage: " Inserire un importo valido",
          }
        ])
      }


      //SE LA VALIDAZIONE HA SUCCESSO

      validation.revalidate().then(isValid => {
        if (isValid) {
          this.stepNext()
        }

      })

    },
    goToPayment() {

      let params = {
        paymentType: this.paymentTypeSelected,
        notes: this.notes,
        amount: this.amount,
        name: this.name,
        surname: this.surname,
        businessName: this.businessName,
        taxCode:this.taxCode,
        email: this.email
      }

      this.$emit('on-last-step', params)
     

    },
    onNoItalianId(event) {
   
      if (event.target.checked) {
        this.taxCode = NOT_ITALIAN_CF_PAYMENTS
      } else {
        this.taxCode = this.user?.codFisc ?? null
      }
    },
    onInputEmail(val){
      if(this.isUserAuth && val !== this.user.contatti?.email){
        this.verifiedEmail = null
      }
    }

  }
}
</script>

