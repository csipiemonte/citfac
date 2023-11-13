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
      >{{ labelText }}</label
    >

    <textarea
      class="form-control border-1"
      @focusout="activateLabel"
      @focusin="activateLabel"
      :id="currentId"
      cols="600"
      maxlength="600"
      @input="input"
    ></textarea>
    <small :id="currentId" class="form-text text-200">{{ moreInfo }}</small>
  </div>
</template>

<script>
// COMPONENTE PER INPUT TESTUALE AD AREA
// PER UTILIZZARE IL COMPONENTE USARE I PROPS
//labelText" TESTO DELLA LABEL DEL CAMPO INSERIMENTO,
//id" UN ID UNIVOCO PER IL COMPONENTE,
//moreInfo" EVENTUALE INFO AGGIUNTIVA DEL CAMPO,
//@updateTextArea" EMETTE IL VALORE AGGIORNATO DEL CAMPO
export default {
  name: "inputTextArea",
  props: {
    labelText: String,
    id: [String, Number],
    moreInfo: String,
    prefill: String,
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
    this.currentId = "inputTextArea" + this.id;
  },

  computed: {},
  mounted() {
    this.textForm = document.getElementById(this.currentId);

    if (this.prefill) {
      this.textForm.value = this.prefill;
      this.currentValue = this.textForm.value;
      this.activateLabel();
    }
  },
  watch: {
    prefill() {
      if (this.prefill && this.textForm) {
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
      this.$emit("updateTextArea", this.textForm.value);
    },
  },
};
</script>
