<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <footer class="csi-footer">
    <div class="container">
      <slot></slot>
      <template v-if="!hideActions">
        <CsiSeparator color="white" />
        <div class="footer-actions-wrapper">
          <slot name="actions"></slot>
        </div>
        
      </template>
    </div>
  </footer>
</template>

<script>
import CsiSeparator from './CsiSeparator.vue'

export default {
  name: 'CsiFooter',
  props: {
    hideActions: Boolean
  },
  components: { CsiSeparator }
}
</script>

