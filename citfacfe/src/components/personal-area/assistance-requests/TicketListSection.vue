<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <section id="assistenza" class="it-page-section mb-5">
    <filter-search-buttons
      id="tickets"
      :hidden-filters="!areTicketsFiltered && ticketList.length <= 0"
      hide-form
      title="Richieste di assistenza"
      @on-search="onSearchTickets"
    >
      <template #dropdownList>
        <ul class="link-list">
          <li>
            <a
              class="dropdown-item list-item"
              @click="onSortTickets('created_at', 'desc')"
              ><span>Data creazione più recente</span></a
            >
          </li>
          <li>
            <a
              class="dropdown-item list-item"
              @click="onSortTickets('created_at', 'asc')"
              ><span>Data creazione meno recente</span></a
            >
          </li>
          <li>
            <a
              class="dropdown-item list-item"
              @click="onSortTickets('number', 'asc')"
              ><span>Numero richiesta</span></a
            >
          </li>
        </ul>
      </template>

  
    </filter-search-buttons>

    <!--    LOADER-->
    <template v-if="loadingTickets || isFilteringTickets">
      <cf-inner-loading :showing="true" class="p-2" />
    </template>
    <template v-else-if="ticketList.length > 0">
      <transition-group name="list">
        <div v-for="(ticket, index) in ticketList" :key="index" class="my-2">
          <ticket-card
            :id="index"
            :ticket="ticket"
            parent-id="accordion-3"
            @update-tickets="onUpdateTickets"
          ></ticket-card>
        </div>
      </transition-group>

      <show-more-less-button
        :show-more="areMoreTickets"
        show-more-label="Vedi altre richieste"
        show-less-label="Vedi meno richieste"
        :show-less="areLessTickets"
        @on-show-more="getMoreTickets"
        @on-show-less="getLessTickets"
        :loading-more="isLoadingMoreTickets"
        :loading-less="isLoadingLessTickets"
      />
    </template>
    <div v-else>
      <p class="ps-3 mt-4">
        <template v-if="areTicketsFiltered"
          >Non ci sono richieste in base ai filtri di ricerca
        </template>

        <template v-else>Non ci sono ancora richieste</template>
      </p>
    </div>

    <div class="row flex-row-reverse">
      <div class="col-12 col-md-6">
        <router-link
          :to="NEW_REQUEST"
          class="w-100 btn btn-primary"
          type="button"
        >
          RICHIEDI ASSISTENZA
        </router-link>
      </div>
    </div>
  </section>
</template>

<script>
import { getTicketList } from "../../../services/api";
import { LIMIT_ACTVITIES } from "../../../services/config";
import CfInnerLoading from "../../generic/CfInnerLoading.vue";
import FilterSearchButtons from "../FilterSearchButtons.vue";
import TicketCard from "./TicketCard.vue";
import { NEW_REQUEST } from "../../../router/routes";
import ShowMoreLessButton from "../ShowMoreLessButton.vue";
export default {
  components: {
    FilterSearchButtons,
    CfInnerLoading,
    TicketCard,
    ShowMoreLessButton,
  },
  name: "TicketListSection",
  props: {
    loadingTickets: { type: Boolean, default: false },
  },
  data() {
    return {
      areTicketsFiltered: false,
      NEW_REQUEST,
      ticketList: [],

      areMoreTickets: false,
      ticketsPage: 1, // la prima chiamata è già stata fatta dalla homepage
      isLoadingMoreTickets: false,
      filteredPartialTicketList: [],
      isLoadingPartialTicket: false,
      isLoadingLessTickets: false,
      partialTicketList: [],
      ticketsSearchText: null,
      isFilteringTickets: false,
      ticketSortType: null,
      ticketSortOrder: null,
    };
  },
  watch: {
    tickets: {
      deep: true,
      immediate: true,
      handler(val, oldval) {
        let defaultTickets = this.tickets?.tickets ?? [];
        this.ticketList = [...defaultTickets];
        this.ticketsPage = this.tickets?.nPage ?? 1;
        this.areMoreTickets = defaultTickets.length >= LIMIT_ACTVITIES;
      },
    },
  },
  computed: {
    tickets() {
      return this.$store.getters["getTicketList"];
    },
    user() {
      return this.$store.getters["getUser"];
    },
    userTaxCode() {
      return this.user?.codFisc ?? null;
    },
    areLessTickets() {
      return this.ticketList.length > LIMIT_ACTVITIES;
    },
  },
  methods: {
    async onSearchTickets(value) {
      if (!value || value?.length === 0) {
        this.ticketsSearchText = null;
      } else {
        this.areTicketsFiltered = true;
        this.ticketsSearchText = value;
      }
      this.ticketList = [];
      this.ticketsPage = 0;
      this.isFilteringTickets = true;
      await this.fetchTicketsList();
      this.isFilteringTickets = false;
    },
    async onSortTickets(type, order) {
      this.ticketSortType = type;
      this.ticketSortOrder = order;
      this.ticketsPage = 0;
      this.isFilteringTickets = true;
      this.ticketList = [];
      await this.fetchTicketsList();
      this.isFilteringTickets = false;
    },


    async getMoreTickets() {
      this.isLoadingMoreTickets = true;
      await this.fetchTicketsList();
      this.isLoadingMoreTickets = false;
    },
    async getLessTickets() {
      this.isLoadingLessTickets = true;

      this.ticketList = this.ticketList.slice(0, -LIMIT_ACTVITIES);
      this.ticketsPage--;
      this.isLoadingLessTickets = false;
    },
    async fetchTicketsList(params) {
      if (!params) {
        params = {
          per_page: LIMIT_ACTVITIES,
          page: this.ticketsPage + 1,
        };
      }

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
    async onUpdateTickets() {
      let params = {
        per_page: LIMIT_ACTVITIES * this.ticketsPage,
        page: 1,
        //Stringa con il cf dell'utente
        filter: `{"customer.codice_fiscale":{"eq": "${this.userTaxCode}"}}`,
      };

      await this.$store.dispatch("setTicketList", params);

      if (
        !!this.ticketsSearchText ||
        (this.ticketSortType && this.ticketSortOrder)
      ) {
        await this.fetchTicketsList(params);
      }
    },
  },
};
</script>
