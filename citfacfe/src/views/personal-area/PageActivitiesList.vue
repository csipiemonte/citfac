<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="row">
    <div class="d-none d-sm-none d-lg-block col-lg-3">
      <cf-page-index id="activities" :menu-items="navActivities" inline />
    </div>

    <div class="col-12 col-lg-8 offset-lg-1">
      <!--   PRATICHE  -->
      <!--  -------------------------------------------------------------------------------------------------------- -->
      <instance-list-section :loading-instances="loadingInstances" />

      <!--   PAGAMENTI  -->
      <!--  -------------------------------------------------------------------------------------------------------- -->
      <payment-list-section
        v-if="showPayments"
        :loading-payments="loadingPayments"
      />

      <!--   APPUNTAMENTI  -->
      <!--  -------------------------------------------------------------------------------------------------------- -->
      <appointment-list-section
        v-if="showAppointments"
        :loading-appointments="loadingAppointments"
      />

      <!--   RICHIESTE ASSISTENZA  -->
      <!--  -------------------------------------------------------------------------------------------------------- -->
      <ticket-list-section
        :loading-tickets="loadingTickets"
        v-if="showAssistanceTickets"
      />

      <!--   BOZZE TICKET-->
      <!--  -------------------------------------------------------------------------------------------------------- -->
      <partial-ticket-list-section v-if="showAssistanceTickets" />
    </div>
  </div>
</template>

<script>
import CfPageIndex from "../../components/generic/CfPageIndex.vue";
import FilterSearchButtons from "../../components/personal-area/FilterSearchButtons.vue";
import CfInnerLoading from "../../components/generic/CfInnerLoading.vue";
import TicketCard from "../../components/personal-area/assistance-requests/TicketCard.vue";
import {
  getTicketList,
} from "../../services/api";
import { NEW_REQUEST, PERSONAL_AREA_DESKTOP } from "../../router/routes";
import PartialTicketCard from "../../components/personal-area/assistance-requests/PartialTicketCard.vue";
import {  LIMIT_ACTVITIES } from "../../services/config";
import CfSelect from "../../components/form/CfSelect.vue";

import AppointmentListSection from "../../components/personal-area/appointments/AppointmentListSection.vue";
import PaymentListSection from "../../components/personal-area/payments/PaymentListSection.vue";
import InstanceListSection from "../../components/personal-area/instances/InstanceListSection.vue";
import TicketListSection from "../../components/personal-area/assistance-requests/TicketListSection.vue";
import PartialTicketListSection from "../../components/personal-area/assistance-requests/PartialTicketListSection.vue";

const NAV_ACTIVITIES = [
  {
    target: "pratiche",
    nome: "Pratiche",
  },
  {
    target: "pagamenti",
    nome: "Pagamenti",
  },
  {
    target: "appuntamenti",
    nome: "Appuntamenti",
  },
  {
    target: "assistenza",
    nome: "Richieste di assistenza",
  },
];

export default {
  name: "PageActivitiesList",
  components: {
    CfSelect,
    PartialTicketCard,
    TicketCard,
    CfInnerLoading,
    FilterSearchButtons,
    CfPageIndex,
    AppointmentListSection,
    PaymentListSection,
    InstanceListSection,
    TicketListSection,
    PartialTicketListSection,
  },
  inject: [
    "hostname",
    "utente",
    "utenteIsLoading",
    "showActivities",
    "showAppointments",
    "showAssistanceTickets",
    "showPayments",
    "showInstances",
  ],
  emits: ["onRefreshTicket"],
  props: {
    loadingActivities: Boolean,
    loadingAppointments: Boolean,
    loadingPayments: Boolean,
    loadingInstances: Boolean,
    loadingTickets: Boolean,
    tickets: Object,
  },
  data() {
    return {
      NAV_ACTIVITIES,
      NEW_REQUEST,

      filteredPartialTicketList: [],
      isLoadingPartialTicket: false,
      partialTicketList: [],
    };
  },

  created() {
    if (!this.showActivities) {
      this.$router.replace(PERSONAL_AREA_DESKTOP);
      return;
    }
  },
  async mounted() {},
  computed: {
    navActivities() {
      let navList = [];

      if (this.showInstances) {
        navList.push({
          target: "pratiche",
          nome: "Pratiche",
        });
      }
      if (this.showPayments) {
        navList.push({
          target: "pagamenti",
          nome: "Pagamenti",
        });
      }

      if (this.showAppointments) {
        navList.push({
          target: "appuntamenti",
          nome: "Appuntamenti",
        });
      }

      if (this.showAssistanceTickets) {
        navList.push({
          target: "assistenza",
          nome: "Richieste di assistenza",
        });
      }

      return navList;
    },

    comuniServices() {
      return this.$store.getters["getComuniConfigServices"];
    },
    userTaxCode() {
      return this.utente?.codFisc;
    },
    moonAuth() {
      return this.$store.getters["getMoonAuth"];
    },

    idMoonToken() {
      return this.$store.getters["getMoonConfig"]?.idMoonToken;
    },
    assetsList() {
      return this.$store.getters["getAssetsList"];
    },
  },
  methods: {
    //RICHIESTE ASSISTENZA
    async onSearchTickets(value) {
      if (!value || value?.length === 0) {
        this.ticketsSearchText = null;
      } else {
        this.areTicketsFiltered = true;
        this.ticketsSearchText = value;
      }
      this.ticketList = [];
      this.ticketsPage = 1;
      this.isFilteringTickets = true;
      await this.fetchTicketsList();
      this.isFilteringTickets = false;
    },
    async onSortTickets(type, order) {
      this.ticketSortType = type;
      this.ticketSortOrder = order;
      this.ticketsPage = 1;
      this.isFilteringTickets = true;
      this.ticketList = [];
      await this.fetchTicketsList();
      this.isFilteringTickets = false;
    },

    refreshTicketList() {
      this.$emit("onRefreshTicket", this.ticketsPage);
    },
    async getMoreTickets() {
      this.isLoadingMoreTickets = true;
      await this.fetchTicketsList();
      this.isLoadingMoreTickets = false;
    },
    async fetchTicketsList() {
      let params = {
        per_page: LIMIT_ACTVITIES,
        page: this.ticketsPage,
      };
      //Stringa con il cf dell'utente
      let filter = `"customer.codice_fiscale":{"eq": "${this.userTaxCode}"}`;
      //Ricerca libero per titolo
      if (!!this.ticketsSearchText) {
        filter += `, "title":{"ci": "${this.ticketsSearchText}"}`;
        
      }
      params.filter = `{${filter}}`;

      //Ordinamento
      if (this.ticketSortType && this.ticketSortOrder) {
        params.sort_by = this.ticketSortType;
        params.order_by = this.ticketSortOrder;
      }

      try {
        let { data: tickets } = await getTicketList({ params });
        this.ticketList = [...this.ticketList, ...tickets];
        if (tickets.length < LIMIT_ACTVITIES) {
          this.areMoreTickets = false;
        } else {
          this.areMoreTickets = true;
          this.ticketsPage++;
        }
      } catch (e) {
        await this.$store.dispatch("setNotificationInfo", {
          body: "Non è stato possibile caricare l'elenco delle richieste di assistenza",
        });
      }
    },
    resetPartialTicketList() {
      this.partialTicketList = [];
      this.filteredPartialTicketList = [];
      this.morePartialTicketLabel = "Vedi altre bozze";
      this.showAllPartialTicket = false;
    },
    refreshPartialTicketList() {
      this.fetchPartialTickets();
    },
  },
};
</script>


