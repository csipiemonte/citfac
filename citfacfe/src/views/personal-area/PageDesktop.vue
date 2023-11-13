<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<!-- AREA PERSONALE - TAB SCRIVANIA -->
<template>
  <div class="row">
    <div class="col-12 col-lg-3 d-lg-block mb-4 d-none">
      <cf-page-index id="desk" :menu-items="navDesk" inline />
    </div>

    <div class="col-12 col-lg-8 offset-lg-1">
      <div
        v-if="showNotifications"
        id="u-messaggi"
        class="it-page-section mb-5"
      >
        <div class="cf-card">
          <div class="card">
            <div class="card-header border-0 p-0 m-0">
              <div class="d-flex">
                <h2 class="cf-card__main-title mb-3">Ultimi messaggi</h2>
              </div>
            </div>
            <template v-if="loadingMessages">
              <cf-inner-loading :showing="true" />
            </template>
            <template v-else-if="recentMessage && !recentMessage.deleted">
              <TransitionGroup name="list">
                <div :key="recentMessage.id" class="">
                  <messages-list-item
                    :id="recentMessage.id"
                    :message="recentMessage"
                    @on-delete="onDeleteMessage"
                    @on-read="onReadMessage"
                  />
                </div>
              </TransitionGroup>
            </template>
            <div v-else>
              <p>Non ci sono ancora notifiche</p>
            </div>
          </div>
        </div>
      </div>

      <div id="u-attivita" class="it-page-section mb-5" v-if="showActivities">
        <div class="cf-card">
          <div class="card">
            <div class="card-header border-0 p-0 m-0">
              <div class="d-flex">
                <h2 class="cf-card__main-title mb-3">Ultime attività</h2>
              </div>
            </div>

            <template v-if="loadingActivities">
              <cf-inner-loading :showing="true" />
            </template>
            <template v-else-if="hasRecentActivites">
              <TransitionGroup name="list">
                <div
                  v-for="(activity, index) in recentActivityList"
                  :key="index"
                  class="my-2"
                >
                  <ticket-card
                    v-if="activity.number"
                    :id="'activity' + index"
                    :ticket="activity"
                    @update-tickets="onUpdateTickets"
                    
                  ></ticket-card>
                  <instance-card
                    v-else-if="activity.codiceIstanza"
                    :id="'activity' + index"
                    :instance="activity"
              
                  ></instance-card>
                  <payment-card
                    v-else-if="activity.iuv"
                    :id="'activity' + index"
                    :payment="activity"
                    :user-cf="userTaxCode"
                  ></payment-card>
                  <appointment-card
                    v-else-if="activity.serviceId"
                    :id="'activity' + index"
                    :appointment="activity"
                  
                  ></appointment-card>
                </div>
              </TransitionGroup>
            </template>
            <div v-else>
              <p>Non ci sono ancora attività</p>
            </div>
          </div>
        </div>
      </div>

      <div id="servizi-p" class="it-page-section mb-5">
        <div class="cf-card">
          <div class="card">
            <div class="card-header border-0 p-0 m-0">
              <div class="d-flex">
                <h2 class="cf-card__main-title mb-3">Servizi preferiti</h2>
              </div>
            </div>

            <template v-if="loadingServices">
              <cf-inner-loading :showing="true" />
            </template>
            <template v-else-if="hasFavouritesServices">
              <TransitionGroup name="list">
                <div
                  v-for="(service, index) in favouritesServicesFullList"
                  :key="index"
                  class="my-2"
                >
                  <service-card
                    favourite-icon
                    @updateServiceList="refreshServiceList"
                    :key="index"
                    :service="service"
                  ></service-card>
                </div>
              </TransitionGroup>
            </template>
            <div v-else>
              <p>Non hai ancora servizi preferiti</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import CfPageIndex from "../../components/generic/CfPageIndex.vue";
import CfInnerLoading from "../../components/generic/CfInnerLoading.vue";
import MessagesListItem from "../../components/personal-area/MessagesListItem.vue";
import TicketCard from "../../components/personal-area/assistance-requests/TicketCard.vue";
import InstanceCard from "../../components/personal-area/instances/InstanceCard.vue";
import PaymentCard from "../../components/personal-area/payments/PaymentCard.vue";
import ServiceCard from "../../components/personal-area/ServiceCard.vue";
import AppointmentCard from "../../components/personal-area/appointments/AppointmentCard.vue";

const NAV_DESK = [
  {
    target: "u-messaggi",
    nome: "Ultimi messaggi",
  },
  {
    target: "u-attivita",
    nome: "Ultime attività",
  },
];

export default {
  name: "PageDesktop",
  components: {
    AppointmentCard,
    ServiceCard,
    PaymentCard,
    InstanceCard,
    TicketCard,
    MessagesListItem,
    CfInnerLoading,
    CfPageIndex,
  },
  inject: [
    "hostname",
    "utente",
    "utenteIsLoading",
    "showActivities",
    "showNotifications",
  ],
  emits: [
    "onRefreshTicket",
    "onDeleteMessage",
    "onReadMessage",
  

  ],
  data() {
    return {};
  },
  props: {
    loadingServices: Boolean,
    loadingMessages: Boolean,
    loadingAppointments: Boolean,
    loadingPayments: Boolean,
    loadingInstances: Boolean,
    loadingTickets: Boolean,
    messages: Object,
    services: Array,
    favouriteServicesIds: Array,
  },
  async mounted() {},
  computed: {
    appointments() {
      return this.$store.getters["getAppointmentList"];
    },
    instances() {
      return this.$store.getters["getInstanceList"];
    },
    payments() {
      return this.$store.getters["getPaymentList"];
    },
    tickets() {
      return this.$store.getters["getTicketList"];
    },
    loadingActivities() {
      return (
        this.loadingAppointments ||
        this.loadingPayments ||
        this.loadingTickets ||
        this.loadingInstances
      );
    },
    navDesk() {
      let nav = [];

      if (this.showNotifications) {
        nav.push({
          target: "u-messaggi",
          nome: "Ultimi messaggi",
        });
      }
      if (this.showActivities) {
        nav.push({
          target: "u-attivita",
          nome: "Ultime attività",
        });
      }

      nav.push({
        target: "servizi-p",
        nome: "Servizi preferiti",
      });

      return nav;
    },
    favouritesServicesFullList() {
      let array = [];
      this.services.forEach((a) => {
        if (this.favouriteServicesIds.includes(a.id)) {
          a.preferito = true;
          array.push(a);
        } else {
          a.preferito = false;
        }
      });
      return array;
    },

    hasFavouritesServices() {
      if (this.favouriteServicesIds.length > 0) {
        return true;
      }
      return false;
    },

    userTaxCode() {
      return this.utente?.codFisc;
    },
    moonAuth() {
      return this.$store.getters["getMoonAuth"];
    },
    hasRecentActivites() {
      return this.recentActivityList.some((a) => !a.deleted);
    },
    idMoonToken() {
      return this.$store.getters["getMoonConfig"]?.idMoonToken;
    },
    ticketsList() {
      return this.tickets?.tickets;
    },
    recentMessage() {
      let messages = this.messages?.messages?.filter((m) => !m.deleted);
      return messages[0];
    },
    instancesList() {
      return this.instances?.instances;
    },
    paymentsList() {
      return this.payments?.payments;
    },

    recentActivityList() {
      //Prendiamo un elemento per ogni attività
      let array = [];

      //Un elemento alla volta
      for (
        let i = 0;
        i <
        Math.max(
          this.ticketsList?.length,
          this.instancesList?.length,
          this.paymentsList?.length,
          this.appointments?.length
        );
        i++
      ) {
        if (this.ticketsList[i]) array.push(this.ticketsList[i]);
        if (this.instancesList[i]) array.push(this.instancesList[i]);
        if (this.paymentsList[i]) array.push(this.paymentsList[i]);
        if (this.appointments[i]) array.push(this.appointments[i]);
      }

      return array.slice(0, 4);
    },
  },
  methods: {
    onDeleteMessage(id) {
      this.$emit("onDeleteMessage", id);
    },
    onReadMessage(id) {
      this.$emit("onReadMessage", id);
    },
    refreshServiceList() {
      this.$emit("onRefreshService");
    },
   
    onUpdateTickets(){
       this.$store.dispatch("setTicketList");
    }

  },
};
</script>


