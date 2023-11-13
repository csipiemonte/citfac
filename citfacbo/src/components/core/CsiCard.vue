<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <component
    :is="cardType"
    class="card csi-card"
    :class="classes"
    @click.prevent="onClick"
  >
    <div class="card-body" :class="{ 'p-0': noPadding }">
      <h5 v-if="header" class="card-title">{{ header }}</h5>
      <slot name="body"></slot>

      <div class="row gap-3  align-items-center flex-row-reverse px-3" v-if="!hideFooter">
        <slot name="footer"></slot>
      </div>
     
    </div>
  </component>
</template>

<script>
export default {
  name: 'CsiCard',
  emits: ['onClick'],
  props: {
    rounded: Boolean,
    shadow: Boolean,
    noPadding: Boolean,
    header: String,
    clickable: Boolean,
    hideFooter:Boolean
  },

  computed: {
    cardType() {
      return this.clickable ? 'a' : 'div'
    },
    classes() {
      let result = []
      if (this.rounded) {
        result.push('rounded')
      }
      if (this.shadow) {
        result.push('shadow')
      }
      if (this.clickable) {
        result.push('clickable')
      }

      return result
    }
  },
  methods: {
    onClick() {
      if (this.clickable) this.$emit('onClick')
    }
  }
}
</script>
