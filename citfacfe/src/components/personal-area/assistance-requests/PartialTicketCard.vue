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
            <svg class="icon icon-secondary icon-md" aria-hidden="true">
              <use
              :xlink:href="$svgs.getSpriteSvgUrl('it-help-circle')"
             
              ></use>
            </svg>
          </div>

          <div class="accordion col">
            <div class="accordion-item">
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
                  <div class="button-wrapper">Bozza: {{ title }}</div>
                </button>
                <div >
                
                  <p class="fw-normal">{{ startDate }}</p>
                </div>
              </div>

              <div
                :id="currentTarget"
                class="accordion-collapse collapse cf-accordion p-0 m-0"
                role="region"
                :aria-labelledby="currentId"
              >
                <div class="accordion-body">
                  <p class="mb-2 fw-normal">
                    Dettagli: <span class="label fw-bold">{{ details }}</span>
                  </p>
                </div>

                <div class="row gap-2 py-3">
                  <div class="col-md-auto col-12">
                    <button
                      @click="goToNewTicket"
                      type="button"
                      class="justify-content-center btn btn-primary"
                    >
                      Completa bozza
                    </button>
                  </div>
                  <div class="col-md-auto col-12">
                    <button
                      @click="openDeleteDialog"
                      type="button"
                      class="justify-content-center btn btn-outline-primary"
                    >
                      Elimina
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

  <!--    MODALE DI ELIMINAZIONE-->
  <cf-modal
    :id="currentDeleteModalId"
    ref="deleteModal"
    no-action
    title="Conferma cancellazione bozza"
  >
    <div class="alert alert-warning" role="alert">
      <div class="text-warning fw-bold">ATTENZIONE</div>
      <div class="text-500">
        Stai per eliminare la bozza: <br /><strong>{{ title }}</strong>
      </div>
    </div>

    <div class="row justify-content-between gap-2 py-3 mx-2">
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
          @click="deletePartialTicket"
          type="button"
          class="btn btn-primary justify-content-center w-100"
          data-bs-dismiss="modal"
        >
          Conferma
        </button>
      </div>
    </div>
  </cf-modal>
</template>

<script>
import InputTextArea from "../../form/InputTestualeArea.vue";
import InputFile from "../../form/InputFile.vue";
import { format_date } from "../../../services/utils";
import { deletePartialTicket } from "../../../services/api";
import CfModal from "../../generic/CfModal.vue";
export default {
  name: "PartialTicketCard",
  components: { CfModal, InputFile, InputTextArea },
  emits:['deletedPartialTicket'],
  props: {
    partialTicket: Object,
    id: [String, Number],
    parentId: String,
  },

  data() {
    return {
      currentId: null,
      currentTarget: null,
      currentDeleteModalId: null,
      accordion: null,
      isExpanded: false,
      isLoadingDetail: false,
      partialTicketString: this.partialTicket.body,
    };
  },

  async created() {
    this.partialTicketString = JSON.parse(this.partialTicketString);
    this.currentId = "partialTicketCard" + this.id;
    this.currentDeleteModalId = "partialTicketDeleteModal" + this.id;
    this.currentTarget = "#" + this.currentId;
  },

  computed: {
    details() {
      return this.partialTicketString?.article?.body;
    },

    startDate() {
      return format_date(this.partialTicket?.dataCreazione, "dd/MM/yyyy");
    },

    title() {
      return this.partialTicketString?.title;
    },
  },
  mounted() {
    this.accordion = document.getElementById(this.currentTarget);
    this.accordionButton = document.getElementById(this.currentId);
  },
  watch: {},
  methods: {
    goToNewTicket() {
      let query = { idBozza: this.partialTicket.id };
      this.$router.push({ name: "nuova-richiesta", query: query });
    },

    async deletePartialTicket() {
      try {
        let id = this.partialTicket.id;
   
        let promise = await deletePartialTicket(id);
       
        this.$emit("deletedPartialTicket", id);
        await this.$store.dispatch("setNotificationInfo", {
          title: "Operazione completata",
          body: "Bozza cancellata con successo",
          positive: true,
        });
      } catch (e) {
        await this.$store.dispatch("setNotificationInfo", {
          title: "Attenzione",
          body: "Non è stato possibile cancellare la richiesta di assistenza",
        });
      }
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
    },
    //VISUALIZZA LA MODALE DI CANCELLAZIONE
    openDeleteDialog() {
      let myModal = new bootstrap.Modal(
        document.getElementById(this.currentDeleteModalId),
        {}
      );
      myModal.show();
    },
  },
};
</script>
