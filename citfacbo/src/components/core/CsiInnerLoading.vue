<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="d-flex w-100 justify-content-center csi-inner-loading p-3" v-if="showing" >
      <div class="spinner-border" :class="[small ? 'small' : 'big', textColor]">
        <span class="visually-hidden">Caricamento...</span>
      </div>
  </div>
</template>

<script>
export default {
  name: "CsiInnerLoading",
  props:{
    showing:Boolean,
    small:Boolean,
    color:{type:String, default: "primary"}
  },
  computed:{
    textColor(){
      return `text-${this.color}`
    },
    size(){

    }
  }
}
</script>
