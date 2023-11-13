<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="accordion px-0 dashboard-answer-section">
    <csi-accordion expand-default :id="id" type="h4" v-if="options?.length">
      <template #header>{{ question }}</template>

      <template #body>
        <dashboard-percent-bar
          v-for="(option, index) in options"
          :key="index"
          :total="totalFeedback"
          :title="option.label"
          :count="option.value"
          :color="option.color"
          progress-height="32px"
        ></dashboard-percent-bar>

        <div class="mt-4">
          <dashboard-pie-chart :id="id" :data="chartData"></dashboard-pie-chart>
        </div>
      </template>
    </csi-accordion>
  </div>
</template>

<script>
import CsiAccordion from '../core/CsiAccordion.vue'
import DashboardPercentBar from './DashboardPercentBar.vue'
import DashboardPieChart from './DashboardPieChart.vue'

export default {
  components: { CsiAccordion, DashboardPieChart, DashboardPercentBar },

  name: 'DashboardAnswersSectionOld',
  props: {
    id: [String, Number],
    question: String,
    type: { type: String, default: 'h2' },
    options: Array,
    averageRating: Number,
    totalFeedback: Number
  },
  data() {
    return {}
  },
  created() {},
  computed: {
    chartData() {
      return {
        labels: this.options.map((option) => option.label),
        datasets: [
          {
            data: this.options.map((option) => option.value),

            backgroundColor: this.options.map((option) => option.color)
          }
        ]
      }
    }
  },
  methods: {}
}
</script>

