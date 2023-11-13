<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="csi-toolbar " :class="classes">
    <div class="container h-100 w-100">
      <div class="w-100 d-flex justify-content-between align-items-center ">
        <slot />
      </div>
  
    </div>
  </div>
</template>

<script>
export default {
  name: 'CsiToolbar',
  props: {
    classes: {}
  },
  computed: {}
}
</script>
