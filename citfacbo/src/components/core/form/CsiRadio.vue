<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="csi-radio">
    <label class="radio-inline form-label me-4">{{ label }}</label>
    <div class="form-check form-check-inline" v-for="(option, index) in options" :key="index">
      <input
        class="form-check-input"
        type="radio"
        :name="name"
        :id="currentId + index"
        :value="option.value"
        @input="onInput"
        v-model="selectedOption"
      />
      <label class="form-check-label" :for="currentId + index">{{ option.label }}</label>
    </div>
  </div>
</template>

<script>
export const OPTIONS = [
  { label: 'Si', value: true },
  { label: 'No', value: false }
]

export default {
  // COMPONENTE PER SELECT SINGOLA
  // PER UTILIZZARE IL COMPONENTE USARE I PROPS
  //label" TESTO DELLA LABEL DEL CAMPO INSERIMENTO,
  //id" UN ID UNIVOCO PER IL COMPONENTE,
  //disabled" PER INDICARE SE DISABILITATA
  //moreInfo" EVENTUALE INFO AGGIUNTIVA DEL CAMPO,
  //optionList" ARRAY DI OPZIONI {name: nomeOpzione, id: valoreOpzione}
  //@selectedOption" EMETTE IL VALORE AGGIORNATO DEL CAMPO
  name: 'CsiRadio',
  props: {
    modelValue: { type: [String, Number, Boolean], default: null },
    disabled: Boolean,
    booleanOptions: Boolean,
    options: { type: Array, default: () => OPTIONS },
    label: String,
    name: String,
    id: String,
    moreInfo: String,
    required: Boolean,
    ariaInvalid: Boolean
  },
  emits: ['on-select', 'update:modelValue'],
  data() {
    return {
      currentId: 'csiRadio' + this.id,
      selectedOption:null
    }
  },

  created() {
    this.selectedOption = this.modelValue
  },
  computed: {
    
  },

  methods: {
    //POSIZIONA LA LABEL PER PERMETTERE LA SCRITTURA
    onInput(event) {
      let value = event.target.value
      if (this.booleanOptions) {
        value = value === 'true'
      }
      this.$emit('update:modelValue', value)
    }
  }
}
</script>
