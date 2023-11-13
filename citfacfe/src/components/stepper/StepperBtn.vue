<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <button class="btn btn-sm" :class="classes" type="button">
    <svg class="icon icon-primary">
      <use v-if="backBtn" :xlink:href="$svgs.getSpriteSvgUrl('it-chevron-left')" ></use>
      <use v-else :xlink:href="$svgs.getSpriteSvgUrl('it-chevron-left')" ></use>
    </svg>
    <slot/>
  </button>

</template>

<script>
// COMPONENTE  CHE CONSENTE DI NAVIGARE SULLO STEPPER
// PER UTILIZZARE IL COMPONENTE USARE I PROPS
//"backBtn" NASCONDE O MOSTRA IL BOTTONE "INDIETRO"

export default {
  name: "StepperBtn",
  props:{
    backBtn:{type:Boolean, default: false},
  },
  computed:{
    classes(){
      let result = []
      if(this.backBtn){
        result.push("btn-outline-primary steppers-btn-prev")
      }else{
        result.push("btn-primary steppers-btn-confirm")
      }

      return result
    }
  },
  methods:{

  }
}
</script>
