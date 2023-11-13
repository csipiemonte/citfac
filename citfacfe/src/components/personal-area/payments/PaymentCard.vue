<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="cf-card cf-card-accordion mb-3">
    <div class="card shadow px-4 pt-4 pb-4">
      <div class="card-body p-0 my-2">
        <div class="row">
          <div class="col-auto position-relative">
            <svg aria-hidden="true" class="icon icon-secondary icon-md">
              <use :xlink:href="$svgs.getSpriteSvgUrl('it-card')"></use>
            </svg>
          </div>
          <div class="accordion col">
            <div class="accordion-item no-border">
              <div class="accordion-header pe-2">
                <button
                  :id="currentId"
                  :aria-controls="currentId"
                  :aria-expanded="false"
                  class="accordion-button collapsed title-small-semi-bold"
                  style="border-style: none !important"
                  type="button"
                  @click="useToggleAccordion(currentTarget, currentId)"
                >
                  <div class="button-wrapper">
                    <div class="row w-100">
                      <div class="col-md-9 col-12">
                        {{ paymentDescription }}
                      </div>
                      <div class="col-md-3 col-12">
                        <div
                          :class="statusColorClass"
                          class="icon-wrapper w-100"
                        >
                          <span class="rounded-icon">
                            <svg
                                aria-hidden="true"
                                class="icon icon-xs me-1"
                                :class="statusIconClass"
                              >
                                <use :href="statusIcon"></use>
                              </svg>
                           
                          </span>
                          <span class="">{{ statusDesc }}</span>
                        </div>
                      </div>
                    </div>
                  </div>
                </button>

                <p class="mb-0 fw-normal">
                  Codice avviso:
                  <strong class="opacity-75">{{ noticeCode }}</strong>
                </p>
              </div>

              <div
                :id="currentTarget"
                :aria-labelledby="currentId"
                class="accordion-collapse collapse p-0 m-0 cf-accordion"
                role="region"
              >
                <div class="accordion-body mt-2 row">
                  <p class="mb-2 fw-normal" v-if="amount">
                    Importo:
                    <span class="label fw-bold">{{ amount }} €</span>
                  </p>
                  <p class="mb-2 fw-normal" v-if="iuv">
                    IUV:
                    <span class="label fw-bold">{{ iuv ?? "-" }}</span>
                  </p>

                  <template v-if="isPayed && transactionNr">
                    <p class="mb-2 fw-normal">
                      Numero transazione:
                      <span class="label fw-bold">{{ transactionNr }}</span>
                    </p>
                    <p class="mb-2 fw-normal" v-if="paymentCode">
                      Codice versamento:
                      <span class="label fw-bold">{{ paymentCode }}</span>
                    </p>
                    <p class="mb-2 fw-normal" v-if="paymentDate">
                      Data pagamento:
                      <span class="label fw-bold">{{ paymentDate }}</span>
                    </p>
                  </template>
                  <template v-else-if="expirationDate">
                    <p class="mb-2 fw-normal">
                      Data scadenza:
                      <span class="label fw-bold">{{ expirationDate }}</span>
                    </p>
                  </template>
                  <p class="mb-2 fw-normal" v-if="tipology">
                    Tipologia:
                    <span class="label fw-bold">{{ tipology }}</span>
                  </p>

                  <p v-if="additionalInfo" class="mb-2 fw-normal">
                    Note:
                    <span class="label fw-bold">{{ additionalInfo }}</span>
                  </p>

                  <div class="mt-3">
                    <router-link
                      v-if="isNotPayed"
                      aria-label="Vai a pagina di pagamento"
                      class="btn btn-primary px-5"
                      style="width: auto"
                      type="button"
                      :to="paymentUrl"
                      @click.prevent="goToPayment"
                    >
                      PAGA ORA
                      <span v-if="isPaying" class="ms-2">
                        <cf-inner-loading showing small />
                      </span>
                    </router-link>
                    <button
                      v-else-if="hasReceipt"
                      aria-label="Scarica ricevuta pagamento"
                      class="btn btn-outline-primary px-5"
                      style="width: auto"
                      type="button"
                      @click="onDownloadReceipt"
                    >
                      <svg
                        aria-hidden="true"
                        class="icon icon-light icon-sm me-1"
                      >
                        <use
                          :xlink:href="$svgs.getSpriteSvgUrl('it-clip')"
                        ></use>
                      </svg>

                      Ricevuta pagamento
                      <span v-if="isDownloading" class="ms-2">
                        <cf-inner-loading showing small />
                      </span>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { useToggleAccordion } from "../../../composables/toggleAccordion";
import { format_date } from "../../../services/utils";
import CfInnerLoading from "../../generic/CfInnerLoading.vue";
import { getPagoPaUrl, getPaymentReceipt } from "../../../services/api";
import { PAYMENT_IUV } from "../../../router/routes";
import { PAYMENT_STATUS_MAP } from "../../../services/config";
import {
  base64ToBlob,
  convertToBlobUrl,
} from "../../../services/business-logic";

export default {
  name: "PaymentCard",
  components: { CfInnerLoading },
  props: {
    payment: Object,
    id: [String, Number],
    userCf: String,
  },
  data() {
    return {
      PAYMENT_IUV,
      isDownloading: false,
      isPaying: false,
    };
  },
  computed: {
    currentId() {
      return "paymentItem" + this.id;
    },
    currentTarget() {
      return this.currentId + "Target";
    },
    paymentDescription() {
      return this.payment?.descrizionePagamento ?? "-";
    },
    paymentStatus() {
      return this.payment?.codiceStatoPagamento;
    },
    isPayed() {
      return this.paymentStatus === PAYMENT_STATUS_MAP.PAYED;
    },
    isNotPayed() {
      return this.paymentStatus === PAYMENT_STATUS_MAP.NOT_PAYED;
    },
    statusIcon() {
      let icon = this.isPayed ? "check-lg" : "currency-euro";
      return  this.$svgs.getBootstrapIconsUrl(icon);
    },
    statusDesc() {
      return this.payment?.descrizioneStatoPagamento;
    },
    statusColorClass() {
      return this.isPayed ? "text-success" : "text-danger";
    },
    statusIconClass(){
      return this.isPayed ? "icon-success" : "icon-danger";
    },
    noticeCode() {
      return this.payment?.codiceAvviso;
    },
    iuv() {
      return this.payment?.iuv;
    },
    expirationDate() {
      return this.payment?.dataScadenza;
    },
    amount() {
      let amount = this.payment?.importo;
      return parseFloat(amount).toFixed(2);
    },
    additionalInfo() {
      return this.payment?.infoAggiuntive;
    },
    transactionNr() {
      return this.payment?.numeroTransazione;
    },
    paymentCode() {
      return this.payment?.codiceVersamento;
    },
    paymentDate() {
      let date = this.payment?.dataOraOperazione;
      return format_date(date);
    },
    hasReceipt() {
      return !!this.payment?.presenzaQuietanza;
    },
    tipology() {
      return this.payment?.descrizionePortale;
    },
    paymentUrl() {
      return {
        name: PAYMENT_IUV.name,
        params: {
          iuv: this.iuv,
        },
        state: {
          noticeCode: this.noticeCode,
          paymentDescription: this.paymentDescription,
        },
      };
    },
  },
  methods: {
    useToggleAccordion,
    async onDownloadReceipt() {
      this.isDownloading = true;
      try {
        let { data } = await getPaymentReceipt(this.userCf, this.iuv);
        const receipt = data?.receipt;
        // E' sempre un PDF

        if (receipt) {
          let baseBlob = base64ToBlob(receipt);
          let url = data ? convertToBlobUrl(baseBlob) : null;
          window.open(url);
        } else {
          await this.$store.dispatch("setNotificationInfo", {
            body: "Non è stato possibile scaricare la ricevuta del pagamento",
          });
        }
      } catch (e) {
        await this.$store.dispatch("setNotificationInfo", {
          body: "Non è stato possibile scaricare la ricevuta del pagamento",
        });
      }
      this.isDownloading = false;
    },
    goToPayment() {
      let route = {
        name: PAYMENT_IUV.name,
        params: {
          iuv: this.iuv,
        },
      };
      this.$router.push(route);
    },
  },
};
</script>

