<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div>
    <div class="cf-card cf-card-accordion mb-3">
      <div class="card shadow px-4 pt-4 pb-4">
        <div class="card-body p-0 my-2">
          <div class="row">
            <div class="col-auto position-relative">
              <svg class="icon icon-secondary icon-md" aria-hidden="true">
                <use
                  :xlink:href="$svgs.getSpriteSvgUrl('it-help-circle')"
                ></use>
              </svg>
            </div>
            <div class="accordion col">
              <div class="accordion-item no-border">
                <div class="accordion-header pe-2">
                  <button
                    :id="currentId"
                    @click="hideAccordion"
                    style="border-style: none !important"
                    class="accordion-button collapsed title-small-semi-bold"
                    type="button"
                    :aria-expanded="isExpanded"
                    :aria-controls="currentId"
                  >
                    <div class="button-wrapper">
                      <div class="row w-100">
                        <div class="col-md-10 col-12">
                          Richiesta assistenza n° {{ ticketNumber }}
                        </div>
                        <div class="col-md-2 col-12">
                          <div class="icon-wrapper">
                            <span class="rounded-icon">
                              <svg
                                class="icon icon-primary icon-xs me-1"
                                aria-hidden="true"
                              >
                                <use :href="ticketStateIconUrl"></use>
                              </svg>
                            </span>
                            <span class="">{{ ticketStateLabel }}</span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </button>
                  <div class="">
                    <p class="text-paragraph-small text-200 mb-0">
                      <em>{{ title }}</em>
                    </p>
                    <p class="fw-normal">{{ startDate }}</p>
                  </div>
                </div>

                <div
                  :id="currentTarget"
                  class="accordion-collapse collapse cf-accordion p-0 m-0"
                  role="region"
                  :aria-labelledby="currentId"
                >
                  <template v-if="!isLoadingDetail">
                    <div class="accordion-body">
                      <p class="mb-2 fw-normal">
                        Tipologia problematica:
                        <span class="label fw-bold">{{ typeLabel }}</span>
                      </p>

                      <p class="mb-2 fw-normal">
                        Dettagli:
                        <span class="label fw-bold">{{ details }}</span>
                      </p>

                      <div
                        v-if="lastOperatorNotice"
                        class="row pb-1 align-items-center bg-gray"
                      >
                        <div class="col-auto">
                          <svg class="icon icon-xs icon-primary">
                            <use
                              :xlink:href="
                                $svgs.getSpriteSvgUrl('it-info-circle')
                              "
                            ></use>
                          </svg>
                        </div>

                        <div class="fw-normal col ps-0">
                          <small>Note operatori: </small>
                          {{ lastOperatorNotice.body }}
                        </div>
                      </div>

                      <div class="cf-icon-list">
                        <div class="link-list-wrapper">
                          <ul class="link-list">
                            <li
                              v-for="(attachment, index) in attachments"
                              :key="index"
                              class="p-0"
                            >
                              <a
                                class="list-item icon-left text-primary title-small-semi-bold"
                                @click="
                                  downloadFile(
                                    attachment.id,
                                    attachment.filename
                                  )
                                "
                                aria-label="Scarica il file"
                              >
                                <span class="list-item-title-icon-wrapper">
                                  <svg
                                    class="icon icon-sm align-self-start icon-color"
                                    aria-hidden="true"
                                  >
                                    <use
                                      :xlink:href="
                                        $svgs.getSpriteSvgUrl('it-clip')
                                      "
                                    ></use>
                                  </svg>
                                  <span
                                    class="list-item-title title-small-semi-bold"
                                    >{{ attachment.filename }}</span
                                  >
                                </span>
                              </a>
                            </li>
                          </ul>
                        </div>
                      </div>
                    </div>

                    <div class="row gap-2 py-3">
                      <div class="col-md-auto col-12">
                        <button
                          @click="openDetailDialog"
                          type="button"
                          class="justify-content-center btn btn-primary"
                        >
                          Dettaglio
                        </button>
                      </div>

                      <div
                        v-if="
                          ticketState !== resolved && ticketState !== closed
                        "
                        class="col-md-auto col-12"
                      >
                        <button
                          @click="openEditDialog"
                          type="button"
                          class="justify-content-center btn btn-primary"
                        >
                          Modifica
                        </button>
                      </div>
                      <div
                        v-if="
                          ticketState !== resolved && ticketState !== closed
                        "
                        class="col-md-auto col-12"
                      >
                        <button
                          @click="openDeleteDialog"
                          type="button"
                          class="justify-content-center btn btn-outline-primary"
                        >
                          Chiudi
                        </button>
                      </div>
                    </div>
                  </template>
                  <template v-else>
                    <div
                      class="row py-2 justify-content-center full-width overlay"
                    >
                      <div class="col-auto">
                        <div class="progress-spinner progress-spinner-active">
                          <span class="visually-hidden">Caricamento...</span>
                        </div>
                      </div>
                    </div>
                  </template>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!--    MODALE DI ELIMINAZIONE-->
    <cf-modal
      title="Conferma chiusura richiesta di assistenza"
      no-action
      :id="currentDeleteModalId"
      ref="deleteModal"
    >
      <div class="alert alert-warning" role="alert">
        <div class="text-warning fw-bold">ATTENZIONE</div>
        <div class="text-500">Stai per chiudere la richiesta</div>
      </div>

      <div class="row justify-content-between gap-2 py-3">
        <div class="col-md-auto col-12">
          <button
            type="button"
            class="btn btn-outline-primary justify-content-center w-100"
            data-bs-dismiss="modal"
          >
            Annulla
          </button>
        </div>
        <div class="col-md-auto col-12">
          <button
            @click="deleteTicket"
            type="button"
            class="btn btn-primary justify-content-center w-100"
            data-bs-dismiss="modal"
          >
            Conferma
          </button>
        </div>
      </div>
    </cf-modal>

    <!--    MODALE DI DETTAGLIO-->
    <cf-modal
      no-action
      medium
      :id="detailModalId"
      ref="detailModal"
      :title="detailModalTitle"
    >
      <div class="row">
        <div class="col-12 mx-3">
          <p class="mb-0 pb-1">
            Stato: <strong class="text-primary">{{ ticketStateLabel }}</strong>
          </p>
        </div>
        <div class="col-12 m-3">
          <p class="text-paragraph-small text-200 mb-0 pb-1">
            <em>{{ typeLabel }}</em>
          </p>
        </div>
      </div>

      <div class="my-3 it-timeline-wrapper">
        <div class="row">
          <div
            :key="index"
            v-for="(article, index) in articlesList"
            class="col-12"
          >
            <div class="timeline-element">
              <div class="it-pin-wrapper it-evidence">
                <div class="pin-icon">
                  <svg class="icon">
                    <use
                      :xlink:href="$svgs.getSpriteSvgUrl('it-code-circle')"
                    ></use>
                  </svg>
                </div>
                <div class="pin-text">
                  <span>{{ formatedArticleDate(article.created_at) }}</span>
                </div>
              </div>
              <div class="card-wrapper">
                <div class="card">
                  <div class="card-body p-0">
         
                    <template v-if="article.sender === customerCod">
                      <h5 class="card-title mb-1">
                        {{ user.nome }} {{ user.cognome }}
                      </h5>
                    </template>
                    <template v-else>
                      <h5 class="card-title mb-1">Operatore</h5>
                    </template>

                    <p class="card-text">{{ article.body }}</p>

                    <div class="cf-icon-list">
                      <div class="">
                        <ul class="link-list">
                          <li
                            v-for="(attachment, index) in article.attachments"
                            :key="index"
                            class="p-0"
                          >
                            <a
                              class="list-item text-primary title-small-semi-bold"
                              @click="
                                downloadFile(
                                  attachment.id,
                                  attachment.filename,
                                  article.id
                                )
                              "
                              aria-label="Scarica il file"
                            >
                              <span class="list-item-title-icon-wrapper">
                                <svg
                                  class="icon icon-sm align-self-start icon-color"
                                  aria-hidden="true"
                                >
                                  <use
                                    :xlink:href="
                                      $svgs.getSpriteSvgUrl('it-clip')
                                    "
                                  ></use>
                                </svg>
                                <span
                                  class="list-item-title title-small-semi-bold"
                                  >{{ attachment.filename }}</span
                                >
                              </span>
                            </a>
                          </li>
                        </ul>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="row justify-content-between gap-2 py-3 mx-2">
        <div class="col-md-auto col-12">
          <button
            type="button"
            class="btn btn-outline-primary justify-content-center w-100"
            data-bs-dismiss="modal"
          >
            Indietro
          </button>
        </div>
        <template v-if="ticketState !== resolved && ticketState !== closed">
          <div class="col-md-auto col-12">
            <button
              type="button"
              class="btn btn-outline-primary justify-content-center w-100"
              @click="openEditDialog"
              data-bs-dismiss="modal"
            >
              Modifica
            </button>
          </div>
          <div class="col-md-auto col-12">
            <button
              @click="openDeleteDialog"
              type="button"
              class="btn btn-primary justify-content-center w-100"
              data-bs-dismiss="modal"
            >
              Chiudi
            </button>
          </div>
        </template>
      </div>
    </cf-modal>

    <!--    MODALE DI MODIFICA-->
    <cf-modal
      no-action
      :id="currentEditModalId"
      ref="editModal"
      :title="editModalTitle"
    >
      <p class="text-paragraph-small text-200 mb-0 pb-1">
        <em>{{ typeLabel }}</em>
      </p>

      <form action="#" :id="formId" enctype="multipart/form-data">
        <!-- Modal body -->
        <div class="">
          <p class="fw-normal">
            Dettagli: <span class="label fw-bold">{{ details }}</span>
          </p>

          <div class="row">
            <div class="col-12">
              <input-text-area
                @updateTextArea="updateDettagli"
                class="my-3"
                labelText="Dettagli*"
                :id="currentTextAreaId"
                moreInfo="Inserire massimo 600 caratteri"
              ></input-text-area>
            </div>
            <div class="col-12">
              <input-file
                id="1"
                label-text="Upload allegato"
                @update-file="updateFile"
              ></input-file>
            </div>
          </div>
        </div>

        <!-- Modal footer -->
        <div class="row justify-content-between gap-2 py-3">
          <div class="col-md-auto col-12">
            <button
              type="button"
              class="btn btn-outline-primary w-100"
              data-bs-dismiss="modal"
            >
              Annulla
            </button>
          </div>
          <div class="col-md-auto col-12">
            <button id="submit-btn" class="btn btn-primary w-100">
              Conferma
              <span class="ms-2" v-if="isLoadingDetail">
                <cf-inner-loading :showing="isLoadingDetail" small />
              </span>
            </button>
          </div>
        </div>
      </form>
    </cf-modal>
  </div>
</template>

<script>
import InputTextArea from "../../form/InputTestualeArea.vue";
import InputFile from "../../form/InputFile.vue";
import JustValidate from "just-validate";
import {
  capitalizeFirst,
  format_date,
  toBase64,
} from "../../../services/utils";

import {
  changeTicketStatus,
  editTicket,
  getTicketArticleFile,
  getTicketDetail,
} from "../../../services/api";
import {
  CLOSED,
  MORE_INFO,
  NEW,
  OPENED,
  RESOLVED,
  CUSTOMER_COD,
} from "../../../services/business-logic";

import CfModal from "../../generic/CfModal.vue";
import CfInnerLoading from "../../generic/CfInnerLoading.vue";

const SENDER_TYPE = {
  customer: "Customer",
  operator: "Agent",
};

export default {
  name: "TicketCard",
  components: { CfInnerLoading, CfModal, InputFile, InputTextArea },
  emits: ["updateTickets"],
  props: {
    ticket: Object,
    id: [String, Number],
    parentId: String,
  },

  data() {
    return {
      validation: null,
      file: null,
      currentId: null,
      currentTarget: null,
      currentDeleteModalId: null,
      currentEditModalId: null,
      detailModalId: null,
      currentTargetParentId: null,
      accordion: null,
      isExpanded: false,
      dettagli: null,
      customerCod: CUSTOMER_COD,
      resolved: RESOLVED,
      opened: OPENED,
      closed: CLOSED,
      articlesList: [],
      firstArticle: [],
      isLoadingDetail: false,
    };
  },

  async created() {
    this.currentId = "ticketCard" + this.id;
    this.currentDeleteModalId = "deleteModal" + this.id;
    this.currentEditModalId = "editModal" + this.id;
    this.detailModalId = "detailModal" + this.id;
    this.currentTarget = "#" + this.currentId;
    this.currentTargetParentId = "#" + this.parentId;
  },

  computed: {
    editModalTitle() {
      return "Modifica richiesta di assistenza n°" + this.ticketNumber;
    },
    detailModalTitle() {
      return "Richiesta di assistenza n°" + this.ticketNumber;
    },
    currentTextAreaId() {
      return this.id;
    },
    formId() {
      return "form" + this.currentId;
    },
    user() {
      return this.$store.getters["getUser"];
    },
    attachments() {
      return this.firstArticle?.attachments;
    },

    details() {
      return this.firstArticle?.body;
    },

    typeLabel() {
      return this.ticket?.type;
    },

    startDate() {
      return format_date(this.ticket?.created_at, "dd/MM/yyyy");
    },

    title() {
      return this.ticket?.title;
    },
    ticketNumber() {
      return this.ticket?.number;
    },
    ticketState() {
      return this.ticket?.state_id;
    },
    ticketStateLabel() {
      return this.ticket?.state ? capitalizeFirst(this.ticket?.state) : "";
    },

    ticketStateIconUrl() {
      if (this.ticketState === NEW) {
        return this.$svgs.getBootstrapIconsUrl("folder-symlink");
      }
      if (this.ticketState === OPENED) {
        return this.$svgs.getBootstrapIconsUrl("folder2-open");
      }
      if (this.ticketState === CLOSED) {
        return this.$svgs.getBootstrapIconsUrl("folder-x");
      }
      if (this.ticketState === RESOLVED) {
        return this.$svgs.getBootstrapIconsUrl("folder-check");
      }
      if (this.ticketState === MORE_INFO) {
        return this.$svgs.getBootstrapIconsUrl("folder-plus");
      }
    },
    lastOperatorNotice() {
      if (this.articlesList?.length > 0) {
        let lastArticle = this.articlesList[this.articlesList.length - 1];

        if (lastArticle && lastArticle.sender === SENDER_TYPE.operator) {
          return lastArticle;
        }
      }
      return null;
    },
  },
  mounted() {
    this.accordion = document.getElementById(this.currentTarget);
    this.accordionButton = document.getElementById(this.currentId);

   
  },
  watch: {},
  methods: {
    formatedArticleDate(date) {
      return format_date(date, "dd/MM/yyyy");
    },
    async editTicket() {
      try {
        this.isLoadingDetail = true;
        let type = null;
        let fileName = null;
        let base64 = null;

        //SE ESISTE ALLEGATO PREDISPONGO I SUOI CAMPI
        if (this.file) {
          //FILE NUOVO
          if (!this.file.data) {
            type = this.file?.type;
            fileName = this.file?.name;
            base64 = await toBase64(this.file);
          } else {
            //FILE DA BOZZA
            fileName = this.file?.filename;
            type = this.file?.mimetype;
            base64 = this.file?.data;
          }

          //RIMUOVO INFO RELATIVE A TIPO E NOME FILE DAL BASE64
          base64 = base64.substring(base64.indexOf(",") + 1);
        }

        let payload = {
          type_id: 13,
          body: this.dettagli ?? null,
          content_type: "text/html",

          from: this.ticket.customer ?? null,
        };

        if (this.file) {
          payload.attachments = [
            {
              filename: fileName,
              data: base64,
              "mime-type": type,
            },
          ];
        }
        let promise = await editTicket(this.ticket.id, payload);
        await this.loadTicketDetail();
        let modal = this.$refs.editModal;
        modal.onHide();

        this.$emit("updateTickets");

        this.$store.dispatch("setNotificationInfo", {
          title: "Operazione completata",
          body: "Ticket aggiornato con successo",
          positive: true,
        });
      } catch (e) {
        await this.$store.dispatch("setNotificationInfo", {
          title: "Attenzione",
          body: "Non è stato possibile modificare la richiesta di assistenza",
        });
      }
      this.isLoadingDetail = false;
    },

    async downloadFile(idFile, name, articleId) {
      try {
        if (!articleId) {
          articleId = this.firstArticle.id;
        }
        let promise = await getTicketArticleFile(
          this.ticket.id,
          articleId,
          idFile
        );
      } catch (e) {
        await this.$store.dispatch("setNotificationInfo", {
          title: "Attenzione",
          body: "Non è stato possibile scaricare i file",
        });
      }
    },
    async loadTicketDetail() {
      this.isLoadingDetail = true;
      try {
        let id = this.ticket.id;
        let promise = await getTicketDetail(id);
        let articleList = promise.data;
        articleList.forEach(
          (a) => (a.created_at = format_date(a.created_at, "yyyy-MM-dd"))
        );
        this.articlesList = articleList;
        this.firstArticle = this.articlesList[0];
      } catch (e) {
        await this.$store.dispatch("setNotificationInfo", {
          title: "Attenzione",
          body: "Non è stato possibile visualizzare i dati",
        });
      }
      this.isLoadingDetail = false;
    },
    async deleteTicket() {
      try {
        let id = this.ticket.id;
        let payload = {
          state_id: 8,
        };
        let promise = await changeTicketStatus(id, payload);
        this.$emit("updateTickets");

        this.$store.dispatch("setNotificationInfo", {
          title: "Operazione completata",
          body: "Ticket cancellato con successo",
          positive: true,
        });
      } catch (e) {
        await this.$store.dispatch("setNotificationInfo", {
          title: "Attenzione",
          body: "Non è stato possibile chiudere la richiesta di assistenza",
        });
      }
    },

    async updateFile(file) {
      this.file = file;
    },
    updateDettagli(value) {
      this.dettagli = value;
    },
    //FUNZIONE PER CREARE LA VALIDAZIONE DEL FORM
    setValidazione() {
      
      let validation = this.validation;

      validation.addField("#inputTextArea" + this.currentTextAreaId, [
        {
          rule: "required",
          errorMessage: "Campo obbligatorio",
        },
      ]);

      //SE LA VALIDAZIONE HA SUCCESSO
      validation.onSuccess((event) => {
        this.editTicket();
      });
    },
    hideAccordion() {
      if (this.accordionButton.classList.contains("collapsed")) {
        this.accordionButton.classList.add("showed");
        this.accordionButton.classList.remove("collapsed");
      } else {
        this.accordionButton.classList.add("collapsed");
        this.accordionButton.classList.remove("showed");
      }
      if (this.accordion.classList.contains("show")) {
        this.accordion.classList.add("collapse");
        this.accordion.classList.remove("show");
        this.isExpanded = false;
      } else {
        this.accordion.classList.add("show");
        this.accordion.classList.remove("collapse");
        this.isExpanded = true;
      }
      if (this.articlesList?.length <= 0) {
        this.loadTicketDetail();
      }
    },
    //VISUALIZZA LA MODALE DI CANCELLAZIONE
    openDeleteDialog() {
      let myModal = new bootstrap.Modal(
        document.getElementById(this.currentDeleteModalId),
        {}
      );
      myModal.show();
    },
    openEditDialog() {
      let myModal = new bootstrap.Modal(
        document.getElementById(this.currentEditModalId),
        {}
      );
      myModal.show();

      const validation = new JustValidate("#" + this.formId);
      this.validation = validation;

      this.setValidazione();
    },
    async openDetailDialog() {
      let myModal = new bootstrap.Modal(
        document.getElementById(this.detailModalId),
        {}
      );
      myModal.show();
    },
  },
};
</script>
