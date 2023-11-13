<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <main id="main-container" class="payment-spontaneous-success container mb-5">
    <breadcrumbs :links="BREADCRUMBS_LINK"></breadcrumbs>
    <cf-page-section class="mb-4">
      <h1 class="alert-heading h2 pb-2">Esito di pagamento</h1>
      <template v-if="resultCode === RESULT_CODE_MAP.SUCCESS">
        <div class="alert alert-success my-3 text-center" role="alert">
          <div>
            <p>
              <strong>{{ resultDesc }}</strong> <br />
              <strong>Riferimento IUV: {{ paymentId }}</strong>
            </p>
          </div>

          <div>
            <p>
              Siamo in attesa di ricevere la Ricevuta Telematica contenente
              l'esito della transazione di pagamento. <br />
              La comunicazione dell'esito e la Ricevuta Telematica saranno
              inviati alla casella di posta che ci hai indicato.
            </p>
          </div>
        </div>
      </template>

      <template v-if="resultCode === RESULT_CODE_MAP.FAILED">
        <div class="alert alert-warning my-3 text-center" role="alert">
          <p>
            <strong>ATTENZIONE! Operazione annullata!</strong> <br />
            <strong>Riferimento IUV: {{ paymentId }}</strong>
          </p>

          <p>
            Siamo in attesa di ricevere la Ricevuta Telematica contenente
            l'esito della transazione di pagamento. <br />
            La comunicazione dell'esito e la Ricevuta Telematica saranno inviati
            alla casella di posta che ci hai indicato.
          </p>
        </div>
      </template>


      <!-- @TODO Aggiungi link a Area personale -->
      <div class="d-flex justify-content-end">
        <router-link :to="PERSONAL_AREA"  class="btn btn-primary">Vai all'area personale</router-link>
      </div> 

   
    </cf-page-section>
  </main>

  <!--    SEZIONE VALUTAZIONI-->
  <valutation-section :asset="ASSETS_MAP.PAYMENTS" ></valutation-section>

  <!--    SEZIONE FISSA DEI CONTATTI-->
  <contacts-section></contacts-section>
</template>

<script>
import CfPageSection from "../../components/generic/CfPageSection.vue";

import Breadcrumbs from "../../components/generic/Breadcrumbs.vue";
import { format_date } from "../../services/utils";
import ValutationSection from "../../components/generic/ValutationSection.vue";
import ContactsSection from "../../components/generic/ContactsSection.vue";
import {  PERSONAL_AREA } from "../../router/routes";
import { getDebtPositions } from "../../services/api";
import { LINK_TYPES_MAP, ASSETS_MAP } from '../../services/config';

const BREADCRUMBS_LINK = [
 
  { url: "servizi", name: "Servizi", data_element: "all-services", type:LINK_TYPES_MAP.LINK },
  { url: "", name: "Esito di pagamento" },
];

const RESULT_CODE_MAP = {
  SUCCESS: "000",
  FAILED: "100",
};
export default {
  name: "PagePaymentFeedback",
  components: {
    ContactsSection,
    ValutationSection,
    Breadcrumbs,
    CfPageSection,
  },
  data() {
    return {
      PERSONAL_AREA,
      isLoading: false,
      BREADCRUMBS_LINK,
      RESULT_CODE_MAP,
      ASSETS_MAP,
      paymentId: null,
      resultDesc: null,
      resultCode: "",
      amount: null,
      requestDate: null,
      email: "",
      payment: null,
    };
  },
  computed: {
    user() {
      return this.$store.getters["getUser"];
    },
    userTaxCode() {
      return this.user?.codFisc ?? null;
    },
    comuniConfig() {
      return this.$store.getters["getComuniConfig"];
    },
    paymentDate() {},
  },
  async mounted() {
    scroll(0, 0);
    let {  iuv,  descEsito, codEsito } = this.$route.query;
    this.paymentId = iuv;
    this.resultCode = codEsito;
    this.resultDesc = descEsito;

    if (this.paymentId && this.userTaxCode) {
      this.isLoading = true;
      try {
        let params = {
          iuv: this.paymentId,
        };
        let { data } = getDebtPositions(this.userTaxCode, { params });
        let debtPositions = data?.debtPositions ?? [];

        this.payment = debtPositions[0];
      } catch (e) {}

      this.isLoading = false;
    }
  },
  created() {},
};
</script>


