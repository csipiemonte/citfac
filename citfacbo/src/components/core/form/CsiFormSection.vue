<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <!--        COLORE SCURO -->
  <div class="bg-gray-200 pb-2 csi-form-section">
    <div class="column p-4">
      <div
        class="col pb-3"
        :class="[{ 'accordion-title': accordion }, { 'open ': isOpen }]"
        @click="toggleAccordion"
        v-if="title"
      >
        <h3 class="csi-h3 col">{{ title }}</h3>
      </div>

      <!--            COLORE CHIARO-->
      <Transition name="slide">
      <div  v-if="isOpen" class="col bg-white px-3 pb-4" :class="{ 'pt-5': !noPadding }">
        <div class="row">
          <slot></slot>
        </div>
      </div>
      </Transition>
    </div>
  </div>
</template>

<script>
//COMPONENTE PER AVERE SFONDO E SPAZIATURE DEL FORM
// PER UTILIZZARE IL COMPONENTE USARE I PROPS
//title" TITOLO DEL FORM,
export default {
  name: 'CsiFormSection',
  props: {
    title: String,
    noPadding: Boolean,
    accordion: Boolean
  },
  data() {
    return {
      isOpen: true
    }
  },
  methods: {
    toggleAccordion() {
      if(this.accordion){
        this.isOpen = !this.isOpen
      }
      
    }
  }
}
</script>

