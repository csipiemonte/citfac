<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<script>
import Header from "./components/Header.vue";
import Footer from "./components/Footer.vue";
import { computed } from "vue";

import {
  getConfigComuni,
  getConfigMenu,
  getConfigServices,
  getConfigDetailServices,
  getMe,
  getNotifyServicesList,
} from "./services/api";
import NotificationToast from "./components/notify/NotificationToast.vue";
import CfInnerLoading from "./components/generic/CfInnerLoading.vue";
import TheGuardFcm from "./components/TheGuardFcm.vue";
import { ASSETS_MAP, MAINTENANCE_HASH_KEY } from "./services/config";
import TheGuardMaintenance from "./components/TheGuardMaintenance.vue";

export default {
  name: "App",
  props: {
    msg: {
      type: String,
      required: false,
    },
  },
  data() {
    return {
      utenteIsLoading: true,
      hostname: window.location.hostname,
     
      isLoading: false,
      isLoadingAuthentication: false,
    };
  },
  async created() {
    this.isLoading = true;
    try {
      let configComuniPromise = await getConfigComuni(this.hostname);
      let comuniConfig = configComuniPromise.data;
      this.$store.commit("setComuniConfig", comuniConfig);
    } catch (e) {}

    //Verifichiamo se ha salvato sullo storage le credenziali di manutenzione
    if (this.comuneSelected?.maintenance) {
      let hash = sessionStorage.getItem(MAINTENANCE_HASH_KEY);
      await this.$store.dispatch("setMaintenanceHash", { hash });

      if(!this.hasMaintenanceHash){
        this.isLoading = false
      }
    }
   
  },
  watch: {
    isMaintenance: {
      immediate: true,
      async handler(val, oldVal) {
        if (!val) {
          await this.getDefaultData();
        }
      },
    },
  },
  computed: {
    comuneSelected() {
      return this.$store.getters["getComuniConfig"];  
    },

    notificationInfo() {
      return this.$store.getters["getNotificationInfo"];
    },
    assetCodeList() {
      return this.$store.getters["getAssetCodeList"];
    },
    utente() {
      return this.$store.getters["getUser"];
    },
    isMaintenance() {
      if (!this.comuneSelected) return true;
      return this.comuneSelected?.maintenance && !this.hasMaintenanceHash;
    },
    hasMaintenanceHash() {
      return !!this.$store.getters["getMaintenanceHash"];
    },
  },
  methods: {
    async getUtentiMe() {
      this.utenteIsLoading = true;
      try {
        let { data: user } = await getMe();
        this.$store.dispatch("setUser", { user });
      } catch (e) {}
      this.utenteIsLoading = false;
    },
    onConfirmCredentials() {
     
      this.getDefaultData();
    },
    async getDefaultData() {
      this.isLoading = true;
      this.getUtentiMe();
      let configComuniServicesPromise = await getConfigServices(this.hostname);
      let configComuniDetailServicesPromise = await getConfigDetailServices();
      let assets = this.comuneSelected?.assets;
      let notificationsServicesPromise = null;
      if (this.assetCodeList.includes(ASSETS_MAP.NOTIFICATIONS)) {
        notificationsServicesPromise = getNotifyServicesList();
      }

      try {
        let comuniConfigServices = await configComuniServicesPromise.data;
        let comuniConfigDetailServices =
          await configComuniDetailServicesPromise.data;

        await this.$store.commit(
          "setComuniConfigServices",
          comuniConfigServices
        );

        await this.$store.commit(
          "setComuniConfigDetailServices",
          comuniConfigDetailServices
        );
      } catch (e) {}
      try {
        let notificationsServicesResponse = await notificationsServicesPromise;
        let list = notificationsServicesResponse.data;
        this.$store.commit("setNotifyServicesList", { list });
      } catch (e) {}

      try {
        let configComuniMenuPromise = await getConfigMenu(this.hostname);
        let comuniConfigMenu = configComuniMenuPromise.data;
        this.$store.commit("setComuniConfigMenu", comuniConfigMenu);
      } catch (e) {}

      this.isLoading = false;
    },
  },

  provide() {
    return {
      comune: computed(() => this.comuneSelected),
      hostname: computed(() => this.hostname),
      utente: computed(() => this.utente),
      utenteIsLoading: computed(() => this.utenteIsLoading),
    };
  },
  components: {
    TheGuardFcm,
    CfInnerLoading,
    NotificationToast,
    Header,
    Footer,
    TheGuardMaintenance,
  },
};
</script>

<template>
  <template v-if="isLoading">
    <main class="container" style="height: 100vh">
      <div
        class="position-absolute translate-middle"
        style="top: 50%; left: 50%"
      >
        <CfInnerLoading showing />
      </div>
    </main>
  </template>
  <template v-else-if="isMaintenance">
    <the-guard-maintenance @onConfirm="onConfirmCredentials" />
  </template>

  <template v-else>
    <Header></Header>
    <template v-if="utenteIsLoading || isLoading">
      <main class="container mt-5" style="min-height: 500px">
        <cf-inner-loading showing />
      </main>
    </template>
    <template v-else>
      <TheGuardFcm>
        <RouterView />
      </TheGuardFcm>
    </template>

    <Footer></Footer>
  </template>

  <notification-toast
    :notification-title="notificationInfo.title"
    :notification-message="notificationInfo.body"
    id="notification-toast"
    :positive="notificationInfo.positive"
  ></notification-toast>
</template>


