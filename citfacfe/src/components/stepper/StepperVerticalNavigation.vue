<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <nav class="d-flex flex-nowrap pt-4 w-100 justify-content-between stepper-vertical-navigation" >
    <button class="btn btn-outline-primary fw-bold me-4 btn-back"
            type="button" @click="$emit('on-cancel')">{{cancelLabel}}</button>
    <button v-if="submit" class="btn btn-primary fw-bold btn-next" type="submit"
            :form="formId">{{confirmLabel}}</button>
    <button v-else class="btn btn-primary fw-bold btn-next" type="button"
            @click="$emit('on-confirm')">{{confirmLabel}}</button>
  </nav>
</template>

<script>

// COMPONENTE CHE CONSENTE LA NAVIGAZIONE DELLO STEPPER VERTICALE
// PER UTILIZZARE IL COMPONENTE USARE I PROPS
//"formId" SE IL BOTTONE E' DENTRO UN FORM, L'ID DI ESSO
//"confirmLabel" LABEL DEL BOTTONE DI CONFERMA
//"submit" SE IL BOTTONE DI CONFERMA E' DI TIPO SUBMIT
//@on-confirm = AZIONE PER CONFERMA
//@on-cancel = AZIONE PER ANNULLAMENTO AZIONE

export default {
  name: "StepperVerticalNavigation",
  props:{
    formId:String,
    confirmLabel: String,
    submit:Boolean,
    cancelLabel:{type:String, default: 'Annulla'}
  }
}
</script>

