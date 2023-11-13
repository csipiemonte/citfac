<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>

  <main id="main-container" class="container">
    <cf-title-section :breadcrumbs="BREADCRUMBS" subtitle class="pb-4 pb-lg-5">
      <template #title>
        Profilo utente {{userFullname}}
      </template>

      <template #subtitle>
        CF: {{userTaxCode}}
      </template>
    </cf-title-section>

    <stepper ref="profileStepper" v-model="activeStep" :steps="stepList">
      <!--      INFORMATIVA SULLA PRIVACY     -->
      <!-- -------------------------------------------------------------------------->
      <template v-slot:[STEPS.PRIVACY]>
        <div>

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
        </div>

      </template>

      <!--      CONTATTI     -->
      <!-- -------------------------------------------------------------------------->

      <template v-slot:[STEPS.CONTACTS]>
        <div>
          <cf-page-section class="my-4">
            <user-contacts-card :email="email" :telephone="telephone" @on-update-email="setEmail"
                                @on-update-telephone="setTelephone"></user-contacts-card>

            <div v-if="hasNoEmail" class="alert alert-danger my-3" role="alert">
              <p class="mb-0"> Attenzione: l'indirizzo email è obbligatorio</p>
            </div>


            <stepper-navigation  @next="stepContactsNext"
                                @prev="stepPrev"></stepper-navigation>
          </cf-page-section>




        </div>

      </template>


      <!--      PREFERENZE     -->
      <!-- -------------------------------------------------------------------------->
      <template v-slot:[STEPS.PREFERENCES]>
        <div>
          <cf-page-section class="my-4">

            <preferences-card @on-change="onChangePreferences"></preferences-card>

            <stepper-navigation @next="stepPreferencesNext" @prev="stepPrev"></stepper-navigation>
          </cf-page-section>

          <cf-inner-loading :showing="isSendingPreferences"/>

        </div>

      </template>

    </stepper>


  </main>
  <!--    SEZIONE FISSA DEI CONTATTI-->
  <contacts-section></contacts-section>
</template>

<script>

import CfTitleSection from "../../components/generic/CfTitleSection.vue";
import CfPageSection from "@/components/generic/CfPageSection.vue";
import InputCheckbox from "../../components/form/InputCheckbox.vue";
import { isEmpty} from "../../services/utils";
import TitleSection from "../../components/generic/TitleSection.vue";
import Stepper from "../../components/stepper/Stepper.vue";
import UserContactsCard from "../../components/personal-area/profile/UserContactsCard.vue";
import {getNotifyTerms, saveUserContacts, setNotifyPreferencesList, updateNotifyTerms} from "../../services/api";
import EmailForm from "../../components/personal-area/profile/EmailForm.vue";
import CfInnerLoading from "../../components/generic/CfInnerLoading.vue";
import StepperNavigation from "../../components/stepper/StepperNavigation.vue";
import PreferencesCard from "../../components/personal-area/profile/PreferencesCard.vue";

import ContactsSection from "../../components/generic/ContactsSection.vue";
import {PERSONAL_AREA, USER_PROFILE} from "../../router/routes";


const BREADCRUMBS = [
  {url: PERSONAL_AREA.path, name: "Area Personale", data_element:"personal-area-login"},
  {url: USER_PROFILE.path, name: "Profilo"}

]

const STEPS = {
  PRIVACY: 1,
  CONTACTS: 2,
  PREFERENCES: 3
}


export default {
  name: "PageProfileNew",
  components: {
    ContactsSection,
    PreferencesCard,
    StepperNavigation,
    CfInnerLoading,
    EmailForm, UserContactsCard, Stepper, CfTitleSection, TitleSection, InputCheckbox, CfPageSection
  },
  data() {
    return {
      BREADCRUMBS,
      STEPS,
      stepList: [
        {id: STEPS.PRIVACY, name: "INFORMATIVA PRIVACY"},
        {id: STEPS.CONTACTS, name: "CONTATTI"},
        {id: STEPS.PREFERENCES, name: "PREFERENZE"},
      ],
      isPrivacyAcepted: false,
      privacyValidation: null,
      activeStep: 1,
      privacyTermsHtml: null,
      email: '',
      telephone: '',
      isLoadingPolicy: true,
      hasNoEmail: false,
      isSendingContacts: false,
      isSendingPreferences: false,
      selectedPreferences: {},
      privacyErrorMsg: '',
      onClickStepPrivacyNext: false
    }
  },
  watch: {
    isPrivacyAcepted() {
      let isValid =  this.setPrivacyValidation()
    }
  },
  mounted() {
    scroll(0, 0);
  },
  computed: {
    user() {
      return this.$store.getters['getUser']
    },
    userContacts(){
      return this.user?.contatti
    },
    userTaxCode() {
      return this.user?.codFisc ?? null
    },
    userFullname() {
      let name = this.user?.nome
      let surname = this.user?.cognome

      return name && surname ? `${name} ${surname}` : null
    },

    servicesList() {
      return this.$store.getters["getNotifyServicesList"]
    },
  },
  async created() {
    this.isLoadingPolicy = true
    this.email = this.user.email
    this.telephone = this.user.phoneNumber
    try {
      let {data: terms} = await getNotifyTerms()
      this.privacyTermsHtml = terms
    } catch (e) {
      await this.$store.dispatch("setNotificationInfo", {body:"Non è stato possibile recuperare l'informativa, si prega di riprovare"})

    } finally {
      this.isLoadingPolicy = false

    }

  },
  methods: {

    setPrivacyValidation() {
      const checkBox = document.getElementById('inputCheckbox__privacy-check')
      const validityState = checkBox.validity;

      if(this.onClickStepPrivacyNext){
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
      this.activeStep +=1
    },
    stepPrev() {
      this.activeStep -=1
    },
    stepPrivacyNext() {
      this.onClickStepPrivacyNext = true


      let isValid = this.setPrivacyValidation()
      if (isValid) {
        this.stepNext()
      }

    },
    async stepContactsNext() {
      if (!this.email) {
        this.hasNoEmail = true

      } else {
        this.hasNoEmail = false
        this.$refs.profileStepper.next()
      }
    },

    onChangePreferences(preferences) {
      this.selectedPreferences = preferences
    },
    async stepPreferencesNext() {
     
      this.isSendingPreferences = true


      try {
        let responseTerms = await updateNotifyTerms(this.userTaxCode)

        let payload = this.userContacts || {};
        if (!isEmpty(this.email)) payload.email = this.email;
        if (!isEmpty(this.telephone)) {
          payload.phone = this.telephone;
          payload.sms = this.telephone;
        }

        let {data: contacts} = await saveUserContacts(this.userTaxCode, payload)
        this.$store.dispatch('setUserContacts', {contacts})
        let responsePreferences = await setNotifyPreferencesList(this.userTaxCode, this.selectedPreferences)
        this.$router.push({name: 'profilo-utente-nuovo-feedback'})
      } catch (e) {
        await this.$store.dispatch("setNotificationInfo", {body:"Non è stato possibile aggiornare il profilo, si prega di riprovare"})
      }

      this.isSendingPreferences = false
    },
    setEmail(email) {
      this.email = email
    },
    setTelephone(tel) {
      this.telephone = tel
    },

  }
}
</script>

