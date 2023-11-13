<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="dashboard-percent-bar row align-items-center mb-4">
    <div :class="classTitle">{{ title }}</div>

    <div class="col-auto">
      <strong class="percent-text">{{ countLabel }} </strong>
    </div>
    <div class="col">
      <div
        class="progress"
        role="progressbar"
        :aria-label="title"
        :aria-valuenow="optionPercent"
        aria-valuemin="0"
        aria-valuemax="100"
        :style="{ height: progressHeight }"
      >
        <div
          class="progress-bar"
          :style="{ width: optionPercent + '%', backgroundColor: color }"
        ></div>
      </div>
    </div>
  </div>
</template>

<script>
import { calcPercent } from '../../services/business-logic'
const N_STARS = 5

const COUNT_TYPE_MAP = {
  TOTAL: 'total',
  PERCENT: 'percent'
}
export default {
  components: {},

  name: 'DashboardPercentBar',
  props: {
    rating: Number,
    total: Number,
    title: String,
    count: Number,
    color: String,
    progressHeight: String,
    classTitle: { type: String, default: 'col-12 col-md' },
    type: { type: String, default: COUNT_TYPE_MAP.PERCENT }
  },
  data() {
    return {}
  },
  created() {},
  computed: {
    optionPercent() {
      return calcPercent(this.count, this.total)
    },
    countLabel() {
      let label = this.optionPercent + '%'

      if (this.type === COUNT_TYPE_MAP.TOTAL) label = this.count

      return label
    }
  },
  methods: {}
}
</script>

