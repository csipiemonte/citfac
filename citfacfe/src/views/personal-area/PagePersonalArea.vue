<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <main v-if="!utente">
    <breadcrumbs :links="BREADCRUMBS_LINKS"></breadcrumbs>

    <section id="intro" class="container px-4 my-4">
      <cf-alert title="Attenzione!">
        <p class="mb-0">Utente non trovato</p>
      </cf-alert>

      <a class="btn btn-outline-primary btn-icon mt-3" :href="getComuneUrl">
        <span>Torna alla homepage</span>
      </a>
    </section>
  </main>
  <main v-else>
    <cf-title-section
      :breadcrumbs="BREADCRUMBS_LINKS"
      class="pb-3 pb-lg-4"
      extras
      subtitle
    >
      <template #title> {{ utente?.nome }} {{ utente?.cognome }} </template>
      <template #extras>
        <router-link
          :to="USER_PROFILE"
          class="btn btn-icon m-0 p-0"
          id="btn-profile"
        >
          <span class="rounded-icon">
            <svg class="icon">
              <use :xlink:href="$svgs.getSpriteSvgUrl('it-user')"></use>
            </svg>
          </span>
          <span class="btn-profile__label">Profilo</span>
        </router-link>
      </template>

      <template #subtitle> CF: {{ userTaxCode }} </template>
    </cf-title-section>

    <div id="main-container" class="container">
      <!-- Se l'utente non ha contatti -->
      <template v-if="!hasContacts">
        <div class="alert alert-warning" role="alert">
          <div class="row align-items-center">
            <p class="col-12 col-md mb-md-0">
              Attenzione: per utilizzare i servizi online è necessario indicare
              il tuo indirizzo e-mail nel profilo personale.
            </p>

            <div class="col-12 col-md-auto text-end">
              <router-link
                :to="USER_PROFILE_NEW"
                class="btn btn-outline-primary"
                >Vai al profilo</router-link
              >
            </div>
          </div>
        </div>
      </template>

      <div class="row">
        <div class="col-12 p-0">
          <nav id="personal-area-nav" class="cf-nav-tabs mb-4 mb-lg-5 mt-lg-4">
            <ul
              class="pb-10 nav nav-tabs text-paragraph-small nav-tabs-icon-text nav-fill"
              role="tablist"
            >
              <li class="nav-item" role="tab">
                <router-link
                  :to="PERSONAL_AREA_DESKTOP"
                  aria-current="page"
                  aria-selected="false"
                  class="nav-link justify-content-start"
                  exact
                  exact-active-class="active focus--mouse "
                  data-focus-mouse="false"
                  has-context-menu="true"
                  role="button"
                >
                  <svg aria-hidden="true" class="icon me-1">
                    <use :xlink:href="$svgs.getSpriteSvgUrl('it-pa')"></use>
                  </svg>
                  Scrivania
                </router-link>
              </li>
              <li class="nav-item" role="tab" v-if="showNotifications">
                <router-link
                  :to="MESSAGES_LIST"
                  aria-current="page"
                  aria-selected="false"
                  class="nav-link justify-content-start"
                  data-focus-mouse="false"
                  exact
                  exact-active-class="active focus--mouse"
                  has-context-menu="true"
                  role="button"
                >
                  <svg aria-hidden="true" class="icon me-1">
                    <use :xlink:href="$svgs.getSpriteSvgUrl('it-mail')"></use>
                  </svg>
                  Messaggi

                  <div
                    v-if="totalUnreadMessages > 0"
                    :aria-label="`${totalUnreadMessages} notifiche non lette`"
                    class="badge bg-danger messages-not-read-badge"
                  >
                    {{ totalUnreadMessages }}
                  </div>
                </router-link>
              </li>
              <li class="nav-item" role="tab" v-if="showActivities">
                <router-link
                  :to="ACTIVITIES_LIST"
                  aria-current="page"
                  aria-selected="false"
                  class="nav-link justify-content-start"
                  data-focus-mouse="false"
                  exact
                  exact-active-class="active focus--mouse"
                  has-context-menu="true"
                  role="button"
                >
                  <svg aria-hidden="true" class="icon me-1">
                    <use :xlink:href="$svgs.getSpriteSvgUrl('it-files')"></use>
                  </svg>
                  Attività
                </router-link>
              </li>
              <li class="nav-item" role="tab" v-if="showDocuments">
                <router-link
                  :to="DOCUMENTS_LIST"
                  aria-current="page"
                  aria-selected="false"
                  class="nav-link justify-content-start"
                  data-focus-mouse="false"
                  exact
                  exact-active-class="active focus--mouse"
                  has-context-menu="true"
                  role="button"
                >
                  <svg aria-hidden="true" class="icon me-1">
                    <use :xlink:href="$svgs.getSpriteSvgUrl('it-box')"></use>
                  </svg>
                  Documenti
                </router-link>
              </li>
              <li class="nav-item" role="tab">
                <router-link
                  :to="SERVICES_LIST"
                  aria-current="page"
                  aria-selected="false"
                  class="nav-link justify-content-start"
                  data-focus-mouse="false"
                  exact
                  exact-active-class="active focus--mouse"
                  has-context-menu="true"
                  role="button"
                >
                  <svg aria-hidden="true" class="icon me-1">
                    <use
                      :xlink:href="$svgs.getSpriteSvgUrl('it-settings')"
                    ></use>
                  </svg>
                  Servizi
                </router-link>
              </li>
            </ul>
          </nav>
        </div>
      </div>

      <div class="it-page-sections-container">
        <!-- Tab panels -->
        <div class="tab-content">
          <!-- SCRIVANIA -->
          <!-- -------------------------------------------------------------------------------------------------- -->
          <div class="tab-pane fade show active" role="tabpanel">
            <router-view
              :loading-payments="isLoadingPayments"
              :loading-appointments="isLoadingAppointments"
              :loading-instances="isLoadingInstances"
              :loading-tickets="isLoadingTickets"
              :loading-messages="isLoadingMessages"
              :loading-services="isLoadingServices"
              :messages="messageList"
              :services="serviceList"
              :favourite-services-ids="favouriteServiceListIds"
              @on-delete-message="onDeleteMessage"
              @on-read-message="onReadMessage"
              @on-refresh-service="onRefreshService"
            />
          </div>
        </div>
      </div>
    </div>
  </main>
  <contacts-section></contacts-section>
</template>

<script>
import Breadcrumbs from "../../components/generic/Breadcrumbs.vue";
import CfTitleSection from "../../components/generic/CfTitleSection.vue";
import { computed } from "vue";
import {
  ACTIVITIES_LIST,
  DOCUMENTS_LIST,
  MESSAGES_LIST,
  PERSONAL_AREA_DESKTOP,
  SERVICES_LIST,
  USER_PROFILE,
  USER_PROFILE_NEW,
} from "../../router/routes";
import {
  getFavouriteService,
  getMoonIdentity,
  getNotifications,
  getTicketList,
} from "../../services/api";
import ContactsSection from "../../components/generic/ContactsSection.vue";
import { LIMIT_ACTVITIES, ASSETS_MAP } from "../../services/config";
import CfAlert from "../../components/generic/CfAlert.vue";

const BREADCRUMBS_LINKS = [{ url: "", name: "Area personale" }];

export default {
  name: "PagePersonalArea",
  components: { ContactsSection, CfTitleSection, Breadcrumbs, CfAlert },
  inject: ["hostname", "utente", "utenteIsLoading"],
  provide() {
    return {
      showAppointments: computed(() => this.showAppointments),
      showAssistanceTickets: computed(() => this.showAssistanceTickets),
      showPayments: computed(() => this.showPayments),
      showInstances: computed(() => this.showInstances),
      showActivities: computed(() => this.showActivities),
      showDocuments: computed(() => this.showDocuments),
      showNotifications: computed(() => this.showNotifications),
    };
  },
  data() {
    return {
      USER_PROFILE,
      DOCUMENTS_LIST,
      ACTIVITIES_LIST,
      USER_PROFILE_NEW,
      MESSAGES_LIST,
      PERSONAL_AREA_DESKTOP,
      BREADCRUMBS_LINKS,
      SERVICES_LIST,
      isLoadingMessages: false,
      isLoadingPayments: false,
      isLoadingAppointments: false,
      isLoadingInstances: false,
      isLoadingTickets: false,
      isLoadingServices: false,

      messageList: {
        messages: [],
        unread: 0,
        total: 0,
      },

      ticketsList: {
        tickets: [],
        nPage: 1,
      },
      favouriteServiceListIds: [],
      serviceList: [],
      favouriteFullServiceList: [],
    };
  },
  computed: {
    getComuneUrl() {
      return this.getComuneConfig?.homePage ?? "";
    },
    userTaxCode() {
      return this.utente?.codFisc;
    },
    hasContacts() {
      return this.utente?.contatti?.email;
    },
    totalUnreadMessages() {
      return this.messageList?.unread;
    },
    moonAuth() {
      return this.$store.getters["getMoonAuth"];
    },
    getComuneConfig() {
      return this.$store.getters["getComuniConfig"];
    },
    getComuneConfigServices() {
      return this.$store.getters["getComuniConfigServices"];
    },
    idMoonToken() {
      return this.$store.getters["getMoonConfig"]?.idMoonToken;
    },
    assetCodeList() {
      return this.$store.getters["getAssetCodeList"];
    },
    showAppointments() {
      return this.assetCodeList?.includes(ASSETS_MAP.APPOINTMENTS);
    },
    showAssistanceTickets() {
      return this.assetCodeList?.includes(ASSETS_MAP.ASSISTANCE);
    },
    showPayments() {
      return this.assetCodeList?.includes(ASSETS_MAP.PAYMENTS);
    },
    showInstances() {
      return this.assetCodeList?.includes(ASSETS_MAP.INSTANCES);
    },
    showDocuments() {
      return this.assetCodeList?.includes(ASSETS_MAP.DOCUMENTS);
    },
    showNotifications() {
      return this.assetCodeList?.includes(ASSETS_MAP.NOTIFICATIONS);
    },
    showActivities() {
      return (
        !!this.showAppointments ||
        !!this.showPayments ||
        !!this.showAssistanceTickets ||
        !!this.showInstances
      );
    },
    appointmentList() {
      return this.$store.getters["getAppointmentList"];
    },
    paymentsList() {
      return this.$store.getters["getPaymentList"];
    },
    instancesList() {
      return this.$store.getters["getInstanceList"];
    },
  },
  mounted() {
    if (!this.utente) return;
    if (this.showNotifications) {
      this.isLoadingMessages = true;
      this.fetchMessages();
    }
    //this.isLoadingActivities = true;
    this.fetchActivities();
    this.fetchServices();
  },
  methods: {
    onRefreshService() {
      this.fetchServices();
    },
    async reloadFavouriteServices() {
      try {
        let promiseFavourite = await getFavouriteService();
      } catch (e) {
        await this.$store.dispatch("setNotificationInfo", {
          body: "Non è stato possibile caricare la lista dei servizi",
        });
      }
    },
    async fetchServices() {
      try {
        this.isLoadingServices = true;
        let promiseFavourite = await getFavouriteService();
        this.serviceList = this.getComuneConfigServices;
        this.favouriteServiceListIds = promiseFavourite.data;
      } catch (e) {
        await this.$store.dispatch("setNotificationInfo", {
          body: "Non è stato possibile caricare la lista dei servizi",
        });
      }
      this.isLoadingServices = false;
    },

    async fetchActivities() {
      // TICKETS
      if (this.showAssistanceTickets) {
        this.fetchTickets();
      }

      // PAGAMENTI
      if (this.showPayments) {
        this.fetchPayments();
      }

      //APPUNTAMENTI
      if (this.showAppointments) {
        this.fetchAppointments();
      }

      // PRATICHE
      if (this.showInstances) {
        this.fetchInstances();
      }
    },

    async fetchMessages() {
      let filters = `"tag":{"match": "+citfac ${this.getComuneConfig.codiceEnte} -deleted" }`;

      try {
        let params = {
          offset: 0,
          limit: 15,
          filter: `{${filters}}`,
        };

        let response = await getNotifications(this.userTaxCode, params);
        let messages = response?.data;
        const unread = response.headers["total-elements-not-read"];
        const total = response.headers["total-elements"];
        this.messageList = { messages, unread, total };
        //  await this.$store.dispatch('setMessageList', {messages, messagesUnread})
      } catch (e) {
        await this.$store.dispatch("setNotificationInfo", {
          body: "Non è stato possibile ottenere i messaggi",
        });
      }
      this.isLoadingMessages = false;
    },
    onDeleteMessage(id) {
      let message = this.messageList.messages?.find((m) => m.id === id);

      if (message) {
        this.fetchMessages();
      }
    },
    onReadMessage(id) {
      this.messageList.messages = this.messageList.messages.map((n) => {
        if (n.id === id) {
          n.read_at = new Date();
        }
        return n;
      });

      this.messageList.unread--;
    },

    async onRefreshTicket(nPage) {
      this.isLoadingTickets = true;
      //ricarico la lista dei ticket attualmente visibile
      let limit = LIMIT_ACTVITIES * nPage;
      try {
        let params = {
          per_page: limit,
          page: 1,
          //Stringa con il cf dell'utente
          filter: `{"customer.codice_fiscale":{"eq": "${this.userTaxCode}"}}`,
        };
        let { data: tickets } = await getTicketList({ params });

        this.ticketsList = {
          tickets: tickets,
          nPage: nPage,
        };
      } catch (e) {
        await this.$store.dispatch("setNotificationInfo", {
          body: "Non è stato possibile caricare l'elenco delle richieste di assistenza",
        });
      }
      this.isLoadingTickets = false;
    },
    async fetchInstances() {
      this.isLoadingInstances = true;
      if (!this.moonAuth) {
        try {
          let promise = await getMoonIdentity();
          let moonIdentity = promise.data;
          await this.$store.commit("setMoonConfig", moonIdentity);
        } catch (e) {
          await this.$store.dispatch("setNotificationInfo", {
            body: "Non è stato possibile ottenere i dati Moon",
          });
        }
      }

      await this.$store.dispatch("setInstanceList");

      this.isLoadingInstances = false;
    },
    async fetchAppointments() {
      this.isLoadingAppointments = true;
      await this.$store.dispatch("setAppointmentList");
      this.isLoadingAppointments = false;
    },
    async fetchPayments() {
      this.isLoadingPayments = true;
      await this.$store.dispatch("setPaymentList");
      this.isLoadingPayments = false;
    },
    async fetchTickets() {
      this.isLoadingTickets = true;

      await this.$store.dispatch("setTicketList");
      this.isLoadingTickets = false;
    },
  },
};
</script>
