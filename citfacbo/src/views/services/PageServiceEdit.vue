<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <main class="main container mt-4" style="max-width: 800px;">
    <h1 class="csi-h3 mb-4">
      <CsiBackButton :to="SERVICES"></CsiBackButton>
      {{ serviceTitle }}
    </h1>

    <template v-if="isLoading">
      <CsiInnerLoading showing></CsiInnerLoading>
    </template>

    <csi-service-form
      :loading="isEditing"
      :defaultService="service"
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
  getServicesDetail,
  getServiceCategories,
  updateServicesDetail,
  updateServiceCard,
  getServiceCard
} from '../../services/api'
import { onShowNotificationToast, slugToTitle } from '../../services/business-logic'
import CsiModal from '../../components/core/CsiModal.vue'
import { SERVICES } from '../../router/routes'
import CsiBackButton from '../../components/core/CsiBackButton.vue'
import CsiServiceCardForm from '../../components/services/CsiServiceCardForm.vue'
import CsiAlert from '../../components/core/CsiAlert.vue'
import { useVuelidate } from '@vuelidate/core'
import { required, requiredIf } from '@vuelidate/validators'
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
    CsiServiceCardForm,
    CsiAlert
  },
  name: 'PageServiceEdit',
  data() {
    return {
      v$: useVuelidate(),
      SERVICES,
      isLoading: false,
      service: null,
      isEditing: false,
      serviceCard: null,
      serviceId: null,
      serviceTitle: null,
      isOpenConfirmModal: false
    }
  },
  validations() {
    let validations = {
      service: {
        titolo: { required }
      }
    }
    if (!this.tenantCms) {
      validations['serviceCard'] = {
        nome: { required },
        descrizione: { required },
        categoriaServizio: { required }
      }
    }

    return validations
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
  created() {
    let { id, slug } = this.$route.params
    this.serviceId = id
    this.serviceTitle = slugToTitle(slug)
  },
  async mounted() {
    if (!this.serviceId) {
      return
    }

    this.isLoading = true
    
    try {
      let { data: service } = await getServicesDetail(this.tenantId, this.serviceId)
      this.service = service
    } catch (error) {
      await onShowNotificationToast({
        body: 'Non è stato possibile caricare il servizio.',
        type: 'error'
      })
    }
   
   
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

    async onSubmit() {
      this.v$.$touch()

      if (this.v$.$error) {
        this.$refs.validationAlert.$el.scrollIntoView({ block: 'start' })

        return
      }

      this.isOpenConfirmModal = true
    },

    async onConfirm(service, serviceCard) {
      this.isEditing = true
     
        if (serviceCard  && !this.tenantCms) {
        try {
          let response= await updateServiceCard(this.tenantId, serviceCard.id, serviceCard)
        
        } catch (error) {
          await onShowNotificationToast({
            body: 'Non è stato possibile modificare servizio',
            type: 'error'
          })


        }
      }
      

      try {
        let response = await updateServicesDetail(this.tenantId, service.id, service)
        await onShowNotificationToast({
          body: 'Servizio creato con successo',
          type: 'success'
        })
       
      } catch (error) {
        let message = 'Non è stato possibile modificare il servizio.'
          if (error.request?.status !== 422) {
            message += 'Scheda descrittiva già associata ad altro servizio'
          }
          await onShowNotificationToast({
            body: message,
            type: 'error'
          })
      }
        await this.$store.dispatch("updateServiceList")
        this.$router.push(SERVICES)
      this.isEditing = false
    }
  }
}
</script>

