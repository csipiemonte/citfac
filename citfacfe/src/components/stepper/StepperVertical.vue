<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div  class="steppe-vertical d-flex flex-column  ml-2">
    <slot ></slot>
  </div>
</template>

<script>
import StepperVerticalItem from "./StepperVerticalItem.vue";
// COMPONENTE "STEPPER VERTICALE"
// PER UTILIZZARE IL COMPONENTE USARE I PROPS
//"step" STEP ATTIVO


export default {
  name: "StepperVertical",
  components: {StepperVerticalItem},
  props: {
    step: {type: Number, default: 0}
  },

  created() {

  }
}
</script>

