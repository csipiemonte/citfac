<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <component
    :aria-label="label"
    class="csi-button btn"
    :is="btnType"
    :class="classes"
    v-bind="$attrs"
  >
    <csi-icon  :src="icon" :color="color" v-if="icon"></csi-icon>

    <span class="btn-spinner spinner-border" v-if="loading">
      <span class="visually-hidden">Caricamento...</span>
    </span>
    <span v-else-if="!noLabel" class="btn-icon-label" :class="{ 'd-none d-lg-block': icon }"><slot></slot></span>
  </component>
</template>

<script>
/* 
 CUSTOM BUTTON
@type: tag usato per il bottone (può essere un button, link (a) o input)
@icon: in caso di bottone con icona
@noMinWidth: se non si vuole avere la lunghezza minima di default (120px)
@loading per visualizzare lo stato di caricamento

*/
import CsiIcon from '../core/CsiIcon.vue'
import CsiInnerLoading from '../core/CsiInnerLoading.vue'
const TYPES = {
  BUTTON: 'button',
  LINK: 'a',
  INPUT: 'input',
  ROUTER_LINK: 'router-link'
}

export default {
  name: 'CsiButton',
  components: { CsiIcon, CsiInnerLoading },
  props: {
    label: String,
    btnType: { type: String, default: TYPES.BUTTON },
    href: String,
    icon: String,
    noMinWidth: Boolean,
    loading: Boolean,
    outline: Boolean,
    color: { type: String, default: () => 'primary' },
    noLabel:Boolean
  },
  data() {
    return {
      TYPES
    }
  },
  mounted() {},
  computed: {
    classes() {
      let result = []

      if (this.outline) {
        result.push(`btn-outline-${this.color}`)
      } else {
        result.push(`btn-${this.color}`)
      }

      if (!this.noMinWidth) {
        result.push(`min-width`)
      }

      if(this.icon){
        result.push(`btn-icon`)
      }

      return result
    }
  }
}
</script>
