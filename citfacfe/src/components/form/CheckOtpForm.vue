<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
<!--  FORM PER INVIO E VERIFICA DEL OTP-->
  <div>
    <cf-inner-loading :showing="isLoadingOtp"/>

    <template v-if="!isLoadingOtp">
      <form id="check-otp-form"  novalidate  @submit.prevent="setValidation">

        <cf-alert title="Attenzione!">

            <p class="mb-0">
              <template v-if="email">
              Abbiamo inviato il codice di verifica all'indirizzo <strong>{{ email }}</strong>
              </template>
              <template v-else-if="telephone">
                Abbiamo inviato il codice di verifica al numero <strong>{{telephone}}</strong>
              </template>
              <br/>
              Inseriscilo qui sotto per terminare la procedura
            </p>

        </cf-alert>
        <div class="row pt-4">
          <div class="col-12">
            <template v-if="!errorGenerationOtp">
              <input-text
                  id="Otp"
                  :disabled="isLoadingOtp"
                  label-text="Codice di conferma"
                  required
                  style="margin-bottom: 0 !important"
                  type="number"
                  v-model="selectedCode"

              ></input-text>
              
              <div v-if="isCountdownVisible" class="px-2">
                <vue-countdown v-slot="{  minutes, seconds }" :time="countdown" @ended="onOtpExpired">
                  <small class="text-danger fw-bold ">Il codice scadrà tra {{ minutes }}:{{
                      $filters.pad(seconds)
                    }}</small>
                </vue-countdown>
              </div>


            </template>

            <template v-else>
              <div class="alert alert-danger my-3" role="alert">
                <p class="mb-0" v-if="maxLimitErr"> Attenzione: Limite richieste giornaliere superato, puoi riprovare domani.</p>
                  <p class="mb-0" v-else> Attenzione: non è stato possibile generare l'otp, si prega
                  di riprovare</p>
              </div>

         

            </template>


          </div>
          <div class="row p-0 col-12 justify-content-end" >
            <button
             v-show="!maxLimitErr"
                class="btn btn-link fw-bold "
                @click="restartOtp"
              >
                Genera nuovo codice

                <span v-if="restartingOtp" class="ms-2">
                          <cf-inner-loading showing small />
                        </span>
              </button>
          </div>  
        </div>

        <Transition>
          <div v-if="hasVerificationError" class="alert alert-danger my-3" role="alert">
            <p class="mb-0"> Codice errato, assicurati di aver copiato bene oppure prova a far generare un nuovo
              codice</p>
          </div>

        </Transition>

        <nav
              class="d-flex flex-nowrap pt-4 w-100 justify-content-between stepper-vertical-navigation"
            >
              <button
                v-if="maxLimitErr"
                class="btn btn-outline-primary fw-bold me-4 btn-back"
                type="button"
                @click="$emit('on-cancel')"
              >
                Annulla
              </button>
              <button
               v-else
                class="btn btn-outline-primary fw-bold me-4 btn-back"
                type="button"
                @click="stepPrev"
              >
                Indietro
              </button>
              <button
              :disabled="errorGenerationOtp"
                class="btn btn-primary fw-bold btn-next"
                submit
              >
                Conferma
              </button>
            </nav>
      </form>
    </template>
  </div>

</template>
<!--  "id" UNIVOCO PER IDENTIFICAZIONE-->
<!--      "label-text" ETICHETTA DEL CAMPO INPUT-->
<!--      "start-timer" DURATA TIMER IN MILLISECONDI-->
<!--      "cf" CODICE FISCALE DEL RICHIEDENTE-->
<!--      "email" MAIL PER CREAZIONE OTP -->
<!--      @sendRequest- EMESSO PER CONTROLLARE L'OTP CREATO E INSERITO-->
<script>

import {createOtp, validateOtp} from "../../services/api";
import CfInnerLoading from "../generic/CfInnerLoading.vue";
import VueCountdown from '@chenfengyuan/vue-countdown';
import JustValidate from "just-validate";
import CfAlert from "../generic/CfAlert.vue";
import InputText from "./InputText.vue";
import {differenceInMilliseconds } from 'date-fns'


export default {
  name: "CheckOtpForm",
  components: {InputText, CfAlert, CfInnerLoading,  VueCountdown},
  props: {
    isUppercase: Boolean,
    email: String,
    telephone: String,
    generateNew: Boolean
  },

  data() {
    return {
      errorGenerationOtp: false,
      isLoadingOtp: false,
      selectedCode: '',
      isCountdownVisible: false,
      isOtpExpired: false,
      validation: null,
      generatedOtp: '',
      otpExpirationDate: null,
      countdown: null,
      hasVerificationError: false,
      isVerifyingOtp: false,
      maxLimitErr:false,
      restartingOtp:false
    }
  },
  computed: {
    taxCode() {
      let user = this.$store.getters["getUser"]
      return user?.codFisc
    },
    inputId() {
      return 'inputTextOtp'
    },
    requestType(){
      let type = ""
      if(this.email){
        type = "EMAIL"
      }else if(this.telephone){
        type="SMS"
      }
      return type
    },
    selectedContact(){
      let contact = ''
      if(this.email){
        contact = this.email
      }else if(this.telephone){
        contact = this.telephone
      }
      return contact
    }
  },

  async mounted() {
    this.isLoadingOtp = true;
    await this.createOtp();
    this.isLoadingOtp = false;
    this.$nextTick(()=>{
      this.validation = new JustValidate("#check-otp-form");
    })
    
   

  },

  methods: {
    //CREAZIONE DLL'OTP
    async createOtp() {

      let payload = {
        codiceFiscale: this.taxCode,
        tipoRichiesta: this.requestType,
        contatto: this.selectedContact,
      };
     
      try {
        let {data} = await createOtp(payload);
        this.generatedOtp = data.codiceOTP
        this.otpExpirationDate = data.expirationDate
        this.getCountdownTimestamp()
        this.isCountdownVisible = true
      } catch (error) {
      
        let errorMsg = "Non è stato possibile inviare la richiesta, si prega di riprovare"
        if(error.request?.status == 403 ){
          this.maxLimitErr = true;
          errorMsg = "Limite richieste giornaliere SMS OTP superato"
        }

        this.errorGenerationOtp = true;
        this.$emit('error-otp')
        await this.$store.dispatch("setNotificationInfo", {body:errorMsg})
      }
      this.textForm = document.getElementById("inputTextOtp");
     
    },
    //RICOMINCIO TIMER E CREO ALTRO OTP
    async restartOtp() {
      this.isCountdownVisible = false
      this.errorGenerationOtp = false;
      this.maxLimitErr = false;
      this.restartingOtp = true
      await this.createOtp();
      this.restartingOtp = false
      this.selectedCode = "";
    },
    //EMETTO EVENTO PER CONTROLLARE OTP
    async checkOtp() {
      let otp = this.selectedCode;

      let params = {
        richiesta: this.requestType,
        otp: otp,
        cf: this.taxCode,
      };


      this.isVerifyingOtp = true;
      this.hasVerificationError = false;
     
      try {
        let {data} = await validateOtp(params);

        if (data === null) {
          // Codice non verificato
          this.hasVerificationError = true;
          return;
        }

        this.$emit("otp-verified");
      } catch (error) {
      
        this.hasVerificationError = true;
      }

    },
    onOtpExpired() {
      this.isOtpExpired = true
      this.isCountdownVisible = false
    },
    setValidation() {
      let validation = this.validation;
      let otpForm = document.getElementById('inputTextOtp');
      if(!otpForm) return
      validation.addField("#inputTextOtp", [
        {
          rule: "required",
          errorMessage: "Campo obbligatorio",
        },
        {
          rule: 'minLength',
          value: 5,
          errorMessage:"Deve avere esattamente 5 cifre"
        },
        {
          rule: 'maxLength',
          value: 5,
          errorMessage:"Deve avere esattamente 5 cifre"
        },
        {
          validator: () => {
            return !this.isOtpExpired;
          },
          errorMessage: 'Codice scaduto',
        }

      ]);

    
       //SE LA VALIDAZIONE HA SUCCESSO
       validation.revalidate().then((isValid) => {
        if (isValid) {
          this.checkOtp()
        }
      });
    },

    getCountdownTimestamp() {
      let expirationDate = new Date(this.otpExpirationDate)
      this.countdown = differenceInMilliseconds(expirationDate, new Date())

    },
    stepPrev(){
      this.$emit('on-back')
    }
  }
}
</script>


