<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="px-2 my-4 container" v-if="showAlert">
    <csi-alert type="warning"  >
    Attenzione! I seguenti asset non sono configurati:
     <ul class="mb-0">
        <li v-for="asset in missingAssets" :key="asset.id" class="fw-bold">{{ asset.nome }}</li>
    </ul> 
    
    <div class="mt-2" v-if="!hideServiceLink">
      Vai a <router-link :to="SERVICES" class="fw-bold">Configurazione servizi</router-link>
    </div>
  
  </csi-alert>
  </div>
  
</template>

<script>
import CsiAlert from './core/CsiAlert.vue'
import {SERVICES} from '../router/routes'
import { ASSETS_MAP } from '../services/config'
const ASSETS_LIST = [
ASSETS_MAP.APPOINTMENTS, ASSETS_MAP.PAYMENTS
]
export default {
  components: { CsiAlert },
  name: 'AssetsMissingAlert',
  data(){
    return{
      SERVICES
    }
  },
  computed: {
    showAlert(){
      return this.$route.meta.showAssetMissingAlert && this.missingAssets.length>0
    },

    hideServiceLink() {
      return this.$route.path.includes(SERVICES.path)
    },
    tenant() {
      return this.$store.getters['getTenant']
    },

    assetsList() {
      let assets =  this.tenant?.assets ?? []

      return assets.filter(a => ASSETS_LIST.includes(a.codice))
    },
    servicves(){
      return this.$store.getters['getServicesList']
    },
    assetServicesList() {
      const services = this.$store.getters['getServicesList']
      const servicesWithAsset = services.filter((s) => !!s.asset)

      return servicesWithAsset.map((s) => s.asset.codice)
    },
    missingAssets() {
      return   this.assetsList.filter(a => !this.assetServicesList.includes(a.codice));
    
    }
  }
}
</script>
