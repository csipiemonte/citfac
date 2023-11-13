<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
 
    <svg class="icon" :class="classes" aria-hidden="true">
      <use :href="src"></use>
    </svg>

</template>

<script>
export default {
  name: 'CsiIcon',
  props: {
    color: String,
    src: String,
    iconClass: {type:Array , default : ()=> []}
  },
  mounted() {
  
  },
  computed: {

    classes() {
      let result = this.iconClass 
        
      if (this.color) {
        result.push(`icon-${this.color}`)
      }

      return result
    }
  }
}
</script>
