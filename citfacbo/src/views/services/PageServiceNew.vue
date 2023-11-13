<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <main class="main container mt-4" style="max-width: 800px">
    <h1 class="csi-h3 mb-4">
      <CsiBackButton :to="SERVICES"></CsiBackButton>
      {{ this.$route.meta?.title ?? '-' }}
    </h1>

    <template v-if="isLoading">
      <CsiInnerLoading showing></CsiInnerLoading>
    </template>
    <csi-service-form
      :loading="isCreatingService"
      v-else
      @on-confirm="onConfirm"
    ></csi-service-form>
  </main>
</template>

<script>
import CsiAccordion from '../../components/core/CsiAccordion.vue'
import CsiButton from '../../components/core/CsiButton.vue'
import CsiCard from '../../components/core/CsiCard.vue'
import CsiInnerLoading from '../../components/core/CsiInnerLoading.vue'
import CsiListItem from '../../components/core/CsiListItem.vue'
import CsiFormSection from '../../components/core/form/CsiFormSection.vue'
import CsiServiceForm from '../../components/services/CsiServiceForm.vue'
import CsiSelect from '../../components/core/form/CsiSelect.vue'
import {
  newServicesDetail,
  getServiceCategories,
  createServiceCard,
  getServicesDetail,
  getServiceCards,
  getServicesDetails
} from '../../services/api'
import { onShowNotificationToast } from '../../services/business-logic'
import CsiModal from '../../components/core/CsiModal.vue'
import { SERVICES } from '../../router/routes'
import CsiBackButton from '../../components/core/CsiBackButton.vue'
import CsiServiceCardForm from '../../components/services/CsiServiceCardForm.vue'
export default {
  components: {
    CsiInnerLoading,
    CsiCard,
    CsiListItem,
    CsiButton,
    CsiAccordion,
    CsiServiceForm,
    CsiFormSection,
    CsiSelect,
    CsiModal,
    CsiBackButton,
    CsiServiceCardForm
  },
  name: 'PageServiceNew',
  data() {
    return {
      SERVICES,
      isLoading: false,
      service: null,
      isCreatingService: false,
      serviceCard: null
    }
  },
  computed: {
    tenantCms() {
      return this.$store.getters['getTenantCms']
    },

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
    serviceCategories() {
      return this.$store.getters['getServiceCategoriesList']
    }
  },
  async mounted() {
    this.isLoading = true

    await this.$store.dispatch('setDefaultServiceParams')

    if (!this.tenantCms) {
      if (this.serviceCategories?.length === 0) {
        await this.getServiceCategoriesList()
      }
    }

    this.isLoading = false
  },
  methods: {
    async getServiceCategoriesList() {
      try {
        let { data: list } = await getServiceCategories()
        await this.$store.dispatch('setServiceCategoriesList', { list })
      } catch (error) {
        await onShowNotificationToast({
          body: 'Non è stato possibile recuperare la lista delle categorie.',
          type: 'error'
        })
      }
    },
    async onConfirm(serviceDetail, serviceCard) {
      this.isCreatingService = true
      let newService = null
      if (serviceCard && !this.tenantCms) {
        try {
          let { data } = await createServiceCard(this.tenantId, serviceCard)
          newService = data
          serviceDetail.idServizio = newService?.id
        } catch (error) {
          await onShowNotificationToast({
            body: 'Non è stato possibile creare il nuovo servizio',
            type: 'error'
          })
        }
      }

        try {
          let response = await newServicesDetail(this.tenantId, serviceDetail)

          await onShowNotificationToast({
            body: 'Servizio creato con successo',
            type: 'success'
          })
        } catch (error) {
          //gestisce errore 422 (solo un scheda descrittiva)
          let message = 'Non è stato possibile creare il nuovo servizio.'
          if (error.request?.status !== 422) {
            message += 'Scheda descrittiva già associata ad altro servizio'
          }

          await onShowNotificationToast({
            body: message,
            type: 'error'
          })
        }
  
      await this.$store.dispatch('updateServiceList')
      this.$router.push(SERVICES)
      this.isCreatingService = false
    }
  }
}
</script>
