<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <header class="csi-header">
    <slot name="toolbar"> </slot>

    <div class="header-content-wrapper container">
      <slot></slot>
    </div>
  </header>
</template>

<script>
export default {
  name: 'CsiHeader',
  props: {}
}
</script>
