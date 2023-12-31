<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="csi-input" :class="{'invalid': error}">
    <div class="input-group">
      <div v-if="prepend && labelActive" class="input-group-prepend">
        <div class="input-group-text">
          <slot name="prepend"></slot>
        </div>
      </div>

      <input
        :id="currentId"
        :aria-label="ariaLabel"
        :class="{ 'text-uppercase': isUppercase }"
        :disabled="disabled"
        :maxlength="maxlength"
        :minlength="minlength"
        :name="currentId"
        :required="required"
        :step="step"
        :type="type"
        :value="modelValue"
        :max="max"
        class="form-control"
        @focusin="activateLabel"
        @focusout="activateLabel"
        @input="input"
        :aria-describedby="`${hintId} ${errorId}`"
        :aria-invalid="ariaInvalid"
      />
      <label :class="{ active: isLabelActive }" :for="currentId" class="form-label"
        >{{ label }}<sup v-if="required" aria-hidden="true">*</sup></label
      >
      <div v-if="(append || isClearable) && labelActive" class="input-group-append">
        <div class="input-group-text">
          <slot name="append" />
          <a v-if="isClearable" alt="azzera campo" @click="onClearInput">
       
          </a>
        </div>
      </div>
    </div>
    <small v-if="moreInfo" :id="hintId" class="form-text text-200">{{ moreInfo }}</small>
    <small v-if="error" :id="errorId" class="small text-danger">{{ errorMsg }}</small>
  </div>
</template>

<script>
// COMPONENTE PER INPUT TESTUALE LIBERO
// PER UTILIZZARE IL COMPONENTE USARE I PROPS
//"modelValue" valore dell'input
//label" TESTO DELLA LABEL DEL CAMPO INSERIMENTO,
//id" UN ID UNIVOCO PER IL COMPONENTE,
//moreInfo" EVENTUALE INFO AGGIUNTIVA DEL CAMPO,
//errorInfo" EVENTUALE MESSAGGIO DI ERRORE  DEL CAMPO,
//"error" TRUE SE IL VALORE INSERITO E' ERRATO
//isUppercase" SE TRUE IL TESTO è MAIUSCOLO,
//prefill" SE PRESENTE PRCOMPILA IL CAMPO
//"type" TIPO DI INPUT
//"maxlength" LUNGHEZZA MASSIMA
//"minlength" LUNGHEZZA MINIMA
//"prepend" ELEMENTO INSERITO PRIMA DELL'INPUT
//"append" ELEMENTO INSERITO DOPO DELL'INPUT
//"step" STEP dell'input
export default {
  name: 'CsiFloatInput',
  props: {
    modelValue: [String, Number],
    label: String,
    id: String,
    moreInfo: String,
    error: Boolean,
    errorMsg: String,
    isUppercase: Boolean,
    required: Boolean,
    prefill: String,
    type: { type: String, default: 'text' },
    maxlength: [String, Number],
    max: String,
    minlength: [String, Number],
    prepend: Boolean,
    append: Boolean,
    step: String,
    disabled: Boolean,
    clearable: Boolean,
    ariaInvalid: Boolean
  },
  emits: ['input', 'update:modelValue'],
  data() {
    return {
      text: null,
      currentId: null,
      isLabelActive: true
    }
  },
  async created() {
    this.currentId = 'inputText' + this.id
  },
  computed: {
    ariaLabel() {
      return `${this.label} ${this.modelValue ?? ''} ${this.moreInfo ?? ''}`
    },
    isClearable() {
      return !!this.modelValue && this.modelValue !== '' && this.clearable
    },
    attrs() {
      const { ...attrs } = this.$attrs
      return attrs
    },
    hintId() {
      return `hint-${this.id}`
    },
    errorId() {
      return `error-${this.id}`
    }
  },
  mounted() {},
  watch: {
    prefill: {
      immediate: true,
      handler(val) {
        if (val) {
          this.text = val
          if (this.isUppercase) {
            this.text = this.text?.toUpperCase()
          }
          this.$emit('update:modelValue', this.text ?? null)
          this.activateLabel()
        }
      }
    },
    modelValue: {
      immediate: true,
      handler(val) {
        this.text = val
        if (this.isUppercase) {
          this.text = this.text?.toUpperCase()
        }

        this.activateLabel()
      }
    }
  },
  methods: {
    //POSIZIONA LA LABEL PER PERMETTERE LA SCRITTURA
    activateLabel() {
      if (!!this.text || this.type === 'date') {
        this.isLabelActive = true
      } else {
        this.isLabelActive = !this.isLabelActive
      }
    },
    input(event) {
      this.text = event.target.value
      if (this.isUppercase) {
        this.text = this.text?.toUpperCase()
      }
      this.$emit('update:modelValue', this.text)
      this.$emit('input', this.text)
      this.activateLabel(event)
    },
    onClearInput() {
      this.$emit('update:modelValue', null)
      this.$emit('input', null)
    }
  }
}
</script>

