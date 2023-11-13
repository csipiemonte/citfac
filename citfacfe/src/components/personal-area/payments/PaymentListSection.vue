<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <section id="pagamenti" class="it-page-section mb-5">
    <filter-search-buttons
      id="payments"
      :hidden-filters="!arePaymentsFiltered && paymentList.length <= 0"
      title="Pagamenti"
      @on-filter="onFilterPayments"
      @on-cancel-filter="onCancelPaymentsFilters"
      @on-search="onSearchPayments"
    >
      <template #dropdownList>
        <ul class="link-list">
          <li>
            <a class="dropdown-item list-item" @click="onSortPayment('-')"
              ><span>Data scadenza più recente</span></a
            >
          </li>
          <li>
            <a class="dropdown-item list-item" @click="onSortPayment('+')"
              ><span>Data scadenza meno recente</span></a
            >
          </li>
        </ul>
      </template>

      <template #filterContent>
        <form id="formDocuments" class="row pt-3">
          <cf-select
            id="payment-status"
            v-model="paymentStatus"
            :option-list="PAYMENT_STATUS_LIST"
            class="col-12"
            clearable
            label-text="Stato pagamento"
          />
        </form>
      </template>
    </filter-search-buttons>

    <!--    LOADER-->
    <template v-if="loadingPayments || isFilteringPayments">
      <cf-inner-loading showing class="p-2" />
    </template>
    <template v-else-if="filteredPaymentList.length > 0">
      <transition-group name="list">
        <div
          v-for="(payment, index) in filteredPaymentList"
          :key="index"
          class="my-2"
        >
          <payment-card :id="index" :payment="payment" :user-cf="userTaxCode" />
        </div>
      </transition-group>

      <show-more-less-button
        :show-more="areMorePayments"
        show-more-label="Vedi altri pagamenti"
        show-less-label="Vedi meno pagamenti"
        :show-less="areLessPayments"
        @on-show-more="getPayments"
        @on-show-less="getLessPayments"
        :loading-more="isLoadingMorePayments"
      />
    </template>
    <div v-else>
      <p class="ps-3 mt-4">
        <template v-if="arePaymentsFiltered"
          >Non ci sono pagamenti in base ai filtri di ricerca
        </template>

        <template v-else>Non ci sono ancora pagamenti</template>
      </p>
    </div>

    <div class="row flex-row-reverse">
      <div class="col-12 col-md-6">
        <router-link
          :to="SERVICE_SELF_PAYMENT"
          class="w-100 btn btn-primary"
          type="button"
        >
          EFFETTUA PAGAMENTO SPONTANEO
        </router-link>
      </div>
    </div>
  </section>
</template>

<script>
import CfSelect from "../../form/CfSelect.vue";
import CfInnerLoading from "../../generic/CfInnerLoading.vue";
import FilterSearchButtons from "../FilterSearchButtons.vue";
import PaymentCard from "./PaymentCard.vue";
import { SERVICE_SELF_PAYMENT } from "../../../router/routes";
import { LIMIT_ACTVITIES } from "../../../services/config";
import { getDebtPositions } from "../../../services/api";
import ShowMoreLessButton from "../ShowMoreLessButton.vue";
const PAYMENT_STATUS_LIST = [

  { id: "PAYED", name: "Pagato", _value: "0" },
  { id: "NOT_PAYED", name: "Non pagato", _value: "1" },
];

export default {
  components: {
    FilterSearchButtons,
    CfSelect,
    CfInnerLoading,
    PaymentCard,
    ShowMoreLessButton,
  },
  name: "PaymentListSection",

  props: {
    loadingPayments: { type: Boolean, default: false },
  },
  data() {
    return {
      PAYMENT_STATUS_LIST,
      SERVICE_SELF_PAYMENT,
      arePaymentsFiltered: false,
      paymentList: [],
      filteredPaymentList: [],
      totalPaymentElements: 0,
      isLoadingMorePayments: false,
      nPagePayments: 1, // la prima chiamata è già stata fatta dalla homepage
      paymentStatus: null,
      paymentsSort: null,
      isFilteringPayments: false,
    };
  },
  watch: {
    loadingPayments: {
      immediate: true,
      handler(val, oldval) {
        if (!val) {
          let defaultPayments = this.payments?.payments ?? [];
          this.paymentList = [...defaultPayments];
          this.filteredPaymentList = [...defaultPayments];
          this.totalPaymentElements = this.payments?.total;
        }
      },
    },
  },
  computed: {
    payments() {
      return this.$store.getters["getPaymentList"];
    },
    areMorePayments() {
      return this.totalPaymentElements > this.paymentList?.length;
    },
    areLessPayments() {
      return this.paymentList?.length > LIMIT_ACTVITIES;
    },
    user() {
      return this.$store.getters["getUser"];
    },
    userTaxCode() {
      return this.user?.codFisc ?? null;
    },
  },
  methods: {
    async onFilterPayments() {
      this.nPagePayments = 0;
      this.arePaymentsFiltered = true;
      this.paymentList = [];
      this.isFilteringPayments = true;
      await this.getPayments();
      this.isFilteringPayments = false;
    },
    async onCancelPaymentsFilters() {
      this.paymentList = [];
      this.nPagePayments = 0;
      this.arePaymentsFiltered = false;
      this.paymentsSort = null;
      this.paymentStatus = null;
      this.isFilteringPayments = true;
      await this.getPayments();
      this.isFilteringPayments = false;
    },
    onSearchPayments(value) {
      if (value && value.length > 0) {
        this.filteredPaymentList = this.paymentList.filter((p) => {
          let uiv = p.iuv?.toLowerCase() ?? "";
          let description = p.descrizionePagamento?.toLowerCase() ?? "";
          let noticeCode = p.codiceAvviso?.toLowerCase() ?? "";
          return (
            uiv.search(value.toLowerCase()) >= 0 ||
            description.search(value.toLowerCase()) >= 0 ||
            noticeCode.search(value.toLowerCase()) >= 0
          );
        });
        this.arePaymentsFiltered = true;
      } else {
        this.filteredPaymentList = this.paymentList;
        this.arePaymentsFiltered = false;
      }
    },
    async onSortPayment(type) {
      this.paymentsSort = `${type}dataScadenza`;

      this.arePaymentsFiltered = true;
      this.nPagePayments = 0;
      this.paymentList = [];
      this.isFilteringPayments = true;
      await this.getPayments();
      this.isFilteringPayments = false;
    },

    getLessPayments() {
      this.paymentList = this.paymentList.slice(0, -LIMIT_ACTVITIES);
      this.filteredPaymentList = this.paymentList;
      this.nPagePayments--;
    },

    async getPayments() {
      this.isLoadingMorePayments = true;
      let params = {
        currentPage: this.nPagePayments + 1,
        elements: LIMIT_ACTVITIES,
        sort: this.paymentsSort ?? "-dataScadenza",
      };
      if (this.paymentStatus) params.status = this.paymentStatus;

      try {
        let { data } = await getDebtPositions(this.userTaxCode, { params });

        this.paymentList = [...this.paymentList, ...data?.debtPositions];
        this.filteredPaymentList = this.paymentList;
        this.totalPaymentElements = data?.totalElements;
        this.nPagePayments += 1;
      } catch (e) {
        await this.$store.dispatch("setNotificationInfo", {
          body: "Non è stato possibile caricare l'elenco dei pagamenti",
        });
      }

      this.isLoadingMorePayments = false;
    },
  },
};
</script>
