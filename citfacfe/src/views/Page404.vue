<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <main id="main-container" class="text-center container my-5">
    <div class="col">
      <img style="max-width: 400px" src="../assets/images/errore404.svg" />
    </div>
    <h1 class="text-primary my-3">Pagina non trovata</h1>
    <p>Torna indietro o utilizza il menù per continuare la navigazione</p>

    <a :href="getComuneUrl" class="btn btn-primary" >
      Vai alla pagina principale
    </a>
  </main>
</template>

<script>
export default {
  name: "Page404",
  components: {},

  data() {
    return {};
  },

  async created() {},
  computed: {
    getComuneConfig() {
      return this.$store.getters["getComuniConfig"];
    },
    getComuneUrl() {
      return this.getComuneConfig?.homePage ?? "";
    },
  },
};
</script>


