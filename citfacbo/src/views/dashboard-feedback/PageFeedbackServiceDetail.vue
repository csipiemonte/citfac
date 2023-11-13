<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <main class=" main container mt-4 dashboard-service-detail">
    
    <h4 class="csi-h4">
      <strong>{{ serviceTitle }}</strong>
    </h4>

    <div class="mt-3 row align-items-center"  v-if="!isLoading">
      <div class="fw-bold col-auto">Periodo</div>
      <dashboard-filter
        :disabled="isLoading"
        class="col"
        @setPeriod="onFilter"
        hide-order
        :default="period"
      ></dashboard-filter>
    </div>

    <template v-if="isFiltering || isLoading">
      <csi-inner-loading showing></csi-inner-loading>
    </template>

    <template v-else>
      <dashboard-feedback-item-detail
        :options="serviceOptions"
        :ratings="serviceRatings"
        :average-rating="averageRating"
        :total-feedback="totalFeedback"
      ></dashboard-feedback-item-detail>

      <dashboard-feedback-item-comments
        :comments="commentsList"
        :more-comments="hasMoreComments"
        :loading="isLoadingComments"
        @load-more="getFeedbackCommentList"
      >
      </dashboard-feedback-item-comments>
    </template>
  </main>
</template>

<script>
import DashboardFilter from '../../components/dashboard/DashboardFilter.vue'
import { getFeedbackComments, getFeedbackDetail } from '../../services/api'

import { DASHBOARD_SERVICES } from '../../router/routes'
import DashboardFeedbackItemDetail from '../../components/dashboard/DashboardFeedbackItemDetail.vue'
import CsiInnerLoading from '../../components/core/CsiInnerLoading.vue'
import { floatWithDecimals, slugToTitle } from '../../services/business-logic'
import { FEEDBACK_COMMENTS_LIMIT } from '../../services/config'
import DashboardFeedbackItemComments from '../../components/dashboard/DashboardFeedbackItemComments.vue'

export default {
  components: {
    DashboardFilter,

    DashboardFeedbackItemDetail,
    CsiInnerLoading,
    DashboardFeedbackItemComments
  },
  name: 'PageFeedbackServiceDetail',
  data() {
    return {
      isLoading: true,
      serviceId: null,
      filteredPagesList: [],
      serviceTitle: null,
      serviceDetail: null,
      commentsList: [],
      isLoadingComments: false,
      commentsOffset: 0,
      hasMoreComments: false,
      period: null,
      isFiltering:false
    }
  },
  created() {
    let { id, slug } = this.$route.params
    this.serviceId = id

    if (!this.serviceId) {
      this.$router.replace(DASHBOARD_SERVICES)
    }

    this.serviceTitle = slugToTitle(slug)
    let query = this.$route.query
    if (query) {
      this.period = {
        startDate: query.s,
        endDate: query.e
      }
    }

    this.isLoading = false

  },

  computed: {
    tenantId() {
      return this.$store.getters['getTenantId']
    },
    averageRating() {
      return floatWithDecimals(this.serviceDetail?.averageRating)
    },
    totalFeedback() {
      return this.serviceDetail?.totalFeedbacks
    },
    serviceOptions() {
      return this.serviceDetail?.options
    },
    serviceRatings() {
      return this.serviceDetail?.rating
    }
  },
  methods: {
    async onFilter(period) {
      if (!this.serviceId) {
        return
      }
      this.isFiltering = true
      this.period = period

      try {
        let params = {
          from: period.startDate,
          to: period.endDate,
          serviceId: this.serviceId
        }
        let { data: detail } = await getFeedbackDetail(this.tenantId, { params })
        this.serviceDetail = detail

        this.getFeedbackCommentList()
      } catch (e) {}

      this.isFiltering = false
    },
    async getFeedbackCommentList() {
      this.isLoadingComments = true

      let params = {
        limit: FEEDBACK_COMMENTS_LIMIT,
        startPage: this.commentsOffset,
        from: this.period.startDate,
        to: this.period.endDate,
        serviceId: this.serviceId
      }

      let { data: comments } = await getFeedbackComments(this.tenantId, { params })
      this.commentsList = [...this.commentsList, ...comments]

      this.hasMoreComments = comments.length > 0 && comments.length >= FEEDBACK_COMMENTS_LIMIT
      this.commentsOffset++

      this.isLoadingComments = false
    }
  }
}
</script>
