<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div id="cf-stepper" :class="{ 'single-step': singleStep }" class="cf-stepper steppers">
    <div class="steppers-header">
      <ul>
        <stepper-item
          v-for="stepItem in steps"
          :key="stepItem.id"
          :active="modelValue === stepItem.id"
          :confirmed="modelValue > stepItem.id"
          :item="stepItem"
        />
      </ul>
      <span class="steppers-index" aria-hidden="true"
        >{{ modelValue }}/{{ stepsLength }}</span
      >
    </div>
    <div aria-live="polite" class="steppers-content">
      <slot :name="modelValue"></slot>
    </div>
  </div>
</template>

<script>
import StepperItem from "./StepperItem.vue";
// COMPONENTE "STEPPER ORIZZONTALE"
// PER UTILIZZARE IL COMPONENTE USARE I PROPS
//"steps" LISTA DI STEPS // {name: '', id: ''}
//"modelValue" STEP ATTIVO
//@next = STEP SUCCESSIVO
//@prev = STEP PRECEDENTE

export default {
  name: "Stepper",
  components: { StepperItem },
  props: {
    steps: { type: Array, default: () => [] }, // {name: '', id: ''}
    modelValue: [String, Number],
    singleStep: Boolean,
  },
  watch: {
    modelValue() {
      document.getElementById("cf-stepper").scrollIntoView();
    },
  },
  data() {
    return {};
  },
  created() {},
  computed: {
    stepsLength() {
      return this.steps?.length ?? 0;
    },
  },
  methods: {
    isActive(id) {
      return this.modelValue === id;
    },
    next() {
      this.$emit("update:modelValue", this.modelValue + 1);
    },
    prev() {
      this.$emit("update:modelValue", this.modelValue - 1);
    },
  },
};
</script>


