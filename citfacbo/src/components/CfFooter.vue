<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <CsiFooter class="text-white mt-5" hideActions>
    <div class="footer-logo-wrapper">
      <img
        class="ue-logo"
        style="min-width: 300px"
        src="@/assets/images/logo-eu-inverted.svg"
        alt="logo Unione Europea"
      />
      <div class="footer-brand-wrapper" v-if="tenant">
        <a href="#" :aria-label="'portale' + tenantName" class="logo-hamburger">
          <span aria-hidden="true" class="me-2">
           
            <img :src="logoSrc" height="80"  width="80"   />
          
          </span>
          <h2 class="brand-title">{{ tenantName }}</h2>
        </a>
      </div>
    </div>


  </CsiFooter>
</template>

<script>
import CsiFooter from './core/CsiFooter.vue'
import CsiIcon from './core/CsiIcon.vue'

export default {
  name: 'CfFooter',
  components: { CsiFooter, CsiIcon },

  props: {},

  computed: {
    tenant() {
      return this.$store.getters['getTenant']
    },
    tenantName() {
      return this.tenant?.nome ?? '-'
    },
    logoSrc() {
      return "/admin/statics/img/logo-96.png";
    },
  }
}
</script>

