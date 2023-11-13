<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <main id="main-container" class="container mb-5">
    <cf-title-section :breadcrumbs="BREADCRUMBS" subtitle class="pb-4 pb-lg-5">
      <template #title> Profilo utente {{ userFullname }} </template>

      <template #subtitle> CF: {{ userTaxCode }} </template>
    </cf-title-section>

    <!--   CON PROFILO -->
    <!------------------------------------------------------------------------------->
    <section
      v-if="hasActiveProfile"
      class="container it-page-sections-container"
    >
      <div class="row">
        <div class="col-12 col-lg-4 d-lg-block mb-4">
          <cf-page-index :menu-items="sectionList" id="contacts" inline />
        </div>

        <div class="col-12 col-lg-8">
          <div
            :id="PAGE_SECTIONS.CONTACTS"
            class="it-page-section mb-5"
          >
            <user-contacts-card
              :email="userContacts.email"
              :telephone="userContacts.sms"
              has-contacts
              @on-update-email="setEmail"
              @on-update-telephone="setTelephone"
            />
          </div>

          <div
            :id="PAGE_SECTIONS.PREFERENCES"
            class="it-page-section mb-5"
          >
            <preferences-card actions />
          </div>
        </div>
      </div>
    </section>
    <!--  SENZA PROFILO -->
    <!------------------------------------------------------------------------------->
    <section v-else class="container">
      <div class="row justify-content-center mb-4">
        <div class="col-12 col-lg-10">
          <div class="p-3 border-start complementary-2-border-color-b2">
            <h5 class="text-warning mb-3">
              <svg aria-hidden="true" class="icon icon-warning">
                <use
                :xlink:href="$svgs.getSpriteSvgUrl('it-horn')"
                
                ></use>
              </svg>
              <span class="complementary-2-color ms-2"
                ><strong>ATTENZIONE</strong></span
              >
            </h5>
            <p>
              Non hai ancora creato il tuo profilo personale. <br />

              Inserisci i tuoi contatti e specifica le preferenze di notifica
              per ricevere informazioni importanti dal tuo comune.
            </p>

            <button
              class="btn btn-primary text-uppercase"
              type="button"
              @click="createNewProfile"
            >
              Componi il profilo personale
            </button>
          </div>
        </div>
      </div>
    </section>
  </main>

  <!--    SEZIONE FISSA DEI CONTATTI-->
  <contacts-section></contacts-section>
</template>

<script>
import { setNotifyPreferences } from "../../services/business-logic";
import CfTitleSection from "../../components/generic/CfTitleSection.vue";
import UserContactsCard from "../../components/personal-area/profile/UserContactsCard.vue";
import { saveUserContacts } from "../../services/api";

import PreferencesCard from "../../components/personal-area/profile/PreferencesCard.vue";
import CfPageIndex from "../../components/generic/CfPageIndex.vue";
import ContactsSection from "../../components/generic/ContactsSection.vue";
import { PERSONAL_AREA, USER_PROFILE } from "../../router/routes";

const PAGE_SECTIONS = {
  CONTACTS: "contatti",
  PREFERENCES: "preferenze",
};

const BREADCRUMBS = [
  {
    url: PERSONAL_AREA.path,
    name: "Area Personale",
    data_element: "personal-area-login",
  },
  { url: USER_PROFILE.path, name: "Profilo utente" },
];
export default {
  name: "PageProfile",
  components: {
    ContactsSection,
    CfPageIndex,
    PreferencesCard,
    UserContactsCard,
    CfTitleSection,
  },
  data() {
    return {
      PAGE_SECTIONS,
      BREADCRUMBS,
      sectionList: [
        {
          target: PAGE_SECTIONS.CONTACTS,
          nome: "Contatti",
        },
        {
          target: PAGE_SECTIONS.PREFERENCES,
          nome: "Preferenze",
          subMenu: [
            {
              target: "preferenze-notifiche",
              nome: "Notifiche",
            },
            {
              target: "preferenze-pagamenti",
              nome: "Pagamenti",
            },
          ],
        },
      ],
      activeSection: PAGE_SECTIONS.CONTACTS,
      notificationAlert: {
        title: "Attenzione",
        body: "",
        positive: false,
      },
    };
  },
  computed: {
    user() {
      return this.$store.getters["getUser"];
    },
    userTaxCode() {
      return this.user?.codFisc ?? null;
    },
    userFullname() {
      let name = this.user?.nome;
      let surname = this.user?.cognome;

      return name && surname ? `${name} ${surname}` : null;
    },
    userContacts() {
      return this.user?.contatti ?? null;
    },
    hasActiveProfile() {
      return !!this.userContacts?.email;
    },
  },
  async created() {},
  methods: {
    onSelectSection(target) {
      this.activeSection = target;
    },
    createNewProfile() {
      this.$router.push({ name: "profilo-utente-nuovo" });
    },
    setEmail(email) {
      let payload = {
        email: email,
        sms: this.userContacts.sms,
      };
      this.setUserContacts(payload);
    },
    setTelephone(telephone) {
      let payload = {
        sms: telephone,
        email: this.userContacts.email,
      };
      this.setUserContacts(payload);
    },

    async setUserContacts(payload) {
      try {
        let { data: contacts } = await saveUserContacts(
          this.userTaxCode,
          payload
        );
        this.$store.dispatch("setUserContacts", { contacts });

        await this.$store.dispatch("setNotificationInfo", {
          body: "Contatti modificati con successo.",
          positive: true,
        });
      } catch (e) {
        await this.$store.dispatch("setNotificationInfo", {
          body: "Non è stato possibile modificare i contatti, si prega di riprovare.",
        });
      }
    },
  },
};
</script>

