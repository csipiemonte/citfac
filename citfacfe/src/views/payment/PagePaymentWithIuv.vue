<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <main id="main-container" class="container">
    <!--    COMPONENTE TITOLO-->

    <cf-title-section :breadcrumbs="BREADCRUMBS_LINK" subtitle>
      <template #title> Pagamento con IUV </template>

      <template #subtitle>
        <div>
          Hai bisogno di aiuto? Consulta le
          <router-link
            class="text-decoration-none fw-bold"
            data-element="faq"
            to="/faq"
            >FAQ</router-link
          >
          o
          <router-link
            class="text-decoration-none fw-bold"
            to="/nuova-richiesta"
            >Richiedi assistenza</router-link
          >
        </div>
        <br />
      </template>
    </cf-title-section>
    <cf-page-section class="my-4">
      <!--   CARICAMENTO   -->
      <template v-if="isLoading">
        <cf-inner-loading showing />
      </template>

      <template v-else-if="!payment">
        <cf-alert title="Attenzione"
          ><p>Posizione debitoria non trovata</p></cf-alert
        >
      </template>
      <!--   STEPPER    -->
      <template v-else>
        <stepper ref="paymentStepper" v-model="activeStep" :steps="STEPS_LIST">
          <!--      INFORMATIVA SULLA PRIVACY     -->
          <!-- -------------------------------------------------------------------------->
          <template v-slot:[STEPS.PRIVACY]>
            <cf-page-section class="my-4">
              <cf-inner-loading :showing="isLoadingPolicy" class="my-4" />

              <div v-show="!isLoadingPolicy">
                <div class="privacy-terms pb-2" v-html="privacyTermsHtml"></div>

                <input-checkbox
                  id="__privacy-check"
                  v-model="isPrivacyAcepted"
                  labelText="Ho letto e compreso l'informativa sulla privacy"
                  required
                >
                  <template #error>{{ privacyErrorMsg }}</template>
                </input-checkbox>

                <div class="d-grid gap-2 d-md-block">
                  <button
                    class="btn btn-primary fw-bold me-4 btn-next col-12 col-md-4"
                    @click="stepPrivacyNext"
                  >
                    Avanti
                  </button>
                </div>
              </div>
            </cf-page-section>
          </template>

          <!--    STEP RIEPILOGO  -->
          <!-- -------------------------------------------------------------------  -->
          <template v-slot:[STEPS.SUMMARY]>
            <div class="mx-1">
              <h2>Riepilogo</h2>
              <!--     PAGAMENTO     -->
              <div class="bg-gray pb-2 my-2">
                <div class="row m-4">
                  <div class="col-12 px-0 py-3">
                    <h3>Pagamento</h3>
                  </div>

                  <div class="col-12 bg-white p-3 mb-4">
                    <div class="it-list-wrapper">
                      <ul class="it-list">
                        <li>
                          <div class="list-item">
                            <div class="py-2">
                              <small>Codice avvisoF</small>
                              <div class="fw-bold">
                                {{ noticeCode || "-" }}
                              </div>
                            </div>
                          </div>
                        </li>
                        <li>
                          <div class="list-item">
                            <div class="py-2">
                              <small>Pagamento</small>
                              <div class="fw-bold">
                                {{ paymentDescription || "-" }}
                              </div>
                            </div>
                          </div>
                        </li>
                      </ul>
                    </div>
                  </div>
                </div>
              </div>
              <!--     DATI UTENTE     -->
              <div class="bg-gray pb-2 my-2" v-if="user">
                <div class="row m-4">
                  <div class="col-12 px-0 py-3">
                    <h3>Effettuato da</h3>
                  </div>

                  <div class="col-12 bg-white p-3 mb-4">
                    <div class="it-list-wrapper">
                      <ul class="it-list">
                        <li>
                          <div class="list-item">
                            <div class="py-2">
                              <small>Nome</small>
                              <div class="fw-bold">{{ user.nome || "-" }}</div>
                            </div>
                          </div>
                        </li>
                        <li>
                          <div class="list-item">
                            <div class="py-2">
                              <small>Cognome</small>
                              <div class="fw-bold">
                                {{ user.cognome || "-" }}
                              </div>
                            </div>
                          </div>
                        </li>

                        <li v-if="user.contatti">
                          <div class="list-item">
                            <div class="py-2">
                              <small>Email</small>
                              <div class="fw-bold">
                                {{ user.contatti.email || "-" }}
                              </div>
                            </div>
                          </div>
                        </li>
                        <li>
                          <div class="list-item">
                            <div class="py-2">
                              <small>Codice fiscale / P.Iva </small>
                              <div class="fw-bold">
                                {{ user.codFisc || "-" }}
                              </div>
                            </div>
                          </div>
                        </li>
                      </ul>
                    </div>
                  </div>
                </div>
              </div>

              <!--     IMPORTO     -->
              <div class="bg-gray pb-2 my-2">
                <div class="row m-4">
                  <div class="col-12 px-0 py-3">
                    <h3>Importo</h3>
                  </div>

                  <div class="col-12 bg-white p-3 mb-4">
                    <div class="it-list-wrapper">
                      <ul class="it-list">
                        <li>
                          <div class="list-item">
                            <div class="py-2">
                              <small>Importo dovuto</small>
                              <div class="fw-bold">{{ amount || "-" }} €</div>
                            </div>
                          </div>
                        </li>
                      </ul>
                    </div>
                  </div>
                </div>
              </div>

              <stepper-navigation
                @next="onPayment"
                @prev="stepPrev"
                :loading="isPaying"
              />
            </div>
          </template>
        </stepper>
      </template>
    </cf-page-section>
  </main>
</template>

<script>
import Breadcrumbs from "../../components/generic/Breadcrumbs.vue";
import Stepper from "../../components/stepper/Stepper.vue";
import TitleSection from "../../components/generic/TitleSection.vue";
import CfPageSection from "../../components/generic/CfPageSection.vue";
import CfTitleSection from "../../components/generic/CfTitleSection.vue";
import CfInnerLoading from "../../components/generic/CfInnerLoading.vue";
import ContactsSection from "../../components/generic/ContactsSection.vue";
import { PERSONAL_AREA } from "../../router/routes";
import InputCheckbox from "../../components/form/InputCheckbox.vue";
import StepperNavigation from "../../components/stepper/StepperNavigation.vue";
import {
  getDebtPositions,
  getPagoPaUrl,
  getpaymentPolicy,
} from "../../services/api";
import { ASSETS_MAP } from "../../services/config";
import CfAlert from "../../components/generic/CfAlert.vue";
const BREADCRUMBS_LINK = [
  { url: PERSONAL_AREA.path, name: "Area personale" },
  { url: "", name: "Pagamento con IUV" },
];

const STEPS = {
  PRIVACY: 1,
  SUMMARY: 2,
  PAYMENT: 3,
};

const STEPS_LIST = [
  { id: STEPS.PRIVACY, name: "INFORMATIVA SULLA PRIVACY" },
  { id: STEPS.SUMMARY, name: "RIEPILOGO" },
  { id: STEPS.PAYMENT, name: "DATI PAGAMENTO" },
];

export default {
  name: "PagePaymentWithIuv",
  components: {
    Stepper,
    ContactsSection,
    CfInnerLoading,
    CfTitleSection,
    CfPageSection,
    TitleSection,
    Breadcrumbs,
    InputCheckbox,
    StepperNavigation,
    CfAlert,
  },
  data() {
    return {
      BREADCRUMBS_LINK,
      STEPS,
      STEPS_LIST,
      iuv: null,
      isPaying: false,
      isLoading: false,
      activeStep: 1,
      isLoadingPolicy: false,
      privacyTermsHtml: "",
      isPrivacyAcepted: false,
      privacyErrorMsg: "",
      onClickStepPrivacyNext: false,
      payment: null,
    };
  },
  watch: {
    isPrivacyAcepted() {
      let isValid = this.setPrivacyValidation();
    },
  },
  created() {
    if (!this.isActivePage) {
      
      window.open(this.getComuneUrl, "_self");
      return;
    }
    this.iuv = this.$route.params?.iuv;
  },
  async mounted() {
    scroll(0, 0);

    this.isLoading = true;

    try {
      let params = {
        iuv: this.iuv,
      };
      let {data} = await getDebtPositions(this.user?.codFisc, { params });
      let debtPositions = data?.debtPositions ?? [];

      this.payment = debtPositions[0];
    } catch (e) {
      await this.$store.dispatch("setNotificationInfo", {
        body: "Non è stato possibile recuperare la posizione debitoria, si prega di riprovare",
      });
    }

    this.isLoading = false;
    this.getPaymentPolicy();
  },

  computed: {
    getComuneUrl() {
      let comuneConfig = this.$store.getters["getComuniConfig"];
      return comuneConfig?.homePage ?? "";
    },
    user() {
      return this.$store.getters["getUser"];
    },

    noticeCode() {
      return this.payment?.codiceAvviso;
    },
    paymentDescription() {
      return this.payment?.descrizionePagamento ?? "-";
    },
    amount() {
      let amount = this.payment?.importo;
      return parseFloat(amount).toFixed(2);
    },
    isActivePage() {
      let assetCodeList = this.$store.getters["getAssetCodeList"];
      return assetCodeList?.includes(ASSETS_MAP.PAYMENTS);
    },
  },
  methods: {
    async getPaymentPolicy() {
      this.isLoadingPolicy = true;
      try {
        let { data: terms } = await getpaymentPolicy();
        this.privacyTermsHtml = terms;
      } catch (e) {
        await this.$store.dispatch("setNotificationInfo", {
          body: "Non è stato possibile recuperare l'informativa, si prega di riprovare",
        });
      } finally {
        this.isLoadingPolicy = false;
      }
    },
    setPrivacyValidation() {
      const checkBox = document.getElementById("inputCheckbox__privacy-check");
      const validityState = checkBox.validity;

      if (this.onClickStepPrivacyNext) {
        checkBox.reportValidity();

        if (validityState.valueMissing) {
          this.privacyErrorMsg = "Dichiara di aver letto l'informativa";
          checkBox.setCustomValidity(this.privacyErrorMsg);
          return false;
        } else {
          this.privacyErrorMsg = "";
          checkBox.setCustomValidity(this.privacyErrorMsg);
          return true;
        }
      }
    },
    stepNext() {
      this.activeStep += 1;
    },
    stepPrev() {
      this.activeStep -= 1;
    },
    stepPrivacyNext() {
      this.onClickStepPrivacyNext = true;

      let isValid = this.setPrivacyValidation();
      if (isValid) {
        this.stepNext();
      }
    },
    async onPayment() {
      this.isPaying = true;
      try {
        let response = await getPagoPaUrl(this.iuv);
        const url = response?.data?.paymentUrl;
    
        if (url) {
          window.open(url, "_self");
        } else {
          await this.$store.dispatch("setNotificationInfo", {
            body: "Siamo spiacenti, al momento non è possibile completare il pagamento. Ti consigliamo di riprovare più tardi, se il problema persiste contattare l'assistenza",
          });
        }
    
      } catch (e) {
        await this.$store.dispatch("setNotificationInfo", {
          body: "Siamo spiacenti, al momento non è possibile completare il pagamento. Ti consigliamo di riprovare più tardi, se il problema persiste contattare l'assistenza",
        });
      }

      this.isPaying = false;
    },
  },
};
</script>
