<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div id="the-guard-maintenance">
    <!-- HEADER -->
    <header class="it-header-wrapper" data-bs-target="#header-nav-wrapper">
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
                  Regione {{ getRegioneName }}
                </a>
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
                  <div class="it-brand-wrapper p-0">
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
                        <!--                      <h2 class="it-brand-title">{{ comune?.titolo }}</h2>-->
                        <h2 class="it-brand-title">
                          {{ getComuneName }}
                        </h2>
                      </div>
                    </a>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </header>
    <!-- MAIN -->
    <main class="container my-4 px-md-5">
      <cf-page-section class="my-4 mx-1">
        <form
          id="auth-form"
          ref="AuthForm"
          class="needs-validation"
          @submit.prevent="onSubmit"
        >
          <form-bg no-padding title="Autenticazione">
            <div class="row gap-1 mt-4">
              <input-text
                id="username"
                v-model="username"
                label-text="Nome utente"
                more-info="Inserisci il nome utente"
                required
              ></input-text>

              <input-text
                id="password"
                v-model="password"
                label-text="Password"
                more-info="Inserisci la password"
                required
                type="password"
              ></input-text>
            </div>

            <div class="row mt-2 mb-4 justify-content-end">
              <div class="col-12 col-md-4">
                <button class="btn btn-primary w-100" type="submit">
                  Conferma
                  <span v-if="isLoadingAuthentication" class="ms-2">
                    <cf-inner-loading showing small />
                  </span>
                </button>
              </div>
            </div>
          </form-bg>
        </form>
      </cf-page-section>
    </main>

    <!-- FOOTER -->
    <footer class="it-footer" id="footer">
      <div class="it-footer-main">
        <div class="container">
          <div class="row">
            <div class="col-12 footer-items-wrapper logo-wrapper">
              <img
                class="ue-logo"
                style="min-width: 300px"
                src="../assets/images/logo-eu-inverted.svg"
                alt="logo Unione Europea"
              />
              <div class="it-brand-wrapper">
                <a
                  :href="getComuneUrl"
                  title="Vai alla homepage"
                  :aria-label="`homepage del comune di ${getComuneName}`"
                  class="logo-hamburger"
                >
                  <img :src="logoSrc" height="80" width="80" />
                  <div class="it-brand-text">
                    <h2 class="it-brand-title">{{ getComuneName }}</h2>
                  </div>
                </a>
              </div>
            </div>
          </div>
        </div>
      </div>
    </footer>
  </div>
</template>

<script>
import JustValidate from "just-validate";
import FormBg from "./form/FormBg.vue";
import InputText from "./form/InputText.vue";
import CfPageSection from "./generic/CfPageSection.vue";
import TitleSection from "./generic/TitleSection.vue";
import { MAINTENANCE_HASH_KEY } from "../services/config";
import CfInnerLoading from "./generic/CfInnerLoading.vue";
import { postMaintenanceCredentials } from "../services/api";

export default {
  components: {
    InputText,
    TitleSection,
    CfPageSection,
    FormBg,
    CfInnerLoading,
  },
  name: "TheGuardMaintenance",
  data() {
    return {
      username: null,
      password: null,
      formValidation: null,
      isLoadingAuthentication: false,
    };
  },
  mounted() {
    this.formValidation = new JustValidate("#auth-form", {
      validateBeforeSubmitting: true,
    });
  },
  computed: {
    getComuneConfig() {
      return this.$store.getters["getComuniConfig"];
    },
    getComuneUrl() {
      return this.getComuneConfig?.homePage ?? "";
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

    logoSrc() {
      return new URL("/statics/img/logo-96.png", import.meta.url);
    },
  },
  async mounted() {
    this.formValidation = new JustValidate("#auth-form");
  },
  methods: {
    async onSubmit() {
      let validation = this.formValidation;

      validation.addField("#inputTextusername", [
        {
          rule: "required",
          errorMessage: "Campo obbligatorio",
        },
      ]);

      validation.addField("#inputTextpassword", [
        {
          rule: "required",
          errorMessage: "Campo obbligatorio",
        },
      ]);

      //SE LA VALIDAZIONE HA SUCCESSO
      validation.revalidate().then(async (isValid) => {
        if (isValid) {
          //CHIAMO SERVIZIO DI VALIDAZIONE
          this.isLoadingAuthentication = true;

          let hash = JSON.stringify({
            username: this.username,
            password: this.password,
          });

          sessionStorage.setItem(MAINTENANCE_HASH_KEY, hash)
          
          try {
            let response = await postMaintenanceCredentials();
            this.$store.dispatch("setMaintenanceHash", { hash });
          } catch (e) {
            await this.$store.dispatch("setNotificationInfo", {
              body: "Autorizzazione negata",
            });
            this.$store.dispatch("setMaintenanceHash", {hash: null});
          }

          this.isLoadingAuthentication = false;
        }
      });
    },
  },
};
</script>


