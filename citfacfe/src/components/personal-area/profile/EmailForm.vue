<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <cf-modal  title="Inserisci indirizzo email"  id="modal-email" ref="modalEmail" @on-hide="onHideModal">
  <stepper-vertical :step="activeStep">
    <stepper-vertical-item
        :name="STEPS.INSERT"
        title="Email"
    >

      <cf-alert title="Attenzione!">
        <p class="mb-0">Riceverai una email per confermare l'indirizzo che hai inserito.</p>
      </cf-alert>
      <form id="form-email"
           
            class="pt-4"
            @submit.prevent="setEmailValidation">

        <input-text
            id="email1"
            v-model="email"
            label-text="Email"
            required
            type="email"

        ></input-text>
        <input-text
            id="email2"
            v-model="secondEmail"
            label-text="Ripeti Email"
            required
            type="email"

        ></input-text>

        <stepper-vertical-navigation @on-cancel="endStepper" confirm-label="Conferma e invia" form-id="form-email" submit/>

      </form>


    </stepper-vertical-item>

    <stepper-vertical-item
        :name="STEPS.VERIFY"
        title="Verifica"
    >
      <check-otp-form :email="email" @otp-verified="onEmailVerified"  @on-back="stepPrev">
       
      </check-otp-form>

    </stepper-vertical-item>

    <stepper-vertical-item
        :name="STEPS.END"
        last
        title="Fine"
    >
      <div class="w-100">
        <cf-alert title="Attenzione!">
          <p class="mb-0"> Verifica completata! <br>
            Il tuo indirizzo <strong>{{ email }}</strong> è stato verificato
          </p>
        </cf-alert>


        <stepper-vertical-navigation  @on-cancel="endStepper" confirm-label="Fine" form-id="check-otp-form" @on-confirm="emailConfirmed"/>
      </div>

    </stepper-vertical-item>
  </stepper-vertical>
  </cf-modal>
</template>

<script>
import StepperVertical from "../../stepper/StepperVertical.vue";
import StepperVerticalItem from "../../stepper/StepperVerticalItem.vue";
import InputText from "../../form/InputText.vue";
import JustValidate from "just-validate";
import CfModal from "../../generic/CfModal.vue";
import CheckOtpForm from "../../form/CheckOtpForm.vue";
import StepperVerticalNavigation from "../../stepper/StepperVerticalNavigation.vue";
import CfAlert from "../../generic/CfAlert.vue";

const STEPS = {
  INSERT: 1,
  VERIFY: 2,
  END: 3
}

// COMPONENTE CHE CONSENTE DI INSERIRE UNA NUOVA EMAIL
// PER UTILIZZARE IL COMPONENTE USARE I PROPS
//@email-confirmed = RESTITUISCE LA NUOVA EMAIL VERIFICATA

export default {
  name: "EmailForm",
  components: {
    CfAlert,
    StepperVerticalNavigation, CheckOtpForm, CfModal, InputText, StepperVerticalItem, StepperVertical
  },
  data() {
    return {
      STEPS,
      activeStep: STEPS.INSERT,
      email: "",
      secondEmail: "",
      emailValidation: null
    }
  },
  mounted() {
   
    this.emailValidation = new JustValidate("#form-email");

  },
  methods: {
    setEmailValidation() {
      let validation = this.emailValidation;
     
      validation.addField("#inputTextemail1", [
        {
          rule: "required",
          errorMessage: "Campo obbligatorio",
        },
        {
          rule: "email",
          errorMessage: "Deve essere un'email",
        },
      ]);
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
      ]);


       //SE LA VALIDAZIONE HA SUCCESSO
       validation.revalidate().then((isValid) => {
        if (isValid) {
          this.stepNext()
        }
      });
    },
    stepNext() {
      this.activeStep++
    },
    stepPrev(){
      this.activeStep--
    },
    onEmailVerified() {
      this.stepNext()
    },
    //CHIUDE IL FORM
    endStepper(){
       let modal =  this.$refs.modalEmail
      modal.onHide()
    
    },

    emailConfirmed(){
      this.$emit('email-confirmed', this.email)
      this.endStepper()
    },
    onHideModal(){
      this.activeStep =STEPS.INSERT
      this.email= ""
      this.secondEmail= ""

    }
  }
}
</script>


