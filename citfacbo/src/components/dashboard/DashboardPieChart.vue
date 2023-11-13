<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="dashboard-pie-chart">
    <div style="max-width: 400px">
      <canvas :id="chartId" width="400" height="400"></canvas>
    </div>

  
  </div>
</template>

<script>
import { Chart, PieController, ArcElement } from 'chart.js/auto'

Chart.register(PieController, ArcElement)


export default {
  components: {},

  name: 'DashboardPieChart',
  props: {
    id: String,
    data: Object,
    total:Number
  },
  data() {
    return {}
  },
  mounted() {
    this.$nextTick(() => {
      let chart = document.getElementById(this.chartId)
      if (!chart) return

      new Chart(chart, {
        type: 'pie',
        data: this.data,
        options: {
          hoverBorderColor: true,
          // responsive: true,
          plugins: {
            legend: {
              display: false
            },
            tooltip: {
              callbacks: {
                label: function (context) {
                  let label = context.dataset.label || ' '

                  
                  if (context.parsed.y !== null) {
                    label +=  context.parsed
                  }
                  return label
                }
              }
            }
          }
        }
      })
    })
  },
  computed: {
    chartId() {
      return `chart_${this.id}`
    }
  },
  methods: {
    getOrCreateLegendList(chart, id) {
      const legendContainer = document.getElementById(id)
      let listContainer = legendContainer.querySelector('ul')

      if (!listContainer) {
        listContainer = document.createElement('ul')
        legendContainer.appendChild(listContainer)
      }

      return listContainer
    }
  }
}
</script>

