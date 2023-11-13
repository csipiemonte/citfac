<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="dashboard-page-list">
    <dashboard-feedback-list
      :type="FEEDBACK_TYPES_MAP.PAGE"
      :list="pagesList"
      :loading="isLoading"
      @on-filter="onFilter"
      @on-select-item="onSelectPage"
    ></dashboard-feedback-list>
  </div>
</template>

<script>
import { getFeedbackStats } from '../../services/api'
import { DASHBOARD_PAGE_DETAILS } from '../../router/routes'
import { FEEDBACK_TYPES_MAP } from '../../services/config'

import { createSlug, orderBy } from '../../services/business-logic'

import DashboardFeedbackList from '../../components/dashboard/DashboardFeedbackList.vue'
export default {
  components: { DashboardFeedbackList },
  name: 'PageFeedbackPages',

  data() {

    return {
      FEEDBACK_TYPES_MAP,
      isLoading: false,
      isFiltering: false,
      period: null,
      periodList: [],
      pagesList: []
    }
  },
  computed: {
    tenantId() {
      return this.$store.getters['getTenantId']
    }
  },

  mounted() {},

  methods: {
    onSelectPage(page) {
      let query = {
        s: this.period.startDate,
        e: this.period.endDate
      }
      let route = {
        name: DASHBOARD_PAGE_DETAILS.name,
        params: {
          url: encodeURI(page.pagePath),
          slug: createSlug(page.pageTitle ?? 'pagina')
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
          type: FEEDBACK_TYPES_MAP.PAGE
        }
        let { data } = await getFeedbackStats(this.tenantId, { params })
        this.pagesList = data


      } catch (e) {
        console.error(e)
      }

      this.isLoading = false
    },

  }
}
</script>
