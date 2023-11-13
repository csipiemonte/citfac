<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  
  <csi-card
    class="dashboard-feedback-item my-3"
    :header="title"
    shadow
    rounded
    clickable
    hideFooter
    @onClick="onSelect"
  >
    <template #body>
      <div class="row align-items-start">
        <div class="col-12 col-md mt-2">
          <a @click.stop="" :href="pagePath" v-if="isPage" target="_blank">
            {{ pagePath }}
          </a>

          <p class="mb-0 mt-2">
            Numero voti raccolti: <strong class=""> {{ total }}</strong>
          </p>
        </div>
        <div class="text-end col-12 col-md d-flex align-items-center justify-content-end">
          <dashboard-rating :rating="rating" class="pe-4"></dashboard-rating>

          <div class="text-center average-rating">
            <small class="">Media voti</small>
            <h5 class="m-0">
              <strong class="text-secondary">{{ rating }}</strong>
            </h5>
          </div>
        </div>
      </div>
    </template>
  </csi-card>
</template>

<script>
import DashboardRating from './DashboardRating.vue'
import CsiCard from '../core/CsiCard.vue'
import { floatWithDecimals } from '../../services/business-logic'

const TYPES_MAP = {
  PAGE: 'page',
  SERVICE: 'service'
}

export default {
  components: { CsiCard, DashboardRating },
  emits: ['onSelect'],
  name: 'DashboardFeedbackItem',
  props: {
    item: Object,
    type: String
  },
  data() {
    return {
      TYPES_MAP
    }
  },
  computed: {
    isPage() {
      return this.type === TYPES_MAP.PAGE
    },
    title() {
      return this.item.pageTitle ?? '-'
    },
    serviceId() {
      return this.item.serviceId
    },
    pagePath() {
      return this.item.pagePath
    },
    rating() {
      return floatWithDecimals(this.item?.averageRating)
    },
    total() {
      return this.item.totalFeedback
    }
  },
  methods: {
    onSelect() {
      this.$emit('onSelect', this.item)
    }
  }
}
</script>

