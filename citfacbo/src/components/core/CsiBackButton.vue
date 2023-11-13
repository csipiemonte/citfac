<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <router-link :to="to">
    <i class="bi bi-arrow-left"></i>
  </router-link>
 
</template>

<script>
export default {
  name: 'CsiBackButton',
  props: {
    to: Object,
   
  },
  data(){
    return {
    
    }
  },
  computed: {
    textColor() {
      return `'text-${this.color}'`
    }
  }
}
</script>

