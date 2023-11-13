<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="csi-select">
    <div class="csi-select-wrapper">
      <label
      :class="{ active: labelActive }"
      :for="currentId"
      class="form-label"
      style="pointer-events: none"
      >{{ label }}<sup v-if="required">*</sup></label
    >

    <select
      :value="modelValue"
      :aria-label="ariaLabel"
      :id="currentId"
      :disabled="disabled"
      :required="required"
      @change="onSelect"
    >

      <option
        v-for="(option, index) in currentOptionList"
        :key="index"
        :id="option.id"
        :selected="option.isStarting"
        :value="option.id"
      >
        {{ option.name }}
      </option>
    </select>
    <small
      v-if="moreInfo || error"
      :id="currentId + 'hint'"
      :class="error ? 'text-danger' : 'text-200'"
      class="form-text"
      >{{ moreInfo }}</small
    >
    </div>

  </div>
</template>

<script>
export default {
  // COMPONENTE PER SELECT SINGOLA
  // PER UTILIZZARE IL COMPONENTE USARE I PROPS
  //label" TESTO DELLA LABEL DEL CAMPO INSERIMENTO,
  //id" UN ID UNIVOCO PER IL COMPONENTE,
  //disabled" PER INDICARE SE DISABILITATA
  //moreInfo" EVENTUALE INFO AGGIUNTIVA DEL CAMPO,
  //optionList" ARRAY DI OPZIONI {name: nomeOpzione, id: valoreOpzione}
  //@selectedOption" EMETTE IL VALORE AGGIORNATO DEL CAMPO
  name: 'CsiFloatSelect',
  props: {
    modelValue: { type: [String, Object], default: null },
    disabled: Boolean,
    optionList: Array,
    label: String,
    id: String,
    moreInfo: String,
    required: Boolean,
    optionValue: String,
    optionLabel: String
  },
  emits: ['on-select', 'update:modelValue'],
  data() {
    return {
      error: false,
      labelActive: true,
      selectedOption: null,
      currentId: 'inputSelect' + this.id,
      selectedOptionDesc: null
    }
  },
  watch: {
    optionList() {
      this.currentOptionList.unshift({
        showable: false
      })
    },
    modelValue: {
      immediate: true,
      handler(val) {
        this.selectedOption = val
        this.activateLabel()
      }
    }
  },
  computed: {
    hasValue() {
      return this.modelValue
    },
    ariaLabel() {
      let selectedOption = this.currentOptionList?.find(
        (option) => option.id === this.modelValue
      )?.name
      return `${this.label} ${selectedOption ?? ''} ${this.moreInfo ?? ''}`
    },
    //COPIA DI ELENCO OPZIONI DA MODIFICARE
    currentOptionList() {
      let array = [...this.optionList]
      array.forEach((a) => {
        a.showable = true
        if (this.optionValue && this.optionLabel) {
          a.id = a[this.optionValue]
          a.name = a[this.optionLabel]
        }

        if (a.id === this.prefill) {
          a.isStarting = true
        }
      })
      return array
    }
   
  },
  mounted() {
    //SELEZIONE L'ELEMENTO GRAFICO
    if (this.modelValue) {
      this.selectedOption = this.modelValue

    }
  },

  methods: {
    //POSIZIONA LA LABEL PER PERMETTERE LA SCRITTURA
    activateLabel() {
      if (this.modelValue && this.modelValue !== '') {
        this.labelActive = true
      } else {
        this.labelActive = !this.labelActive
      }
    },
    onSelect(event) {
      let option = event.target.value
      this.selectedOption = option
      this.$emit('update:modelValue', option)
      this.$emit('on-select', option)
    }
  }
}
</script>
