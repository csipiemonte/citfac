<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <section id="partialTickets" class="it-page-section mb-5">
    <filter-search-buttons
      id="partialTickets"
      hidden-filters
      hide-search
      title="Bozze richieste di assistenza"
    >
    </filter-search-buttons>

    <!--    LOADER-->
    <template v-if="isLoadingPartialTicket">
      <cf-inner-loading :showing="isLoadingPartialTicket" class="p-2" />
    </template>

    <template v-else-if="partialTicketList.length > 0">
      <transition-group name="list">
        <div
          v-for="(partialTicket, index) in partialTicketList"
          :key="index"
          v-show="!partialTicket.deleted"
          class="my-2"
        >
          <partial-ticket-card
            :id="index"
            :partial-ticket="partialTicket"
            parent-id="accordion-4"
            @deleted-partial-ticket="onDeletePartialTicket"
          ></partial-ticket-card>
        </div>
      </transition-group>
      <show-more-less-button
        :show-more="areMorePartialTickets"
        show-more-label="Vedi altre bozze"
        show-less-label="Vedi meno bozze"
        :show-less="partialTicketList.length > LIMIT_ACTVITIES"
        @on-show-more="changePartialTicketsLength('+')"
        @on-show-less="changePartialTicketsLength('-')"
      />
    </template>

    <template v-else>
      <p class="ps-3 mt-4">Non ci sono bozze</p>
    </template>

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
import { getPartialTicketlist } from "../../../services/api";
import { ASSETS_MAP, LIMIT_ACTVITIES } from "../../../services/config";
import CfInnerLoading from "../../generic/CfInnerLoading.vue";
import FilterSearchButtons from "../FilterSearchButtons.vue";
import { NEW_REQUEST } from "../../../router/routes";
import PartialTicketCard from "./PartialTicketCard.vue";
import ShowMoreLessButton from '../ShowMoreLessButton.vue';
export default {
  components: { FilterSearchButtons, CfInnerLoading, PartialTicketCard, ShowMoreLessButton },
  name: "PartialTicketListSection",

  data() {
    return {
      NEW_REQUEST,
      LIMIT_ACTVITIES,
      partialTickets: [],
      partialTicketList: [],
      isLoadingPartialTicket: false,
      offset: LIMIT_ACTVITIES,
    };
  },
  async mounted() {
    this.isLoadingPartialTicket = true;
    await this.fetchPartialTickets();
    this.isLoadingPartialTicket = false;
  },
  computed: {
    user() {
      return this.$store.getters["getUser"];
    },
    userTaxCode() {
      return this.user?.codFisc ?? null;
    },
    assetsList() {
      return this.$store.getters["getAssetsList"];
    },
    areMorePartialTickets() {
      return this.partialTickets?.length > this.partialTicketList?.length;
    },
  },
  methods: {
    async fetchPartialTickets() {
      try {
       
        let idAsset = this.assetsList.find(
          (a) => a.codice === ASSETS_MAP.ASSISTANCE
        )?.id;

        let params = { asset: idAsset };
        let { data: tickets } = await getPartialTicketlist(params);

        this.partialTickets = tickets;
        this.partialTicketList = this.partialTickets.slice(0, this.offset);
      } catch (e) {
        await this.$store.dispatch("setNotificationInfo", {
          title: "Attenzione",
          body: "Non è stato possibile recuperare le bozze di richiesta di assistenza",
        });
      }
    },
    onDeletePartialTicket(id) {
      this.partialTicketList = this.partialTicketList.map((ticket) => {
        if (ticket.id === id) {
          ticket.deleted = true;
        }
        return ticket;
      });

      setTimeout(() => {
        this.fetchPartialTickets();
      }, 200);
    },

    changePartialTicketsLength(type) {
      if (type === "-") {
        this.offset -= LIMIT_ACTVITIES;
      } else {
        this.offset += LIMIT_ACTVITIES;
      }

      this.partialTicketList = this.partialTickets.slice(0, this.offset);
    },
  },
};
</script>
