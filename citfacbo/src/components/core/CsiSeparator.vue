<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <hr class="csi-separator" :class="textColor" />
</template>

<script>
export default {
  name: 'CsiSeparator',
  props: {
    color: String
  },

  computed: {
    textColor() {
      return `'text-${this.color}'`
    }
  }
}
</script>

