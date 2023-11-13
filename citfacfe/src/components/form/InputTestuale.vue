<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="form-group">
    <div :class="{ 'input-group': prepend }">
      <div v-if="prepend && labelActive" class="input-group-prepend">
        <div class="input-group-text">
          <slot name="prepend" />
        </div>
      </div>
      <label
        :for="currentId"
        style="pointer-events: none"
        :class="{ active: labelActive }"
        >{{ labelText }}</label
      >

      <input
        :class="{ 'text-uppercase': isUppercase }"
        :name="currentId"
        @focusout="activateLabel"
        @focusin="activateLabel"
        @input="input"
        type="text"
        class="form-control"
        :id="currentId"
        :maxlength="maxLenght"
        :readonly="readonly"
      />
    </div>
    <small class="form-text text-200" :id="currentId">{{ moreInfo }}</small>
  </div>
</template>

<script>
// COMPONENTE PER INPUT TESTUALE LIBERO
// PER UTILIZZARE IL COMPONENTE USARE I PROPS
//labelText" TESTO DELLA LABEL DEL CAMPO INSERIMENTO,
//id" UN ID UNIVOCO PER IL COMPONENTE,
//moreInfo" EVENTUALE INFO AGGIUNTIVA DEL CAMPO,
//isUppercase" SE TRUE IL TESTO è MAIUSCOLO,
//prefill" SE PRESENTE PRCOMPILA IL CAMPO
//@updateText" EMETTE IL VALORE AGGIORNATO DEL CAMPO
export default {
  name: "InputTestuale",
  props: {
    labelText: String,
    id: String,
    moreInfo: String,
    isUppercase: Boolean,
    prefill: String,
    prepend: String,
    maxLenght: Number,
    readonly: Boolean
  },
  data() {
    return {
      textForm: null,
      currentId: null,
      labelActive: false,
      currentValue: null,
    };
  },
  async created() {
    this.currentId = "inputText" + this.id;
  },
  computed: {},
  mounted() {
    this.textForm = document.getElementById(this.currentId);

    if (this.prefill && this.textForm) {
      this.textForm.value = this.prefill;
      this.currentValue = this.textForm.value;
      this.activateLabel();
    }
  },
  watch: {
    prefill() {
      if (this.textForm && this.prefill) {
        this.textForm.value = this.prefill;
        this.currentValue = this.textForm.value;
        this.activateLabel();
      }
    },
  },
  methods: {
    //POSIZIONA LA LABEL PER PERMETTERE LA SCRITTURA
    activateLabel() {
      if (this.textForm?.value) {
        this.labelActive = true;
      } else {
        this.labelActive = !this.labelActive;
      }
    },
    input() {
      this.currentValue = this.textForm.value;
      this.$emit("updateText", this.currentValue);
    },
  },
};
</script>
