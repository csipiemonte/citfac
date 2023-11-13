<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="alert csi-alert d-flex align-items-start" role="alert" :class="typeClass">
    <svg
      xmlns="http://www.w3.org/2000/svg"
      viewBox="0 0 24 24"
      class="notify-icon flex-shrink-0 me-3"
    >
      <path :d="$svgs.getAlertSvg(type)"></path>
    </svg>
    <div>
      <slot></slot>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CsiAlert',
  props: {
    type: { type: String, default: 'info' } //[success, warning, info, danger]
  },

  computed: {
    typeClass() {
      return `csi-alert__${this.type}`
    }
  }
}
</script>
