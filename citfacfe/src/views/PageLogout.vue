<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <main class="container">
    <section  class="container my-5 it-page-sections-container">
      <h1 class="mb-3">Sei uscito dall'area personale</h1>

        <a type="button" class="btn btn-primary" :href="getComuneUrl">Torna alla Homepage</a>


    </section>
  </main>
</template>

<script>
export default {
name: "PageLogout",
computed:{
  getComuneUrl() {
      let comuneConfig = this.$store.getters["getComuniConfig"];
      return comuneConfig?.homePage ?? "";
    },
}
}

</script>


