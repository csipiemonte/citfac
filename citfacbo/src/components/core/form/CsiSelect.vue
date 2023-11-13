<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="csi-select">
    <div class="csi-select-wrapper">
      <label :for="currentId" class="form-label">{{ label }}<sup v-if="required">*</sup></label>

      <select
        :value="modelValue"
        :aria-label="label"
        :id="currentId"
        :disabled="disabled"
        :required="required"
        :aria-describedby="currentId + 'hint'"
        :aria-invalid="ariaInvalid"
        @change="onSelect"
      >
        <option v-if="clearable" selected="selected" value=""></option>

        <option
          v-for="(option, index) in currentOptionList"
          :key="index"
          :id="option.id"
          :selected="option.isStarting"
          :value="option.id"
          :disabled="option.disabled"
        >
          {{ option.name }}
        </option>
      </select>
      <small
        v-if="moreInfo || error"
        :id="currentId + 'hint'"
        :class="error ? 'text-danger' : 'small'"
        class="form-text small"
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
  name: 'CsiSelect',
  props: {
    modelValue: { type: [String, Number, Object], default: null },
    disabled: Boolean,
    optionList: { type: Array, default: () => [] },
    label: String,
    id: String,
    moreInfo: String,
    required: Boolean,
    optionValue: { type: String, default: 'id' },
    optionLabel: { type: String, default: 'name' },
    emitObject: Boolean,
    ariaInvalid: Boolean,
    clearable: Boolean
  },
  emits: ['on-select', 'update:modelValue'],
  data() {
    return {
      error: false,
      labelActive: true,
      currentId: 'inputSelect' + this.id,
      selectedOptionDesc: null
    }
  },

  created() {},
  computed: {
    //COPIA DI ELENCO OPZIONI DA MODIFICARE
    currentOptionList() {
      let list = JSON.parse(JSON.stringify(this.optionList))
      let newList = list.map((item) => {
        if (this.optionValue && this.optionLabel) {
          item.id = item[this.optionValue]
          item.name = item[this.optionLabel]
        }

        return item
      })

      return newList
    }
  },

  methods: {
    //POSIZIONA LA LABEL PER PERMETTERE LA SCRITTURA
    onSelect(event) {
      let option = event.target.value
      if (this.emitObject) {
        let optionObject = this.optionList?.find((o) => o[this.optionValue].toString() === option)
        this.$emit('on-select', optionObject)
      } else {
        this.$emit('on-select', option)
      }

      this.$emit('update:modelValue', option)
    }
  }
}
</script>
