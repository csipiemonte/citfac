<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="form-group my-4">
    <div class="input-group">
      <div v-if="prepend" class="input-group-prepend">
        <div class="input-group-text">
          <slot name="prepend"/>
        </div>
      </div>

      <label :class="{ 'active ': labelActive }" :for="currentId">{{
          labelText
        }}<sup v-if="required">*</sup></label>
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
          :readonly="readonly"
          class="form-control"
          @focusin="activateLabel"
          @focusout="activateLabel"
          @input="input"
      />

      <div v-if="(append || isClearable) && labelActive" class="input-group-append">
        <div class="input-group-text">
          <slot name="append"/>
          <a v-if="isClearable" alt="azzera campo" @click="onClearInput">
            <svg  class="icon icon-sm"><use 
              :xlink:href="$svgs.getSpriteSvgUrl('it-close-big')"
             ></use></svg>
          </a>

        </div>


      </div>
      <slot name="button"/>
     
    </div>
    <small v-if="moreInfo" :id="currentId" class="form-text text-200">{{ moreInfo }}</small>
    <small v-if="error" :id="currentId" class="small text-danger">{{ errorInfo }}</small>
  </div>
</template>

<script>

// COMPONENTE PER INPUT TESTUALE LIBERO
// PER UTILIZZARE IL COMPONENTE USARE I PROPS
//"modelValue" valore dell'input
//labelText" TESTO DELLA LABEL DEL CAMPO INSERIMENTO,
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
  name: "InputText",
  props: {
    modelValue: [String, Number], 
    labelText: String,
    id: String,
    moreInfo: String,
    errorInfo: String,
    error: Boolean,
    isUppercase: Boolean,
    required: Boolean,
    prefill: String,
    type: {type: String, default: 'text'},
    maxlength: [String, Number],
    max: String,
    minlength: [String, Number],
    prepend: Boolean,
    append: Boolean,
    step: String,
    disabled: Boolean,
    clearable: Boolean,
    readonly:Boolean
  },
  emits:['input', 'update:modelValue'],
  data() {
    return {
      text: null,
      currentId: null,
      labelActive: false,
    };
  },
  async created() {

    this.currentId = "inputText" + this.id;
  },
  computed: {
    ariaLabel() {
      return `${this.labelText} ${this.modelValue ?? ''} ${this.moreInfo ?? ''}`

    },
    isClearable(){
      return !!this.modelValue && this.modelValue !== '' && this.clearable
    },
    attrs() {
      const {...attrs} = this.$attrs;
      return attrs;
    },

  },
  mounted() {


  },
  watch: {
    prefill: {
      immediate: true,
      handler(val, oldVal) {
        if (!!val) {
          this.text = val
          if(this.isUppercase){
            this.text = this.text?.toUpperCase()
          }
          this.$emit('update:modelValue', this.text ?? null)
          this.activateLabel()
        }

      }
    },
    modelValue:{
      immediate: true,
      handler(val, oldVal) {
        this.text = val
        if(this.isUppercase){
          this.text = this.text?.toUpperCase()
        }

        this.activateLabel()
      }

    }
  },
  methods: {

    //POSIZIONA LA LABEL PER PERMETTERE LA SCRITTURA
    activateLabel() {

      this.labelActive = !!this.text || this.type==="date"

    },
    input(event) {
      this.text = event.target.value
      if(this.isUppercase){
        this.text = this.text?.toUpperCase()
      }
      this.$emit('update:modelValue', this.text)
      this.$emit('input', this.text)
    },
    onClearInput(){
      this.$emit('update:modelValue', null)
      this.$emit('input',null)
    }
  },
}
</script>


