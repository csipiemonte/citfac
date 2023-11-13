<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="row gap-3 align-items-stretch mt-3 dashboard-feedback-item-detail">
    <div class="col-12 col-md-6 col-lg-5 px-0">
      <div class="flex-column d-flex gap-4 align-items-stretch h-100">
        <!-- MEDIA RATING -->
        <!-- ------------------------------------------------ -->
        <csi-card bordered class="col" hide-footer>
          <template #body>
            <div class="text-center">
              <small>Media voti</small>
              <div class="average-rating">{{ averageRating }}</div>
              <dashboard-rating :rating="averageRating" class="pe-4"></dashboard-rating>
            </div>
          </template>
        </csi-card>

        <!-- TOTALE VOTI -->
        <!-- ------------------------------------------------ -->
        <csi-card bordered class="col" hide-footer>
          <template #body>
            <div class="text-center">
              <small>Voti raccolti</small>
              <div class="total-feedbacks">{{ totalFeedback }}</div>
            </div>
          </template>
        </csi-card>
      </div>
    </div>

    <!-- LISTA RATING -->
    <!-- ------------------------------------------------ -->

    <csi-card bordered class="col-12 col-md" hide-footer>
      <template #body>
        <dashboard-percent-bar
          v-for="r in ratingsMap"
          :key="r.rating"
          :total="totalFeedback"
          :title="r.label"
          :count="r.total"
          color="$primary"
          class-title="col-auto"
        ></dashboard-percent-bar>
      </template>
    </csi-card>

    <!-- ELENCO RISPOSTE -->
    <!-- ------------------------------------------------ -->
    <dashboard-answers-section
      class=""
      :averageRating="averageRating"
      :totalFeedback="totalFeedback"
      question="Quali sono stati gli aspetti che hai preferito / Dove hai incontrato le maggiori difficoltà?"
      id="questionPositive"
      :options="optionsList"
    >
    </dashboard-answers-section>
  </div>
</template>

<script>
import { FEEDBACK_ANSWERS_MAP } from '../../services/config'
import CsiAccordion from '../core/CsiAccordion.vue'
import CsiAlert from '../core/CsiAlert.vue'
import CsiCard from '../core/CsiCard.vue'
import DashboardAnswersSection from './DashboardAnswersSection.vue'

import DashboardPercentBar from './DashboardPercentBar.vue'
import DashboardRating from './DashboardRating.vue'
export default {
  components: {
    CsiCard,
    DashboardRating,
    DashboardAnswersSection,
    CsiAccordion,
    DashboardPercentBar,
    CsiAlert
  },
  name: 'DashboardFeedbackItemDetail',
  props: {
    options: { type: Array, default: () => [] },
    ratings: { type: Array, default: () => [] },
    averageRating: { type: Number, default: 0 },
    totalFeedback: { type: Number, default: 0 }
  },

  computed: {
    optionsMap() {
      return this.options?.reduce((obj, option) => {
        return { ...obj, [option.option]: option.total }
      }, {})
    },

    ratingsMap() {
      let ratings = []

      for (let i = 1; i <= 5; i++) {
        let rating = this.ratings.find((r) => r.rating === i) ?? { rating: i, total: 0 }
        let label = i === 1 ? 'stella' : 'stelle'
        ratings.push({ ...rating, ...{ label: `${i} ${label}` } })
      }

      return ratings
    },
    optionsList() {
      let mappedOptions = []

      if (this.optionsMap) {
        Object.keys(FEEDBACK_ANSWERS_MAP).forEach((key) => {
          mappedOptions.push({
            ...FEEDBACK_ANSWERS_MAP[key],
            ...{ value: this.optionsMap[key] ?? 0 }
          })
        })
      }

      return mappedOptions
    }

  
  }
}
</script>

