<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<script>
  //PER COMPORRE IL TITOLO DELLA PAGINA BASTA PASSARE TITOLO E SOTTOTIOLO ALLE PROP: "title" E "subtitle" RISPETTIVAMENTE
export default {
  name: "TitleSection",
  props: {
    title: String,
    subtitle: String,
  },
  data() {
    return {};
  },
  async created() {},
  computed: {},
  methods: {},
  async mounted() {},
};
</script>

<template>
  <div class="container">
    <div class="row justify-content-center ">
      <div class="col-12 col-lg-10">
        <div class="cf-hero">
          <section class="it-hero-wrapper bg-white align-items-start">
            <div class="it-hero-text-wrapper pt-0 ps-0 pb-2">
              <h1 class="text-black hero-title" data-element="page-name">
                {{ title }}
              </h1>
              <template v-if="subtitle">
                <div class="hero-text">
                  <p>{{ subtitle }}</p>
                </div>
              </template>
            </div>
          </section>
        </div>
      </div>
    </div>
  </div>
</template>


