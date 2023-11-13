<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <main class="main container text-center mt-4">
    <div class="col">
      <img style="max-width: 400px" src="../assets/images/errore404.svg" />
    </div>
    <h1 class="text-secondary my-3">Pagina non trovata</h1>

    <router-link :to="HOMEPAGE" class="btn btn-primary"> Vai alla pagina principale </router-link>
  </main>
</template>

<script>
import {  HOMEPAGE } from '../router/routes'
export default {
  name: 'PageError404',
  components: {},

  data() {
    return {
        HOMEPAGE
    }
  },

  async created() {},
  computed: {
    getComuneConfig() {
      return this.$store.getters['getComuniConfig']
    },
    getComuneUrl() {
      return this.getComuneConfig?.homePage ?? ''
    }
  }
}
</script>

<style scoped></style>
