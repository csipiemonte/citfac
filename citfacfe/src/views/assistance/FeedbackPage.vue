<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<script>
import ContactsSection from "../../components/generic/ContactsSection.vue";

import Breadcrumbs from "../../components/generic/Breadcrumbs.vue";
import {
  LOGIN_URL,
  PARTIAL_TICKET,
  TICKET,
  TICKET_NO_AUTH,
} from "../../services/business-logic";
import TitleSection from "../../components/generic/TitleSection.vue";
import {login} from "../../services/utils";
import { ASSETS_MAP } from "../../services/config";

export default {
  
  name: "FeedBackPage",
  components: { TitleSection, Breadcrumbs, ContactsSection },
  data() {
    return {
      LOGIN_URL,
      email: null,
      type: null,
      breadcrumbsLinks: [
       
        { url: "", name: "Assistenza" },
      ],
      ASSETS_MAP
    };
  },
  async created() {
    scroll(0, 0);

    this.type = this.$route.query.feedbackType;
    this.email = this.$route.query.email;
  },
  computed: {
    title() {
      if (this.isTicket || this.isTicketNoAuth) {
        return "Richiesta inviata";
      }
      if (this.isPartialTicket) {
        return "Bozza salvata";
      } else return "";
    },
    isTicket() {
      if (this.$route.query.feedbackType === TICKET) {
        return true;
      }
      return false;
    },
    isTicketNoAuth() {
      if (this.$route.query.feedbackType === TICKET_NO_AUTH) {
        return true;
      }
      return false;
    },
    isPartialTicket() {
      if (this.$route.query.feedbackType === PARTIAL_TICKET) {
        return true;
      }
      return false;
    },
  },
  methods: {
    async goToLogin(){

     login()
    }
  },
  async mounted() {},
};
</script>

<template>
  <breadcrumbs :links="breadcrumbsLinks"></breadcrumbs>
  <title-section :title="title"></title-section>
  <main>
    <section id="intro" class="container px-4 my-4">
      <div class="row px-lg-4">
        <template v-if="isTicketNoAuth">
          <div class="col-12">
            La richiesta di assistenza è stata inviata con successo, sarai
            ricontattato presto.
          </div>
          <div class="col-12">
            Abbiamo inviato il riepilogo alla email:
            <strong>{{ email }}</strong>
          </div>
        </template>
        <template v-if="isTicket">
          <div class="col-12">
            La richiesta di assistenza è stata inviata con successo, sarai
            ricontattato presto.
          </div>

          <div class="col-12 pt-2">
            Puoi consultare o modificae la tua richiesta di assistenza accedendo
            alla tua
            <RouterLink
                    class="text-primary"
                    to="/area-personale"
                    data-element="personal-area-login"
            ><span>Area riservata</span></RouterLink
            >
          </div>
        </template>

        <template v-if="isPartialTicket">
          <div class="col-12">
            La bozza di richiesta di assistenza è stata salvata con successo,.
          </div>

          <div class="col-12 pt-2">
            Puoi modificare ed inviare la bozza accedendo alla tua
            <RouterLink
                    class="dropdown-item list-item text-primary"
                    to="/area-personale"
                    data-element="personal-area-login"
            ><span>Area riservata</span></RouterLink
            >
          </div>
        </template>
      </div>
    </section>



    <!--    SEZIONE FISSA DEI CONTATTI-->
    <contacts-section></contacts-section>
  </main>
</template>


