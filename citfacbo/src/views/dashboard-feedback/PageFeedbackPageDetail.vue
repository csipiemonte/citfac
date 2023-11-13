<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
 
    <main class="main container mt-4 dashboard-page-detail" >
    <h4 class="csi-h4">
      <strong>{{ pageTitle }}</strong>
    </h4>

    <div>
      Url pagina:
      <a class="page-url" :href="pagePath" target="_blank">{{ pagePath }}</a>
    </div>

    <div class="mt-3 row align-items-center" v-if="!isLoading">
      <div class="fw-bold col-auto">Periodo</div>
      <dashboard-filter
        :default="period"
        :disabled="isLoading"
        class="col"
        @setPeriod="onFilter"
        hide-order
      ></dashboard-filter>
    </div>

    <template v-if="isFiltering || isLoading">
      <csi-inner-loading showing></csi-inner-loading>
    </template>

    <template v-else>
      <dashboard-feedback-item-detail
        :options="pageOptions"
        :average-rating="averageRating"
        :ratings="pageRatings"
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
import CsiCard from '../../components/core/CsiCard.vue'
import DashboardRating from '../../components/dashboard/DashboardRating.vue'

import { FEEDBACK_COMMENTS_LIMIT } from '../../services/config'

import CsiAccordion from '../../components/core/CsiAccordion.vue'

import CsiInnerLoading from '../../components/core/CsiInnerLoading.vue'
import { floatWithDecimals, slugToTitle } from '../../services/business-logic'
import { DASHBOARD_PAGES } from '../../router/routes'
import DashboardFeedbackItemDetail from '../../components/dashboard/DashboardFeedbackItemDetail.vue'
import DashboardFeedbackItemComments from '../../components/dashboard/DashboardFeedbackItemComments.vue'

export default {
  components: {
    DashboardFilter,
    CsiCard,
    DashboardRating,
    CsiAccordion,
    CsiInnerLoading,
    DashboardFeedbackItemDetail,
    DashboardFeedbackItemComments
  },
  name: 'PageFeedbackPageDetail',
  data() {
    return {
      isLoading: true,
      isFiltering: false,
      defaultStartDate: null,
      defaultEndDate: null,
      filteredPagesList: [],
      pageDetail: null,
      pageTitle: null,
      pageId: null,
      commentsList: [],
      isLoadingComments: false,
      commentsOffset: 0,
      hasMoreComments: false,
      period: null
    }
  },
  async created() {
    let { url, slug } = this.$route.params
    this.pageId = url

    if (!this.pageId) {
      this.$router.replace(DASHBOARD_PAGES)
    }

    this.pageTitle = slugToTitle(slug)
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
    pagePath() {
      return decodeURI(this.pageId)
    },
    averageRating() {
      return floatWithDecimals(this.pageDetail?.averageRating)
    },
    totalFeedback() {
      return this.pageDetail?.totalFeedbacks
    },
    pageOptions() {
      return this.pageDetail?.options
    },
    pageRatings() {
      return this.pageDetail?.rating
    }
  },
  methods: {
    async onFilter(period) {
      if (!this.pageId) return
      this.isFiltering = true
      this.period = period
      try {
        let params = {
          from: period.startDate,
          to: period.endDate,
          page: this.pageId
        }
        let { data: detail } = await getFeedbackDetail(this.tenantId, { params })
        this.pageDetail = detail
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
        page: this.pageId
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
