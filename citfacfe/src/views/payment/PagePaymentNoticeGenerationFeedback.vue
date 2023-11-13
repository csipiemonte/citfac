<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>

    <main id="main-container" class="payment-notice-feedback container mb-5">
      <breadcrumbs :links="BREADCRUMBS_LINK"></breadcrumbs>
      <cf-page-section class="mb-4">
        <div class="alert alert-success my-3" role="alert">
          <h1 class="alert-heading h2 pb-2">Avviso di pagamento creato correttamente</h1>
          <p>L'avviso di pagamento è stato scaricato nella cartella download del tuo dispositivo o nella cartella impostata dal browser per salvare i files scaricati dal web.
        </p>
        </div>

        <div>
         
          <a class="btn btn-outline-primary btn-icon mt-3" :href="getComuneUrl">
            <span>Torna alla homepage</span>
          </a>
        </div>

      </cf-page-section>
    </main>

    <!--    SEZIONE VALUTAZIONI-->
    <valutation-section :asset="ASSETS_MAP.PAYMENTS"></valutation-section>

    <!--    SEZIONE FISSA DEI CONTATTI-->
    <contacts-section></contacts-section>


</template>

<script>

import CfPageSection from "../../components/generic/CfPageSection.vue";

import Breadcrumbs from "../../components/generic/Breadcrumbs.vue";
import {format_date} from "../../services/utils";
import ValutationSection from "../../components/generic/ValutationSection.vue";
import ContactsSection from "../../components/generic/ContactsSection.vue";

import { LINK_TYPES_MAP } from '../../services/config';

const BREADCRUMBS_LINK = [
  
  {url: "servizi", name: "Servizi" , data_element:"all-services", type:LINK_TYPES_MAP.LINK},
  {url: "", name: "Pagamento spontaneo"},
]
export default {
  name: "PagePaymentNoticeGenerationFeedback",
  components: {ContactsSection, ValutationSection, Breadcrumbs, CfPageSection},
  data() {
    return {
      BREADCRUMBS_LINK,
     
      paymentCode: null,
      requestCode: null,
      requestDesc: '',
      amount: null,
      requestDate: null,
      email: ''
    }
  },
  mounted(){
    scroll(0,0)
  },
  created() {
  },
  computed:{
    getComuneUrl() {
      let comuneConfig = this.$store.getters["getComuniConfig"];
      return comuneConfig?.homePage ?? "";
    },
  },
  methods:{
    onDownload(){

    }
  }
}
</script>

