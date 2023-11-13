<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="row align-items-stretch" >
    <div class="col-12 col-md ">
      <div class="card-wrapper cf-card-simple">
        <div class="card  bg-gray">
          <div class="card-body ">
            <p class="subtitle-small">Pagamento online tramite PAGOPA</p>
          </div>
          <div class="card-footer text-center p-4" style="border:none">
            <router-link class="btn btn-primary" :to="SELF_PAYMENT_ONLINE" type="button">
              Paga online tramite PAGOPA
            </router-link>
          </div>
        </div>
      </div>

    </div>
    <div class="col-12 col-md ">
      <div class="card-wrapper cf-card-simple">
        <div class="card bg-gray">
          <div class="card-body ">
            <p class="subtitle-small">Genera e stampa un avviso di pagamento che ti servirà per pagare presso i
              centri autorizzati (sportelli ATM, i punti vendita SISAL, Lottomatica e Banca 5, e gli uffici
              postali.)</p>
          </div>
          <div class="card-footer text-center p-4" style="border:none">
            <router-link class="btn btn-primary" :to="SELF_PAYMENT_NOTICE" type="button">
              Genera avviso di pagamento
            </router-link>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script>
import {SELF_PAYMENT_NOTICE, SELF_PAYMENT_ONLINE} from "../../router/routes";
import { ASSETS_MAP } from '../../services/config';

export default {
  name: "PagePaymentHome",
  data(){
    return{
      SELF_PAYMENT_NOTICE,
      SELF_PAYMENT_ONLINE,
    }
  },
  created(){
    if(!this.isActivePage){
    
     window.open(this.getComuneUrl, '_self')
      return
    }
  },
  computed:{
    getComuneUrl() {
      let comuneConfig = this.$store.getters["getComuniConfig"];
      return comuneConfig?.homePage ?? "";
    },
    isActivePage() {
      let assetCodeList = this.$store.getters["getAssetCodeList"];
      return assetCodeList?.includes(ASSETS_MAP.PAYMENTS)
    },
  }
}
</script>

