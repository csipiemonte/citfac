<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <main class=" main container mt-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1 class="csi-h3 m-0">{{this.$route.meta?.title ?? '-'}}</h1>
      <router-link class="btn btn-primary" :to="SERVICE_NEW"> Nuovo servizio </router-link>
    </div>

    <template v-if="isLoading">
      <csi-inner-loading showing></csi-inner-loading>
    </template>

    <!-- ELENCO SERVIZI  -->
    <!-- ------------------------------------------------------------------------------------------ -->

    <template v-else-if="hasServices">
      <TransitionGroup name="list">
        <service-item
          class="mb-3"
          v-for="service in servicesList"
          :key="service.id"
          :service="service"
          v-show="!service.deleted"
          @on-delete="onDeleteService"
        ></service-item>
      </TransitionGroup>
    </template>

    <div class="d-grid container text-center" v-else>
      <csi-alert type="info" class="my-4">Nessun servizio</csi-alert>
    </div>
  </main>
</template>

<script>
import CsiButton from '../../components/core/CsiButton.vue'

import CsiInnerLoading from '../../components/core/CsiInnerLoading.vue'

import ServiceItem from '../../components/services/ServiceItem.vue'

import { SERVICE_NEW } from '../../router/routes'

import CsiAlert from '../../components/core/CsiAlert.vue'
export default {
  components: {
    CsiInnerLoading,
    CsiButton,
    ServiceItem,
    CsiAlert
  },
  name: 'PageServices',
  data() {
    return {
      SERVICE_NEW,
      isLoading: false,
      servicesList: [],
      faqCategory: null,
      isLoadingFaqs: false,
      faqs: [],
      faqOptions: [],
      isSavingSettings: false,
      service: null,
      isOpenConfirmModal: false
    }
  },
  computed: {
    tenantId() {
      return this.$store.getters['getTenantId']
    },
    services() {
      return this.$store.getters['getServicesList']
    },
    serviceCards() {
      return this.$store.getters['getServiceCardList']
    },
    assistanceServices() {
      return this.$store.getters['getAssistanceServiceList']
    },
    moonModules() {
      return this.$store.getters['getMoonModulesList']
    },
    notifyServices() {
      return this.$store.getters['getNotifyServiceList']
    },
    hasServices(){
      let services=  this.servicesList.filter((service) =>   !service.deleted )
      return services.length>0
    }
  },
  async mounted() {
    this.isLoading = true
    await this.$store.dispatch('setDefaultServiceParams')
    this.servicesList = [...this.services]
    this.isLoading = false
  },
  methods: {

    async onDeleteService(id) {
      setTimeout(() => {
        this.servicesList = this.servicesList.map((service) => {
          if (service.id === id) {
            service.deleted = true
          }
          return service
        })
      }, 500)
    },

   
  }
}
</script>
