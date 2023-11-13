<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="dashboard-feeedback-list">
    <dashboard-filter
      :disabled="loading || !filteredList.length"
      @setPeriod="onFilter"
      @orderBy="onOrderBy"
    ></dashboard-filter>

    <div class="columns mt-3">
      <template v-if="loading">
        <csi-inner-loading showing></csi-inner-loading>
      </template>
      <template v-else-if="filteredList.length > 0">
        <dashboard-feedback-item
          v-for="(item, index) in filteredList"
          :key="index"
          :item="item"
          :type="type"
          @onSelect="onSelectItem"
        ></dashboard-feedback-item>
      </template>
      <div class="d-grid container text-center" v-else>
        <csi-alert type="info" class="my-4">
          Nessun risultato in base ai filtri di ricerca</csi-alert
        >
      </div>
    </div>
  </div>
</template>

<script>
import DashboardFeedbackItem from '../../components/dashboard/DashboardFeedbackItem.vue'
import DashboardFilter from '../../components/dashboard/DashboardFilter.vue'

import { ORDER_BY_MAP } from '../../services/config'
import CsiInnerLoading from '../../components/core/CsiInnerLoading.vue'
import {  orderBy } from '../../services/business-logic'
import CsiAlert from '../../components/core/CsiAlert.vue'
export default {
  components: { DashboardFilter, DashboardFeedbackItem, CsiInnerLoading, CsiAlert },
  name: 'DashboardFeedbackList',
  props: {
    type: { type: String, required: true, default: null },
    list: { type: Array, default: () => [] },
    loading: { type: Boolean, default: false }
  },
  data() {
    return {
      period: null,
      periodList: [],
      filteredList: []
    }
  },
  watch: {
    list: {
      immediate: true,
      deep: true,
      handler(val) {
        this.filteredList = val
      }
    }
  },
  computed: {
    tenantId() {
      return this.$store.getters['getTenantId']
    }
  },
  created() {
    this.filteredList = [...this.list]
  },

  methods: {
    onSelectItem(item) {
     this.$emit('on-select-item', item)
    },
    async onFilter(period) {
    
      this.$emit('on-filter', period)
    },
    onOrderBy(value) {
 
      switch (value) {
        case ORDER_BY_MAP.NAME:
          this.filteredList = orderBy(this.filteredList, ['pageTitle'])
          break
        case ORDER_BY_MAP.VOTE_HIGHT:
          this.filteredList = orderBy(this.filteredList, ['averageRating'], ['desc'])
          break
        case ORDER_BY_MAP.VOTE_LOW:
          this.filteredList = orderBy(this.filteredList, ['averageRating'], ['asc'])
          break
        default:
          this.filteredList = this.list
      }
    }
  }
}
</script>
