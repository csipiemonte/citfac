<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="dashboard-feedback-item-comments">
    <!-- ELENCO COMMENTI -->
    <!-- ------------------------------------------------ -->
    <h4 class="detail-title mt-5">Commenti</h4>

    <template v-if="comments.length > 0">
      <ul class="list-group list-group-flush">
        <li class="list-group-item" v-for="(comment, index) in comments" :key="index">
          <div class="row align-items-center mt-2">
            <small class="text-gray col-auto">{{ formatCommentDate(comment.createdAt) }}</small>
            <dashboard-rating class="col" :rating="comment.rating" size="18px"></dashboard-rating>
          </div>
          <p class="my-2">{{ comment.details || '-' }}</p>
        </li>
      </ul>


      <csi-button v-if="moreComments" @click="$emit('load-more')" outline class="mt-3" :loading="loading"  >Carica altri</csi-button>
    </template>

    <template v-else-if="!loading">
      <csi-alert type="info" class="my-4">Nessun commento in base ai filtri di ricerca</csi-alert>
    </template>

    
  </div>
</template>

<script>
import { format } from 'date-fns'

import CsiAlert from '../core/CsiAlert.vue'
import CsiCard from '../core/CsiCard.vue'
import DashboardRating from './DashboardRating.vue'
import CsiButton from '../core/CsiButton.vue'
export default {
  components: {
    CsiCard,
    DashboardRating,
    CsiAlert,
    CsiButton
  },
  name: 'DashboardFeedbackItemComments',
  emits:['load-more'],
  props: {
    comments: { type: Array, default: () => [] },
    moreComments: Boolean,
    loading: Boolean
  },

  computed: {},
  methods: {
    formatCommentDate(commentDate) {
      let date = new Date(commentDate)
      return format(date, 'dd/MM/yyyy')
    }
  }
}
</script>

