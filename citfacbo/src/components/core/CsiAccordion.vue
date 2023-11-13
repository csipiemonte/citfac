<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<!--// Da ragruppare sempre in una classe "cmd-accordion"-->
<template>
  <div class="accordion-item csi-accordion">
    <component :is="type" class="accordion-header">
      <button
        class="accordion-button csi-accordion__btn"
        :class="[{ collapsed: !isOpen }, headerClass]"
        type="button"
        :id="id"
        :aria-controls="targetId"
        :aria-expanded="isOpen"
        @click="toggleAccordion"
      >
        <slot name="header"></slot>
      </button>
    </component>
    <Transition name="slide">
      <div
        v-if="isOpen"
        :id="targetId"
        :aria-labelledby="id"
        class="accordion-collapse collapse"
        :class="{ show: isOpen }"
        role="region"
      >
        <div class="accordion-body" :class="{ 'p-0': noPadding }">
          <slot name="body"></slot>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script>
export default {
  name: 'CsiAccordion',
  props: {
    id: [String, Number],
    type: { type: String, default: 'h2' },
    noPadding: Boolean,
    headerClass: String,
    expandDefault: {type:Boolean, default:false}
  },
  data() {
    return {
      isOpen: this.expandDefault
    }
  },
  created(){
    
  },
  computed: {
    targetId() {
      return this.id + 'Target'
    },
   
  },

  methods: {
    toggleAccordion() {
      this.isOpen = !this.isOpen
    }
  }
}
</script>
