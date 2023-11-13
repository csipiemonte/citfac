<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <li :class="classes"
  >{{ item.name }}
    <template v-if="confirmed">
      <svg class="icon steppers-success">
        <use 
        :xlink:href="$svgs.getSpriteSvgUrl('it-check')"
       ></use>
      </svg>
      <span class="visually-hidden">Confermato</span>
    </template>
    <template v-if="active">
      <span class="visually-hidden">Attivo</span>
    </template>


  </li>
</template>

<script>
// COMPONENTE  DEL SINGOLO STEP (DA USARE SEMPRE DENTRO STEPPER)
// PER UTILIZZARE IL COMPONENTE USARE I PROPS
//"active" INDICA SE LO STEP E' ATTIVO
//"confirmed" INDICA SE LO STEP E' CONFERMATO
//"item " DATI DELLO STEP {'NAME', "SUBTITLE"}.. EVENTUALMENTE DA IMPLEMENTARE IN CASO DI NECESSITA'

export default {
  name: "StepperItem",
  props: {
    active: {type: Boolean, default: false},
    confirmed: {type: Boolean, default: false},
    item: {type: Object, default: null}
  },
  computed: {
    classes() {
      let result = []
      if (this.confirmed) result.push("confirmed");
      if (this.active) result.push("active");
      return result;
    }
  }
}
</script>

