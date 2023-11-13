<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <main>
    <!--  TITOLO-->
    <div id="main-container" class="container" style="min-height: 300px">
      <div v-if="isLoading" class="my-4">
        <cf-inner-loading showing />
      </div>
    </div>
  </main>
</template>

<script>
import CfInnerLoading from "../../components/generic/CfInnerLoading.vue";
import { useMoonUrl } from "../../composables/moonUrl";
import {
  SERVICE_APPOINTMENT_BOOKING,
  SERVICE_SELF_PAYMENT,
} from "../../router/routes";
import { ASSETS_MAP } from "../../services/config";
const ASSETS_URL_MAP = {
  [ASSETS_MAP.PAYMENTS]: SERVICE_SELF_PAYMENT,
  [ASSETS_MAP.APPOINTMENTS]: SERVICE_APPOINTMENT_BOOKING,
};
export default {
  name: "PageServicesMoon",
  components: { CfInnerLoading },
  data() {
    return {
      isLoading: true,
      serviceId: null,
    };
  },
  created() {
    let { id } = this.$route.params;
    this.serviceId = id;
  },
  mounted() {
    scrollTo(0, 0);

    let service = this.activeService;
    this.$store.dispatch("setActiveService", { service });

    if (this.serviceId) {
      if (this.isMoonService) {
        location.replace(this.moonUrlNewRequest);
      } else if (!!this.asset) {
        this.$router.replace(ASSETS_URL_MAP[this.asset.codice]);
      } else {
        window.open(this.urlService, "_self");
      }
    }
  },
  computed: {
    servicesList() {
      return this.$store.getters["getComuniConfigServices"];
    },
    activeService() {
      return this.servicesList.find((service) => service.id == this.serviceId);
    },
    urlService() {
      return this.activeService?.linkServizio;
    },
    isMoonService() {
      return this.activeService?.dettaglio?.moduloMoon;
    },
    asset() {
      return this.activeService?.dettaglio?.asset;
    },
    moonUrlNewRequest() {
      let moonCode = this.activeService?.dettaglio?.moduloMoon;
      let assistanceCode = this.activeService?.dettaglio?.codiceAssistenza;
      let faqCode = this.activeService?.dettaglio?.idFaq;
      let url = this.useMoonUrl(moonCode, "new");

      if (faqCode) {
        url += `&faq=${faqCode}`;
      }
      if (assistanceCode) {
        url += `&assistenza=${assistanceCode}`;
      }
      return url;
    },
  },
  methods: {
    useMoonUrl,
  },

  inject: ["comune", "hostname", "utente", "utenteIsLoading"],
};
</script>

