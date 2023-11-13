<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="form-group mb-4">
    <label
        :class="{ active: labelActive }"
        :for="currentId"
        style="pointer-events: none"
    >{{ labelText }}<sup v-if="required">*</sup></label
    >
    <div class="select-wrapper">
 
      <select
          v-model="selectedOption"
          :aria-label="ariaLabel"
          :id="currentId"
          :disabled="disabled"
          :required="required"
          class="form-control fw-bold"
          @focusin="activateLabel(false)"
          @focusout="activateLabel(false)"
          @change="onSelect"
      >
        <option v-if="clearable" selected="selected" value=""></option> 

          <option
              v-for="(option, index) in currentOptionList" :key="index"
              :id="option.id"
              :selected="option.isStarting"
              :value="option.id"
          >
            {{ option.name }}
          </option>



      </select>
      <small v-if="moreInfo || error" :id="currentId+'hint'" :class="error ? 'text-danger' : 'text-200'" class="form-text ">{{
          moreInfo
        }}</small>
    </div>
  </div>
</template>

<script>


export default {
  // COMPONENTE PER SELECT SINGOLA
  // PER UTILIZZARE IL COMPONENTE USARE I PROPS
  //labelText" TESTO DELLA LABEL DEL CAMPO INSERIMENTO,
  //id" UN ID UNIVOCO PER IL COMPONENTE,
  //disabled" PER INDICARE SE DISABILITATA
  //moreInfo" EVENTUALE INFO AGGIUNTIVA DEL CAMPO,
  //optionList" ARRAY DI OPZIONI {name: nomeOpzione, id: valoreOpzione}
  //@selectedOption" EMETTE IL VALORE AGGIORNATO DEL CAMPO
  name: "CfSelect",
  props: {
    modelValue: {type:[String, Object], default: null},
    disabled: Boolean,
    optionList: Array,
    labelText: String,
    id: String,
    moreInfo: String,
    prefill: Number,
    required: Boolean,
    clearable:Boolean,
    optionValue:String,
    optionLabel:String

  },
  emits:['on-select', "update:modelValue"],
  data() {
    return {

      error:false,
      labelActive: false,
      selectedOption: null,
      currentId: "inputSelect" + this.id,
      selectedOptionDesc: null,

    };
  },
  watch: {
    optionList() {
      this.currentOptionList.unshift({
        showable: false,
      });

    },
    prefill() {
      if (!!this.prefill) {
        this.selectedOption = this.prefill
        this.$emit("update:modelValue", this.prefill)
        this.activateLabel();
      }
    },
    modelValue(){
      if(!this.modelValue){
        this.selectedOption = null
        this.activateLabel()
      }
    }

  },
  computed: {
    hasValue(){
      return this.modelValue
    },
    ariaLabel(){
      let selectedOption = this.currentOptionList?.find(option => option.id === this.modelValue)?.name
      return `${this.labelText} ${selectedOption ?? ''} ${this.moreInfo ?? ''}`

    },
    //COPIA DI ELENCO OPZIONI DA MODIFICARE
    currentOptionList() {
      let array = [...this.optionList];
      array.forEach((a) => {
        a.showable = true;
        if(this.optionValue && this.optionLabel){
          a.id = a[this.optionValue]
          a.name = a[this.optionLabel]
        }


        if (a.id === this.prefill) {
          a.isStarting = true;
        }

        
      });
      return array;
    },

  },
  mounted() {
    //SELEZIONE L'ELEMENTO GRAFICO
    if (this.modelValue) {

      this.selectedOption=this.modelValue
      this.activateLabel(false,'mounted');
    }
  },

  methods: {
    //POSIZIONA LA LABEL PER PERMETTERE LA SCRITTURA
    activateLabel(isChangeOption) {
      if (this.prefill) {
        this.labelActive = true;

        return;
      }
      if (!!this.modelValue) {
        this.labelActive = true;
      } else {
        this.labelActive = !this.labelActive;
      }

      if (isChangeOption) {
        this.labelActive = false;
      }
    },
    onSelect(event) {
      let option = event.target.value
      this.selectedOption = option
      this.$emit("update:modelValue", option)
      this.$emit("on-select", option)
    },

  },
}
</script>

