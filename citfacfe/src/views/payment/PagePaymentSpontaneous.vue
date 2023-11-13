<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <main id="main-container" class="container">
    <!--    COMPONENTE TITOLO-->

    <cf-title-section :breadcrumbs="BREADCRUMBS_LINK" subtitle>
      <template #title> Pagamento spontaneo </template>

      <template #subtitle>
        <div>
          Hai bisogno di aiuto? Consulta le
          <router-link
            class="text-decoration-none fw-bold"
            data-element="faq"
            to="/faq"
            >FAQ</router-link
          >
          o
          <router-link
            class="text-decoration-none fw-bold"
            to="/nuova-richiesta"
            >Richiedi assistenza</router-link
          >
        </div>
        <br />

        <template v-if="!isUserAuth">
          <div>
            Hai già un'identità digitale SPID o CIE?
            <a
              :href="LOGIN_URL"
              class="text-decoration-none fw-bold"
              @click.prevent="goToLogin"
              >Accedi</a
            >
          </div>
        </template>
      </template>
    </cf-title-section>
    <cf-page-section class="my-4">
      <template v-if="isLoading">
        <cf-inner-loading showing />
      </template>

      <router-view v-else />
    </cf-page-section>
  </main>
  <!--    SEZIONE FISSA DEI CONTATTI-->
  <contacts-section></contacts-section>
</template>

<script>
import Breadcrumbs from "../../components/generic/Breadcrumbs.vue";
import TitleSection from "../../components/generic/TitleSection.vue";
import CfPageSection from "../../components/generic/CfPageSection.vue";
import CfTitleSection from "../../components/generic/CfTitleSection.vue";
import CfInnerLoading from "../../components/generic/CfInnerLoading.vue";

import { login } from "../../services/utils";

import { LOGIN_URL } from "../../services/business-logic";
import ContactsSection from "../../components/generic/ContactsSection.vue";
import { getPaymentTypes } from "../../services/api";

import { ASSETS_MAP, LINK_TYPES_MAP } from "../../services/config";

const BREADCRUMBS_LINK = [
  { url: "servizi", name: "Servizi", data_element: "all-services" , type: LINK_TYPES_MAP.LINK},
  { url: "", name: "Pagamento spontaneo" },
];

export default {
  name: "PagePaymentSpontaneous",
  components: {
    ContactsSection,
    CfInnerLoading,
    CfTitleSection,
    CfPageSection,
    TitleSection,
    Breadcrumbs,
  },
  data() {
    return {
      LOGIN_URL,
      BREADCRUMBS_LINK,
      isLoading: false,
    };
  },

  async mounted() {
    scroll(0, 0);
    this.isLoading = true;
    try {
      let { data } = await getPaymentTypes();
      let types = data?.paymentTypes ?? [];
      await this.$store.dispatch("setPaymentTypeList", { types });
    } catch (e) {
      await this.$store.dispatch("setNotificationInfo", {
        body: "Non è stato possibile ottenere le tipologie di pagamento",
      });
    }
    this.isLoading = false;
  },
  created() {
    if (!this.isActivePage) {
  
      window.open(this.getComuneUrl, "_self");
      return;
    }
  },
  computed: {
    getComuneUrl() {
      let comuneConfig = this.$store.getters["getComuniConfig"];
      return comuneConfig?.homePage ?? "";
    },
    user() {
      return this.$store.getters["getUser"];
    },

    getDevLogout() {
      return this.$store.getters["getDevLogout"];
    },
    isUserAuth() {
      return this.user && !this.getDevLogout;
    },
    isActivePage() {
      let assetCodeList = this.$store.getters["getAssetCodeList"];
      return assetCodeList?.includes(ASSETS_MAP.PAYMENTS);
    },
  },
  methods: {
    async goToLogin() {
      login();
    },
  },
};
</script>


