<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="row  justify-content-center full-width overlay cf-inner-loading" v-if="showing" >
    <div class="col-auto">
      <div class="progress-spinner progress-spinner-active " :class="{'small' : small}">
        <span class="visually-hidden">Caricamento...</span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "CfInnerLoading",
  props:{
    showing:Boolean,
    small:Boolean
  }
}
</script>


