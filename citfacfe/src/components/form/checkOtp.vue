<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div
    data-keyboard="false"
    data-bs-backdrop="static"
    class="modal"
    id="otpModal"
  >
    <div style="margin: auto" class="modal-dialog modal-fullscreen-sm-down modal-dialog-centered">
      <div class="modal-content">
        <!-- Modal Header -->
        <div class="modal-header">
          <h4 class="modal-title">Verifica email</h4>
          <button
            type="button"
            class="btn-close"
            data-bs-dismiss="modal"
          ></button>
        </div>

        <!-- Modal body -->

        <div
          :style="[
            !isLoadingOtp
              ? { visibility: 'visible' }
              : { visibility: 'hidden' },
          ]"
        >
          <div class="modal-body">
            Abbiamo inviato il codice di verifica all'indirizzo <strong>{{ email }}</strong>
            <br />
            Inseriscilo qui sotto per terminare la procedura
          </div>

          <div class="row px-4 pt-4">
            <div class="col-12">
              <template v-if="!errorGenerationOtp">
                <div class="form-group" style="margin-bottom: 5px !important">
                  <label :class="{ active: labelActive }" :for="currentId">{{
                    labelText
                  }}</label>
                  <input
                    :class="{ 'text-uppercase': isUppercase }"
                    :name="currentId"
                    @focusout="activateLabel"
                    @focusin="activateLabel"
                    type="text"
                    class="form-control"
                    :id="currentId"
                    :disabled="isLoadingOtp"
                    @input="input"
                  />
                  <small class="form-text text-200" :id="currentId">{{
                    moreInfo
                  }}</small>
                  <template v-if="!errorGenerationOtp">
                    <span class="form-text text-200"
                      >Il codice scadrà tra:
                      <small class="form-text text-200" id="timer"></small
                    ></span>
                  </template>
                </div>
              </template>

              <template v-if="errorGenerationOtp">
                <div>
                  <p class="text-danger form-text">
                    Attenzione: non è stato possibile generare l'otp, si prega
                    di riprovare
                  </p>
                </div>
              </template>


                <div class="row justify-content-end gap-2 py-3 ">
                  <div class="col-md-auto col-12">
                <button
                  type="button"
                  class="btn btn-outline-primary w-100"
                  @click="restartOtp()"
                >
                  Genera nuovo codice
                </button>
                  </div>
                  <div class="col-md-auto col-12">
                <button
                  :disabled="errorGenerationOtp"
                  @click="checkOtp()"
                  type="button"
                  class="btn btn-primary w-100"
                  data-bs-dismiss="modal"
                >
                  Conferma
                </button>
                  </div>
                </div>

            </div>
          </div>
        </div>

        <template v-if="isLoadingOtp">
          <div class="row py-2 justify-content-center full-width overlay">
            <div class="col-auto">
              <div class="progress-spinner progress-spinner-active">
                <span class="visually-hidden">Caricamento...</span>
              </div>
            </div>
          </div>
        </template>
      </div>
    </div>
  </div>

</template>
<!--    MODALE PER IL CREZIONE E CONTROLLO DEGLI OTP. NECESITA DI VARI PROP:-->
<!--  "id" UNIVOCO PER IDENTIFICAZIONE-->
<!--      "label-text" ETICHETTA DEL CAMPO INPUT-->
<!--      "start-timer" DURATA TIMER IN MILLISECONDI-->
<!--      "modal-visible" VISIBILITA DELLA MODALE-->
<!--      "cf" CODICE FISCALE DEL RICHIEDENTE-->
<!--      "email" MAIL PER CREAZIONE OTP -->
<!--      @sendRequest- EMESSO PER CONTROLLARE L'OTP CREATO E INSERITO-->
<script>
import { createOtp } from "../../services/api";

export default {
  name: "CheckOtp",
  props: {
    labelText: String,
    id: String,
    moreInfo: String,
    isUppercase: Boolean,
    startTimer: Number,
    cf: String,
    email: String,
    modalVisible: Boolean,
  },

  data() {
    return {
      textForm: null,
      currentId: null,
      labelActive: false,
      interval: null,
      errorGenerationOtp: false,
      isLoadingOtp: false,
      selectedCode: null,
    };
  },

  async created() {
    this.currentId = "inputText" + this.id;
  },

  computed: {},
  mounted() {},
  watch: {
    //CREO OTP SOLO QUANDO LA MODALE è VISIBILE
    async modalVisible() {
      await this.createOtp();
      if (this.modalVisible === true && !this.errorGenerationOtp) {
        this.textForm = document.getElementById(this.currentId);

        //IN CASO DI ERRORE MOSTRO MESSAGGIO APPOSITO
        if (!this.errorGenerationOtp) {
          this.setOtpCountDown();
        }
      }

      //SE STO CHIUDENDO LA MODALE, BLOCCO L'INTERVAL
      if (this.modalVisible === false) {
        clearInterval(this.interval);
      }
    },
  },
  methods: {
    //CREAZIONE DLL'OTP
    async createOtp() {
      let payload = {
        codiceFiscale: this.cf,
        tipoRichiesta: "EMAIL",
        contatto: this.email,
      };
      this.isLoadingOtp = true;
      try {
        let promise = await createOtp(payload);
      } catch (e) {
        this.errorGenerationOtp = true;
      }
      this.isLoadingOtp = false;
    },

    //RICOMINCIO TIMER E CREO ALTRO OTP
    async restartOtp() {
      this.errorGenerationOtp = false;
      await clearInterval(this.interval);
      await this.createOtp();
      this.textForm = document.getElementById(this.currentId);
      if (!this.errorGenerationOtp) {
        this.setOtpCountDown();
      }
      this.textForm.value = null;
      this.labelActive = false;
    },

    //EMETTO EVENTO PER CONTROLLARE OTP E CREARE TICKET
    async checkOtp() {
      let otp = this.selectedCode;

      let params = {
        richiesta: "EMAIL",
        otp: otp,
        cf: this.cf,
      };

     
      this.$emit("sendRequest", otp, this.interval);
    },

    //INIZIALIZZO COUNTDOWN PER OTP
    setOtpCountDown() {
      // Set the date we're counting down to

      let countDownTime = this.startTimer;
      this.interval = setInterval(function () {
        let minutes = Math.floor(
          (countDownTime % (1000 * 60 * 60)) / (1000 * 60)
        );
        let seconds = Math.floor((countDownTime % (1000 * 60)) / 1000);
        // Display the result in the element with id="demo"
        document.getElementById("timer").innerHTML = minutes + ": " + seconds;
        countDownTime = countDownTime - 1000;
        // If the count down is finished, write some text
        if (countDownTime < 0) {
          clearInterval(this.interval);
          document.getElementById("timer").innerHTML = "Codice scaduto!";
        }
      }, 1000);
    },

    //POSIZIONA LA LABEL PER PERMETTERE LA SCRITTURA
    activateLabel() {
      if (this.textForm?.value) {
        this.labelActive = true;
      } else {
        this.labelActive = !this.labelActive;
      }
    },
    input() {
      this.selectedCode = this.textForm.value;
    },
  },
};
</script>
