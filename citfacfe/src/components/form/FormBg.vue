<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <!--        COLORE SCURO -->
  <div class="bg-gray pb-2 my-2">
    <div class="row m-4">
      <div class="col-12 px-0 py-3">
        <h3>{{ title }}</h3>
      </div>

      <!--            COLORE CHIARO-->
      <div class="col-12  bg-white" :class="{'pt-5' : !noPadding}">
        <slot></slot>
      </div>
    </div>
  </div>
</template>

<script>

  //COMPONENTE PER AVERE SFONDO E SPAZIATURE DEL FORM
  // PER UTILIZZARE IL COMPONENTE USARE I PROPS
  //title" TITOLO DEL FORM,
export default {
  name: "FormBg",
  props: {
    title: String,
    noPadding:Boolean
  },

  data() {
    return {};
  },

  created() {},

  computed: {},
  mounted() {},
  methods: {},
};
</script>

