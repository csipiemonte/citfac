<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
<!--  ALERT CON CSS RICHIESTO PER CITTA FACILE-->
  <div class="cf-alert p-3 border-start  complementary-2-border-color-b2">
    <h5 class="text-warning mb-3">
      <svg aria-hidden="true" class="icon icon-warning ">
        <use 
        :xlink:href="$svgs.getSpriteSvgUrl('it-horn')"
       >
        </use>
      </svg>
      <span class="complementary-2-color ms-2 text-uppercase"><strong>{{title}}</strong></span>
    </h5>
   <div><slot></slot></div>


  </div>
</template>

<script>
export default {
name: "CfAlert",
  props:{
  title:String
  },
  methods:{
    
  }
}
</script>


