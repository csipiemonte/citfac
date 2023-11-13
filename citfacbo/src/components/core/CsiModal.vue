<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <Teleport to="body">
    <Transition name="modal">
    <div class="modal-backdrop" v-if="modelValue">
      <div
        class="csi-modal"
        :id="id"
        tabindex="-1"
        :aria-labelledby="titleId"
        aria-hidden="true"
        :class="[maximized ? 'modal-fullscreen' : 'modal-fullscreen-sm-down']"
      >
        <div class="modal-content">
          <div name="header" class="modal-header">
            <h1 class="modal-title " :id="titleId">{{ title }}</h1>
            <button type="button" class="btn-close" @click="hide" aria-label="Chiudi"></button>
          </div>

          <div class="modal-body py-4">
            <slot></slot>
          </div>

          <div class="modal-footer" v-if="!noAction">
            <button type="button" class="btn btn-outline-primary" @click="hide">
              {{ cancelLabel }}
            </button>
            <csi-button type="button"  :class="confirmBtnClass" @click="onConfirm" :loading="loading">
              {{ confirmLabel }}
            </csi-button>
          </div>
        </div>
      </div>
    </div>
  </Transition>
  </Teleport>
 
</template>

<script>
import CsiButton from './CsiButton.vue'
/* COMPONENTE FINESTRA MODALE CUSTOM
- title: TITOLO DELLA MODALE
- id UN ID UNIVOCO PER IL COMPONENTE,
- maximized: FINESTRA A SCHERMO INTERO
- noAction: nascondi azioni del footer
-cancelLabel : label da utilizzare nel bottone "annulla"
-confirmLabel : label da utilizzare nel bottone "conferma"
-modelValue : gestisce la visualizzazione della modale

*/
export default {
  components: { CsiButton },
  name: 'CsiModal',
  props: {
    title: String,
    id: String,
    maximized: Boolean,
    noAction: Boolean,
    cancelLabel: { type: String, default: 'Annulla' },
    confirmLabel: { type: String, default: 'Conferma' },
    confirmBtnClass: {type:String, default: "btn-primary"},
    modelValue: Boolean,
    loading:Boolean
  },
  emits: ['update:modelValue', 'onConfirm', 'onHide'],
  data() {
    return {
      showModalContent: false
    }
  },
  computed: {
    titleId() {
      return this.id + '-title'
    }
  },
  watch: {
    modelValue: {
      immediate: true,
      handler(val) {
        if (val) {
          document.body.classList.add('modal-open')
        } else {
          document.body.classList.remove('modal-open')
        }
      }
    }
  },
  mounted() {},

  methods: {
    hide() {
  
      this.$emit('update:modelValue', false),
      this.$emit('onHide', false)
    },
    onConfirm() {
      this.$emit('onConfirm')
     
    }
  }
}
</script>
