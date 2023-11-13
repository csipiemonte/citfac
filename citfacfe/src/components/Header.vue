<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<script>
import CfDropdown from "./generic/CfDropdown.vue";

import { LOGIN_URL, LOGOUT_URL } from "../services/business-logic";
import { login, logout } from "../services/utils";
import InputText from "./form/InputText.vue";
import CfModal from "./generic/CfModal.vue";

export default {
  name: "Header",
  components: { CfDropdown, InputText, CfModal },
  data() {
    return {
      devLabel: "DevLogout",
      LOGIN_URL,
      LOGOUT_URL,
      searchText: "",
    };
  },
  methods: {
    async goToLogout() {
      logout();
    },
    async goToLogin() {
      login();
    },
    devLogout() {
      if (this.getDevLogout === true) {
        this.$router.push({ name: "home" });
        this.$store.commit("setDevLogout", false);
        this.devLabel = "DevLogOut";
      } else {
        this.$router.push({ name: "home" });
        this.$store.commit("setDevLogout", true);
        this.devLabel = "DevLogIn";
      }
    },
    onSearch() {
      if (this.searchText.length > 0) {
        let url = `${this.getComuneUrl}/ricerca?search_api_fulltext=${this.searchText}`;

        window.open(url, "_self");
      }
    },
  },
  mounted: function () {},
  computed: {
    getComuneImgUri() {
      let code = this.getComuneConfig?.codiceImmagine?.toLowerCase();
      return new URL(`../assets/images/logo/${code}.png`, import.meta.url).href;
    },
    getComuneConfig() {
      return this.$store.getters["getComuniConfig"];
    },
    getComuneUrl() {
      return this.getComuneConfig?.homePage ?? "";
    },

    getComuneConfigMenu() {
      return this.$store.getters["getComuniConfigMenu"];
    },
    getComuneConfigMenuMain() {
      return this.getComuneConfigMenu?.main ?? [];
    },
    getComuneConfigMenuTopics() {
      let menu = [];
      if (this.getComuneConfigMenu) {
        menu = this.getComuneConfigMenu["menu-argomenti"] ?? [];
      }
      return menu;
    },
    getComuneName() {
      return this.getComuneConfig?.nome;
    },
    getRegioneName() {
      return this.getComuneConfig?.regione?.nome;
    },
    getRegioneLink() {
      return this.getComuneConfig?.regione?.link;
    },

    getDevLogout() {
      return this.$store.getters["getDevLogout"];
    },



    hideMenu() {
      let meta = this.$route?.meta;

      return meta?.hideMenu;
    },
    logoSrc() {
      return new URL("/statics/img/logo-96.png", import.meta.url);
    },
    hasCms(){
      return this.getComuneConfig?.cms
    }
  },
  props: {
    msg: {
      type: String,
      required: false,
    },
  },
  inject: ["comune", "hostname", "utente", "utenteIsLoading"],
};
</script>

<template>
  <div class="skiplink">
    <a class="visually-hidden-focusable" href="#main-container"
      >Vai ai contenuti</a
    >
    <a class="visually-hidden-focusable" href="#footer">Vai al footer</a>
  </div>
  <!-- /skiplink -->
  <header
    class="it-header-wrapper"
    data-bs-target="#header-nav-wrapper"
    style=""
  >
    <div class="it-header-slim-wrapper">
      <div class="container">
        <div class="row">
          <div class="col-12">
            <div class="it-header-slim-wrapper-content">
              <a
                :href="getRegioneLink"
                :aria-label="`Vai al portale ${getRegioneName} - link esterno - apertura nuova scheda`"
                class="d-lg-block navbar-brand"
                target="_blank"
                :title="`Vai al portale ${getRegioneName}`"
              >
                <!--                  {{ comune?.regione?.titolo }}-->
                Regione {{ getRegioneName }}
              </a>
              <div class="it-header-slim-right-zone" role="navigation">
     
                <a
                  v-if="utenteIsLoading || !utente || getDevLogout"
                  class="btn btn-primary btn-icon btn-full"
                  data-element="personal-area-login"
                  title="Accedi all'area personale"
                  :href="LOGIN_URL"
                  @click.prevent="goToLogin"
                >
                  <span aria-hidden="true" class="rounded-icon">
                    <svg class="icon icon-primary">
                      <use :xlink:href="$svgs.getSpriteSvgUrl('it-user')"></use>
                    </svg>
                  </span>
                  <span class="d-none d-lg-block"
                    >Accedi all'area personale</span
                  >
                </a>
                <cf-dropdown
                  v-else
                  id="user-btn"
                  button-classes="btn-icon btn-full"
                  class="nav-item"
                  type="a"
                  hide-icon
                >
                  <template #button>
                    <span class="rounded-icon">
                      <svg class="icon icon-primary m-0">
                        <use
                          :xlink:href="$svgs.getSpriteSvgUrl('it-user')"
                        ></use>
                      </svg>
                    </span>
                    <span class="d-none d-lg-block"
                      >{{ utente.nome }} {{ utente.cognome }}</span
                    >
                  </template>

                  <template #list>
                    <li>
                      <RouterLink
                        class="dropdown-item list-item"
                        to="/area-personale"
                        data-element="personal-area-login"
                        ><span>Area personale</span></RouterLink
                      >
                    </li>
                    <li>
                      <RouterLink
                        class="dropdown-item list-item"
                        to="/area-personale/profilo-utente"
                        ><span>Profilo</span></RouterLink
                      >
                    </li>
                    <li>
                      <a
                        class="list-item left-icon"
                        :href="LOGOUT_URL"
                        @click.prevent="goToLogout"
                      >
                        <span> Esci</span>
                      </a>
                    </li>
                  </template>
                </cf-dropdown>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="it-nav-wrapper">
      <div class="it-header-center-wrapper">
        <div class="container">
          <div class="row">
            <div class="col-12">
              <div class="it-header-center-content-wrapper">
                <div class="it-brand-wrapper">
                  <a
                    title="Vai alla homepage"
                    :href="getComuneUrl"
                    :aria-label="`homepage del comune di ${getComuneName}`"
                  >
                    <img
                      :src="logoSrc"
                      height="80"
                      style="margin-right: 10px"
                      width="80"
                    />
                    <div class="it-brand-text">
                      <h2 class="it-brand-title">
                        {{ getComuneName }}
                      </h2>
                    </div>
                  </a>
                </div>
                <div v-if="!hideMenu && hasCms" class="it-right-zone">
                  <div class="it-search-wrapper">
                    <span class="d-none d-md-block">Cerca</span>
                    <button
                      aria-label="Cerca nel sito"
                      class="search-link rounded-icon"
                      data-bs-target="#search-modal"
                      data-bs-toggle="modal"
                      type="button"
                    >
                      <svg class="icon">
                        <use
                          :xlink:href="$svgs.getSpriteSvgUrl('it-search')"
                        ></use>
                      </svg>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div
        v-if="!hideMenu"
        id="header-nav-wrapper"
        class="it-header-navbar-wrapper"
      >
        <div class="container">
          <div class="row">
            <div class="col-12">
              <!--start nav-->
              <nav
                aria-label="Navigazione principale"
                class="navbar navbar-expand-lg has-megamenu px-0"
              >
                <button
                  aria-controls="nav4"
                  aria-expanded="false"
                  aria-label="Mostra/Nascondi la navigazione"
                  class="custom-navbar-toggler"
                  data-bs-target="#nav4"
                  data-bs-toggle="navbarcollapsible"
                  role="navigation"
                  type="button"
                >
                  <svg class="icon">
                    <use :href="$svgs.getSpriteSvgUrl('it-burger')"></use>
                  </svg>
                </button>
                <div id="nav4" class="navbar-collapsable">
                  <div class="overlay" style="display: none"></div>
                  <div class="close-div">
                    <button class="btn close-menu" type="button">
                      <span class="visually-hidden"
                        >Nascondi la navigazione</span
                      >
                      <svg class="icon">
                        <use
                          :xlink:href="$svgs.getSpriteSvgUrl('it-close-big')"
                        ></use>
                      </svg>
                    </button>
                  </div>
                  <div class="menu-wrapper">
                    <a
                      class="logo-hamburger"
                      :href="getComuneUrl"
                      title="Vai alla homepage"
                      :aria-label="`homepage del comune di ${getComuneName}`"
                    >
                      <svg aria-hidden="true" class="icon">
                        <use :xlink:href="$svgs.getSpriteSvgUrl('it-pa')"></use>
                      </svg>
                      <div class="it-brand-text">
                        <div class="it-brand-title">{{ getComuneName }}</div>
                      </div>
                    </a>
                    <ul class="navbar-nav" data-element="main-navigation">
                      <li
                        v-for="(menuElement, index) in getComuneConfigMenuMain"
                        class="nav-item"
                        :key="index"
                      >
                        <a
                          :href="menuElement.url"
                          class="nav-link"
                          data-element="management"
                        >
                          <span>{{ menuElement.title }}</span>
                        </a>
                      </li>
            
                    </ul>
                    <ul
                      class="navbar-nav navbar-secondary  "
                    >
                      <li
                        class="col-auto nav-item"
                        v-for="(topic, index) in getComuneConfigMenuTopics"
                        :key="index"
                      >
                        <a
                          class="nav-link py-0"
                          data-element="all-topics"
                          :href="topic.url"
                        >
                          <span :class="{'fw-bold' : index === getComuneConfigMenuTopics.length-1 }">{{ topic.title }}</span>
                        </a>
                      </li>
                    </ul>
                 
                  </div>
                </div>
              </nav>
            </div>
          </div>
        </div>
      </div>
    </div>
  </header>

  <cf-modal
    no-action
    id="search-modal"
    ref="searchModal"
    class="modal-lg"
    maximized
  >
    <div class="main container">
      <div class="row justify-content-center">
        <div class="col-12 col-lg-10">
          <h2 class="modal-title text-black pb-4">
            Esplora tutti i contenuti del sito
          </h2>
          <form novalidate @submit.prevent="onSearch">
            <input-text
              v-model="searchText"
              prepend
              append
              labelText="Cerca per parola chiave"
            >
              <template #prepend>
                <svg class="icon">
                  <use :xlink:href="$svgs.getSpriteSvgUrl('it-search')"></use>
                </svg>
              </template>

              <template #button>
                <button class="btn btn-primary py-0 px-3" type="submit">
                  <span class="">Cerca</span>
                </button>
              </template>
            </input-text>
          </form>
        </div>
      </div>
    </div>
  </cf-modal>
</template>
