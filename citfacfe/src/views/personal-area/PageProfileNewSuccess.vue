<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <main id="main-container" class="container">
    <cf-title-section class="pb-4 pb-lg-5" :breadcrumbs="BREADCRUMBS" subtitle>
      <template #title>
        Profilo utente {{userFullname}}
      </template>

      <template #subtitle>
        CF: {{userTaxCode}}
      </template>
    </cf-title-section>

    <div class="alert alert-success my-4" role="alert" >
      <h1 class="alert-heading h2 pb-2">Profilo utente completato</h1>
      <p class="mb-0">Hai inserito correttamente i tuoi contatti e attivato le notifiche</p>
    </div>

    <a type="button"  data-element="personal-area-login"  class="btn btn-primary mb-4" @click="goToProfile">Vai all'area personale</a>
  </main>

</template>

<script>
import CfTitleSection from "../../components/generic/CfTitleSection.vue";
import { PERSONAL_AREA, USER_PROFILE} from "../../router/routes";

const BREADCRUMBS = [
  {url: PERSONAL_AREA.path, name: "Area Personale", data_element:"personal-area-login"},
  {url: USER_PROFILE.path, name: "Profilo"}

]


export default {
  name: "PageProfileNewSuccess",
  components: {CfTitleSection},
  data() {
    return {
      BREADCRUMBS
    }
  },
  computed: {
    user() {
      return this.$store.getters['getUser']
    },
    userTaxCode() {
      return this.user?.codFisc ?? null
    },
    userFullname() {
      let name = this.user?.nome
      let surname = this.user?.cognome

      return name && surname ? `${name} ${surname}` : null
    },
  },
  methods:{
    goToProfile(){
      this.$router.replace({name: "area-personale"})
    }
  }
}
</script>


