<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <cf-modal
    id="modal-telephone"
    ref="modalTelephone"
    title="Inserisci numero di telefono mobile"
    @on-hide="onHideModal"
  >
    <stepper-vertical :step="activeStep">
      <stepper-vertical-item :name="STEPS.INSERT" title="Numero di telefono">
        <cf-alert title="Attenzione!">
          <p class="">
            Riceverai un codice via SMS per confermare il numero di telefono
            mobile.
          </p>
          <small class="mb-0"
            >Hai a disposizione
            <strong>
              {{
                attemptsLeft === 1
                  ? `1 tentatitvo`
                  : `${attemptsLeft} tentativi`
              }}
            </strong>
          </small>
        </cf-alert>

        <Transition>
          <div
            v-if="attemptsLeft !== undefined && attemptsLeft <= 0"
            class="alert alert-danger my-3"
            role="alert"
          >
            <p class="mb-0">
              Per oggi non puoi più ricevere il codice via SMS, puoi riprovare
              domani.
            </p>
          </div>
        </Transition>

        <template v-if="attemptsLeft > 0">
          <input-text
            id="telephone"
            v-model="telephone"
            :error="telephoneErr"
            :error-info="telephoneErrorInfo"
            label-text="Numero di telefono"
            maxlength="10"
            minlength="9"
            required
            type="tel"
            prepend
          >
            <template #prepend>+39</template>
          </input-text>

          <stepper-vertical-navigation
            confirm-label="Conferma e invia"
            form-id="form-telephone"
            @on-cancel="endStepper"
            @on-confirm="onConfirmTelephone"
          />
        </template>
      </stepper-vertical-item>

      <stepper-vertical-item :name="STEPS.VERIFY" title="Verifica">
        <check-otp-form
          :telephone="telephone"
          @otp-verified="onTelephoneVerified"
          @on-cancel="endStepper"
          @on-back="stepPrev"
        >
        </check-otp-form>
      </stepper-vertical-item>

      <stepper-vertical-item :name="STEPS.END" last title="Fine">
        <div class="w-100">
          <cf-alert title="Attenzione!">
            <p class="mb-0">
              Verifica completata! <br />
              Il tuo numero di telefono <strong>{{ telephone }}</strong> è stato
              verificato
            </p>
          </cf-alert>

          <stepper-vertical-navigation
            confirm-label="Fine"
            form-id="check-otp-form"
            @on-cancel="endStepper"
            @on-confirm="telephoneConfirmed"
          />
        </div>
      </stepper-vertical-item>
    </stepper-vertical>
  </cf-modal>
</template>

<script>
import StepperVertical from "../../stepper/StepperVertical.vue";
import StepperVerticalItem from "../../stepper/StepperVerticalItem.vue";
import InputText from "../../form/InputText.vue";
import CfModal from "../../generic/CfModal.vue";
import CheckOtpForm from "../../form/CheckOtpForm.vue";
import StepperVerticalNavigation from "../../stepper/StepperVerticalNavigation.vue";
import CfAlert from "../../generic/CfAlert.vue";
import { getAttemptsLeft } from "../../../services/api";

import { MOBILE_PHONE_PREFIX_ITALY } from "../../../services/config";

const STEPS = {
  INSERT: 1,
  VERIFY: 2,
  END: 3,
};
// COMPONENTE CHE CONSENTE DI INSERIRE UN NUOVO NUMERO DI TELEFONO
// PER UTILIZZARE IL COMPONENTE USARE I PROPS
//@telephone-confirmed = RESTITUISCE IL NUOVO NUMERO VERIFICATO

export default {
  name: "TelephoneForm",
  components: {
    StepperVerticalItem,
    StepperVertical,
    CfModal,
    CfAlert,
    CheckOtpForm,
    InputText,
    StepperVerticalNavigation,
  },
  data() {
    return {
      STEPS,
      activeStep: STEPS.INSERT,
      telephone: "",
      telephoneValidation: null,
      attemptsLeft: 0,
      telephoneErr: false,
      telephoneErrorInfo: "",
      onClickTelephoneValidation: false,
    };
  },
  async created() {
    this.isLoading = true;
    try {
      let params = { cf: this.userTaxCode };
      //VERIFICHIAMO IL NUMERO MASSIMO DI TENTATIVI RIMASTI
      let { data } = await getAttemptsLeft({ params });
      this.attemptsLeft = data.attemptsLeft;
    } catch (e) {
      await this.$store.dispatch("setNotificationInfo", {
        body: "Non è stato possibile caricare il numero di SMS giornalieri disponibili",
      });
    }
    this.isLoading = false;
  },
  watch: {
    telephone() {
      let isValid = this.setTelephoneValidation();
    },
  },
  computed: {
    userTaxCode() {
      let user = this.$store.getters["getUser"];
      return user?.codFisc ?? null;
    },
  },

  methods: {
    endStepper() {
      let modal = this.$refs.modalTelephone;

      modal.onHide();
    },
    onConfirmTelephone() {
      const isValid = this.setTelephoneValidation();
      this.telephoneErr = !isValid;
      if (isValid) {
        this.stepNext();
      }
    },
 
    setTelephoneValidation() {
      const input = document.getElementById("input-telephone");
      const validityState = input.validity;
      const NUMERIC_REGEX = /^[0-9]*$/;
      let isNumeric = NUMERIC_REGEX.test(this.telephone);
      this.onClickTelephoneValidation = true;
      if (this.onClickTelephoneValidation) {
        let errorMsg = "";
        input.reportValidity();
        if (validityState.valueMissing) {
          errorMsg = "Campo obbligatorio";
        } else if (validityState.tooShort) {
          errorMsg = "Deve essere composto da almeno 9 cifre";
        } else if (validityState.tooLong) {
          errorMsg = "Deve essere composto da massimo 10 cifre";
        } else if (!isNumeric) {
          errorMsg = "Deve contenere solo numeri";
        }

        let isValid = validityState.valid && isNumeric;
        this.telephoneErrorInfo = errorMsg;
        if (!isValid) {
          input.setCustomValidity(this.telephoneErrorInfo);
        }
        return isValid;
      }
    },
    stepNext() {
      this.activeStep++;
    },
    onTelephoneVerified() {
      this.stepNext();
    },
    telephoneConfirmed() {
      this.$emit(
        "telephone-confirmed",
        MOBILE_PHONE_PREFIX_ITALY + this.telephone
      );
      this.endStepper();
    },
    stepPrev() {
      this.activeStep--;
    },
    onHideModal() {
      this.activeStep = STEPS.INSERT;
      this.telephone = "";
      (this.telephoneErrorInfo = ""), (this.telephoneErr = false);
    },
  },
};
</script>

