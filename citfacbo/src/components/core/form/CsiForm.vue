<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>

 <form class="csi-form">
<slot></slot>
 </form>
</template>

<script>
//COMPONENTE PER AVERE SFONDO E SPAZIATURE DEL FORM
// PER UTILIZZARE IL COMPONENTE USARE I PROPS
//title" TITOLO DEL FORM,
export default {
  name: 'CsiForm',
  props: {
    title: String,
    noPadding:Boolean
  },


}
</script>

