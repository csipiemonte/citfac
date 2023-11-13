<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="dashboard-services-list">
    <dashboard-feedback-list
      :type="FEEDBACK_TYPES_MAP.SERVICE"
      :list="servicesList"
      :loading="isLoading"
      @on-filter="onFilter"
      @on-select-item="onSelectService"
    ></dashboard-feedback-list>
  </div>
</template>

<script>

import { DASHBOARD_SERVICES_DETAILS } from '../../router/routes'
import { getFeedbackStats } from '../../services/api'

import { createSlug } from '../../services/business-logic'
import DashboardFeedbackList from '../../components/dashboard/DashboardFeedbackList.vue'
import { FEEDBACK_TYPES_MAP } from '../../services/config'

export default {
  components: { DashboardFeedbackList },
  name: 'PageFeedbackServices',

  data() {
    return {
      FEEDBACK_TYPES_MAP,
      isLoading: false,
      period: null,
      servicesList: []
    }
  },
  computed: {
    tenantId() {
      return this.$store.getters['getTenantId']
    }
  },

  methods: {
    onSelectService(service) {
      let query = {
        s: this.period.startDate,
        e: this.period.endDate
      }
      let route = {
        name: DASHBOARD_SERVICES_DETAILS.name,
        params: {
          id: service.serviceId,
          slug: createSlug(service.pageTitle ?? 'servizio')
        },
        query
      }

      this.$router.push(route)
    },
    async onFilter(period) {
      this.isLoading = true
      this.period = period
      try {
        let params = {
          from: period.startDate,
          to: period.endDate,
          type: FEEDBACK_TYPES_MAP.SERVICE
        }
        let { data } = await getFeedbackStats(this.tenantId, { params })
        this.servicesList = data

    
      } catch (e) {
        console.error(e)
      }

      

      this.isLoading = false
    },
 
  }
}
</script>
