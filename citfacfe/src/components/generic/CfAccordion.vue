<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="cf-accordion">
    <div :id="'accordion'+id" class="accordion">
      <div class="accordion-item  p-3">
        <div class="accordion-header">

          <button
              :id="id"
              :aria-controls="id"
              class="accordion-button collapsed "
              type="button"
              @click="toggleAccordion"
          >

            <div class="button-wrapper">

              <slot name="buttonHeader" />

            </div>
          </button>

          <slot name="subtitleHeader" />
        </div>

        <div
            :id="targetId"
            :aria-labelledby="id"
            class="accordion-collapse collapse p-0 m-0  cf-accordion"
            role="region"
        >
          <div class="accordion-body mt-2">

            <slot name="body"/>
          </div>

        </div>
      </div>
    </div>
  </div>

</template>

<script>
export default {
name: "CfAccordion",
  props:{
   id:[String,Number]
  },
  computed:{
    targetId() {
      return this.id + 'Target'
    },
  },


  methods:{
   toggleAccordion ()  {

      const accordion = document.getElementById(this.targetId);
      const accordionButton = document.getElementById(this.id);

      if (accordionButton.classList.contains("collapsed")) {
        accordionButton.classList.add("showed");
        accordionButton.classList.remove("collapsed");
      } else {
        accordionButton.classList.add("collapsed");
        accordionButton.classList.remove("showed");
      }
      if (accordion.classList.contains("show")) {
        accordion.classList.add("collapse");
        accordion.classList.remove("show");
        accordion.ariaExpanded = "false";

      } else {
        accordion.classList.add("show");
        accordion.classList.remove("collapse");
        accordion.ariaExpanded = "true";
      }
    }
  }
}
</script>


