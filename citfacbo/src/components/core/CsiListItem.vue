<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <li
    class="csi-list-item list-group-item w-100 d-flex justify-content-between align-items-center"
    :aria-current="active"
    :class="classes"
  >
    <slot></slot>

    <div class="col-auto d-flex justify-content-end align-items-center" v-if="sideActions">
      <slot name="right-side"></slot>
    </div>
  </li>
</template>

<script>
export default {
  name: 'CsiListItem',
  props: {
    action: Boolean,
    active: Boolean,
    sideActions:Boolean
  },

  computed: {
    classes() {
      let result = []
      if (this.action) {
        result.push('list-group-item__action')
      }
      if (this.active) {
        result.push('active')
      }
      return result
    }
  }
}
</script>

