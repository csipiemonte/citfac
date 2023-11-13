<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="form-group">
    <label
        :for="currentId"
        style="pointer-events: none"
        :class="{ active: labelActive }"
        class="pb-2"
    >{{ labelText }}<sup v-if="required">*</sup></label
    >
    <textarea
        :value="modelValue"
        class="form-control border-1"
        @focusout="activateLabel"
        @focusin="activateLabel"
        :id="currentId"
        :name="currentId"
        :cols="cols"
        :maxlength="maxlength"
        @input="input"
        :required="required"
        :aria-label="ariaLabel"
    >

    </textarea>
    <div v-if=" isClearable && labelActive" class="input-group-append">
      <div class="input-group-text">
        <a v-if="isClearable" alt="azzera campo" @click="onClearInput">
          <svg  class="icon icon-sm"><use 
            :xlink:href="$svgs.getSpriteSvgUrl('it-close-big')"
         ></use></svg>
        </a>

      </div>


    </div>
    <small v-if="moreInfo" :id="currentId" class="form-text text-200" >{{ moreInfo }}</small>
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
//"maxlength" LUNGHEZZA MASSIMA



export default {
  name: "InputTextarea",
  props: {
    modelValue:[String, Number],
    labelText: String,
    id: String,
    moreInfo: String,
    errorInfo:String,
    error: Boolean,
    required: Boolean,
    maxlength:[String, Number],
    cols:[String, Number],
    clearable: Boolean
  },
  data() {
    return {
      text: null,
      currentId: null,
      labelActive: false,
    };
  },
  async created() {
    this.currentId = "inputTextarea" + this.id;
    this.text= this.modelValue
  },
  computed: {

    attrs() {
      const { ...attrs } = this.$attrs;
      return attrs;
    },
    ariaLabel() {
      return `${this.labelText} ${this.modelValue ?? ''} ${this.moreInfo ?? ''}`

    },
    isClearable(){
      return !!this.modelValue && this.modelValue !== '' && this.clearable
    },
  },

  mounted() {
    this.activateLabel();

  },

  methods: {

    //POSIZIONA LA LABEL PER PERMETTERE LA SCRITTURA
    activateLabel() {
      this.labelActive = !!this.text
    },
    input(event) {
      this.text = event.target.value
      this.$emit('update:modelValue', this.text)
    },
    onClearInput(){
      this.$emit('update:modelValue', null)
    }
  },
}
</script>

