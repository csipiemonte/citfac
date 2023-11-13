<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div>
    <csi-card :id="'service' + serviceId" class="service-item" shadow rounded >
     
      <template #body>
        <h3>{{ serviceTitle }} <span class="small" v-if="isAsset">(Servizio di tipo asset)</span></h3>

        <div class="row">
          <div class="col-12 mb-3 col-md-6">
            <div class="small">Scheda descrittiva</div>
            <strong class="label fw-bold">{{ serviceCard?.nome || '-' }}</strong>
          </div>

          <div class="col-12 mb-3 col-md-6">
            <div class="small">Modulo Moon</div>
            <strong class="label fw-bold">{{ moonModule?.descrizioneModulo || '-' }}</strong>
          </div>

          <div class="col-12 mb-3 col-md-6">
            <div class="small">Assistenza CRM</div>
            <strong class="label fw-bold">{{ assistanceCrm?.name || '-' }}</strong>
          </div>

          <div class="col-12 mb-3 col-md-6">
            <div class="small">Sportello appuntamenti</div>
            <strong class="label fw-bold">{{ apppointmentOffice?.name || '-' }}</strong>
          </div>

          <div class="col-12 mb-3 col-md-6">
            <div class="small">Notificatore</div>
            <strong class="label fw-bold">{{ notifyService?.name || '-' }}</strong>
          </div>

          <div class="col-12 mb-3">
            <div class="small">FAQ</div>
            <template v-if="isLoadingFaqs">
              <csi-inner-loading showing></csi-inner-loading>
            </template>
            <faq-breadcrumbs
              v-else-if="faqBreadcrumb.length > 0"
              :faqBreadcrumb="faqBreadcrumb"
            ></faq-breadcrumbs>
            <strong class="label fw-bold" v-else>-</strong>
          </div>
        </div>
      </template>

      <template #footer>
        <csi-button btn-type="router-link" :to="serviceDetailUrl" class="col-auto"
          >Modifica</csi-button
        >
        <csi-button
          type="button"
          color="danger"
          outline
          class="col-auto"
          @click="showCancelModal = true"
          >Cancella</csi-button
        >
      </template>
    </csi-card>

    <csi-modal
      id="ServiceDeleteModal"
      v-model="showCancelModal"
      title="Cancella Servizio"
      @onConfirm="onDeleteService"
      confirmBtnClass="btn-danger"
      :loading="isDeletingService"
    >
      <p>
        <strong>Attenzione</strong><br />
        Vuoi cancellare il servizio <strong>{{ serviceTitle }}</strong> ?
      </p>
    </csi-modal>
  </div>
</template>

<script>
import CsiButton from '../core/CsiButton.vue'
import CsiModal from '../core/CsiModal.vue'

import { createSlug, onShowNotificationToast } from '../../services/business-logic'
import {
  deleteServiceCard,
  deleteServicesDetail,
  getFaqDetail,
  getFaqTypeList
} from '../../services/api'
import CsiInnerLoading from '../core/CsiInnerLoading.vue'

import CsiCard from '../core/CsiCard.vue'

import FaqBreadcrumbs from './FaqBreadcrumbs.vue'
import { SERVICE_EDIT } from '../../router/routes'

export default {
  components: {
    CsiModal,
    CsiButton,
    CsiInnerLoading,
    CsiCard,
    FaqBreadcrumbs
  },

  CsiCarde: 'ServiceItem',
  emits: ['onDelete'],
  props: {
    service: Object
  },
  data() {
    return {
      faqBreadcrumb: [],
      isLoadingFaqs: false,
      showCancelModal: false,
      isDeletingService: false
    }
  },
  async mounted() {
    if (this.service.idFaq) {
      this.isLoadingFaqs = true
      try {
        let { data: faq } = await getFaqDetail(this.service.idFaq)
        this.faqBreadcrumb = faq.breadcrumb ?? []
      } catch (error) {}
      this.isLoadingFaqs = false
    }
  },
  computed: {
    tenantId() {
      return this.$store.getters['getTenantId']
    },
    isAsset(){
      return !!this.service?.asset
    },
    serviceDetailUrl() {
      return {
        name: SERVICE_EDIT.name,
        params: {
          id: this.serviceId,
          slug: createSlug(this.serviceTitle)
        }
      }
    },
    tenantCms() {
      return this.$store.getters['getTenantCms']
    },
    serviceTitle() {
      return this.service?.titolo ?? ''
    },
    serviceId() {
      return this.service?.id
    },
    serviceCard() {
      return this.serviceCardList?.find(
        (s) => s.id.toString() === this.service?.idServizio?.toString()
      )
    },
    serviceCardList() {
      return this.$store.getters['getServiceCardList']
    },
    assistanceCrm() {
      return this.assistanceCrmList.find(
        (a) => a.id.toString() === this.service?.codiceAssistenza?.toString()
      )
    },
    assistanceCrmList() {
      return this.$store.getters['getAssistanceServiceList']
    },
    moonModule() {
      return this.moonModuleList.find((m) => m.codiceModulo === this.service?.moduloMoon)
    },
    moonModuleList() {
      let modules = this.$store.getters['getMoonModulesList']
      return modules.map((m) => {
        let status = m.stato?.descrizione ?? '-'
        m.descrizioneModuloNew = `${m.descrizioneModulo} (${status})`
        return m
      })
    },
    notifyService() {
      return this.notifyServiceList.find((n) => n.name === this.service?.codiceNotifiche)
    },
    notifyServiceList() {
      return this.$store.getters['getNotifyServiceList']
    },
    apppointmentOffice(){
     let offices =  this.$store.getters['getAppointmentOfficesList']
     return offices.find(
        (office) => office.id.toString() === this.service?.idSportello?.toString()
      )
    },

  },
  methods: {
    async onDeleteService() {
      this.isDeletingService = true
      try {
        let responseCard = await deleteServiceCard(this.tenantId, this.service.idServizio)

        let responseDetail = await deleteServicesDetail(this.tenantId, this.serviceId)
        this.$emit('onDelete', this.serviceId)
      } catch (error) {
        await onShowNotificationToast({
          body: 'Non è stato possibile cancellare il servizio.',
          type: 'error'
        })
      }
      this.isDeletingService = false
      this.showCancelModal = false
    }
  }
}
</script>
