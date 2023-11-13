<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="border user-contacts-card pt-3">
    <div class="container">
      <h3 class="mb-4">Contatti</h3>

      <div :class="{ 'form-group': !email }" class="mb-5">
        <label for="input-email">*Email</label>
        <div
          class="input-group align-items-end"
          @click="openEmailDialog"
          @keyup.enter="openEmailDialog"
          @keyup.space="openEmailDialog"
        >
          <input
            id="input-email"
            :value="email"
            class="form-control-plaintext col"
            name="input-email"
            readonly
            type="text"
            aria-label="inserisci o modifica l'indirizzo email"
          />
          <button type="button" aria-hidden="true" class="input-group-text">
            <svg class="icon icon-sm icon-primary">
              <use :href="email ? fieldIconFilled : fieldIconEmpty"></use>
            </svg>
          </button>
        </div>
      </div>
      <div :class="{ 'form-group': !telephone }" class="mb-5">
        <label for="input-telephone">Telefono</label>
        <div class="input-group align-items-end">
          <input
            id="input-telephone"
            :value="telephoneLabel"
            class="form-control-plaintext col"
            name="input-telephone"
            readonly
            type="text"
            @click="openTelephoneDialog"
            @keyup.enter="openTelephoneDialog"
            @keyup.space="openTelephoneDialog"
            title="mostra finestra telefono"
          />
          <button
            type="button"
            aria-hidden="true"
            class="input-group-text"
            @click="openTelephoneDialog"
            @keyup.enter="openTelephoneDialog"
            @keyup.space="openTelephoneDialog"
            aria-label="inserisci o modifica il numero di telefono"
          >
            <svg class="icon icon-sm icon-primary">
              <use :href="telephone ? fieldIconFilled : fieldIconEmpty"></use>
            </svg>
          </button>

          <button
            v-if="telephone"
            type="button"
            aria-hidden="true"
            class="input-group-text"
            @click="onDeleteTelephone"
            @keyup.enter="onDeleteTelephone"
            @keyup.space="onDeleteTelephone"
            aria-label="cancella il numero di telefono"
          >
            <svg class="icon icon-sm icon-danger ">
              <use :href="fieldDeleteIcon"></use>
            </svg>
          </button>
        </div>
      </div>

      <template v-if="hasContacts">
        <div class="form-group mb-5">
          <label for="input-terms">Termini e condizioni d'uso</label>
          <div
            class="input-group align-items-end"
            data-bs-target="#modal-user-notify-terms"
            data-bs-toggle="modal"
          >
            <input
              id="input-terms"
              :value="terms"
              class="form-control-plaintext col"
              name="input-terms"
              readonly
              type="text"
            />
            <span aria-hidden="true" class="input-group-text">
              <svg class="icon icon-sm icon-primary">
                <use :href="fieldIconEmpty"></use>
              </svg>
            </span>
          </div>
        </div>

        <div class="mb-4">
          <button
            class="btn btn-outline-primary"
            data-bs-toggle="modal"
            data-bs-target="#modal-user-notify-cancel"
          >
            ELIMINA CONTATTI
          </button>
        </div>
      </template>
    </div>

    <email-form @email-confirmed="onEmailConfirmed"></email-form>
    <telephone-form
      @telephone-confirmed="onTelephoneConfirmed"
    ></telephone-form>
    <user-notify-terms-modal />
    <user-contacts-cancel-modal />
  </div>
</template>

<script>
import StepperVertical from "../../stepper/StepperVertical.vue";
import StepperVerticalItem from "../../stepper/StepperVerticalItem.vue";
import EmailForm from "./EmailForm.vue";
import TelephoneForm from "./TelephoneForm.vue";
import UserNotifyTermsModal from "./UserNotifyTermsModal.vue";
import UserContactsCancelModal from "./UserContactsCancelModal.vue";
import { MOBILE_PHONE_PREFIX_ITALY } from "../../../services/config";

export default {
  name: "UserContactsCard",
  components: {
    UserContactsCancelModal,
    UserNotifyTermsModal,
    TelephoneForm,
    EmailForm,
    StepperVerticalItem,
    StepperVertical,
  },
  props: {
    telephone: [Number, String],
    email: String,
    hasContacts: Boolean,
  },
  data() {
    return {
      terms: "",
    };
  },
  created() {},
  computed: {
    fieldIconEmpty() {
      return this.$svgs.getSpriteSvgUrl("it-chevron-right");
    },
    fieldIconFilled() {
      return this.$svgs.getSpriteSvgUrl("it-pencil");
    },
    fieldDeleteIcon() {
      return this.$svgs.getSpriteSvgUrl("it-delete");
    },
    telephoneLabel() {
      if (!this.telephone) return "";
      let pattern = "^(" + MOBILE_PHONE_PREFIX_ITALY + ")+";
      let regex = new RegExp(pattern, "g");
      return this.telephone.replace(regex, "");
    },
  },
  methods: {
    onEmailConfirmed(email) {
      this.$emit("on-update-email", email);
    },
    onTelephoneConfirmed(telephone) {
      this.$emit("on-update-telephone", telephone);
    },
    onDeleteTelephone() {
      this.$emit("on-update-telephone", null);
    },
    openTelephoneDialog() {
      let myModal = new bootstrap.Modal(
        document.getElementById("modal-telephone"),
        {}
      );
      myModal.show();
    },
    openEmailDialog() {
      let myModal = new bootstrap.Modal(
        document.getElementById("modal-email"),
        {}
      );
      myModal.show();
    },
  },
};
</script>


