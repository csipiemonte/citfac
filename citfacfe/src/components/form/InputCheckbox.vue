<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="form-check">
    <input
      :id="currentId"
      type="checkbox"
      class="form-control form-check-input"
      :required="required"
      @input="onInput"
    />
    <label  class="active fw-semibold" :for="currentId" >
      <small v-if="smallText">{{ labelText }}</small>
      <span v-else>{{ labelText }}</span>
      <div style="min-height: 25px">
        <small class="form-text small text-danger" aria-hidden="true" ><slot name="error"/></small>
      </div>

    </label>
  </div>

</template>

<script>
  // COMPONENTE PER CHECKBOX SEMPLICE
  // PER UTILIZZARE IL COMPONENTE USARE I PROPS
  //labelText" TESTO DELLA LABEL DELLA CHECKBOX,
  //ID : l'id del componente
  //modelvalue : il valore dell'input
  //smallText : Testo diventa piccolo
export default {
  name: "inputCheckbox",
  props: {
    labelText: String,
    id: String,
    required:Boolean,
    modelValue:[Boolean, String],
    smallText:Boolean
  },

  data() {
    return {
      currentId: null,
    };
  },

  async created() {
    this.currentId = "inputCheckbox" + this.id;
  },

  computed: {},
  mounted() {

  },
  methods: {
    onInput(event) {
      this.$emit("update:modelValue", event.target.checked);
    },
  },
};
</script>
