<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div>
    <div class="cf-card cf-card-accordion mb-3">
      <div class="card shadow px-4 pt-4 pb-4">
        <div class="card-header border-0 p-0 m-0">
          <div class="cf-card__date" style="margin-left: 44px">
            Data invio: {{ startDate }}
          </div>
        </div>
        <div class="card-body p-0 my-2">
          <div class="row">
            <div class="col-auto position-relative">
              <svg aria-hidden="true" class="icon icon-secondary icon-md">
                <use :xlink:href="$svgs.getSpriteSvgUrl('it-files')"></use>
              </svg>
            </div>
            <div class="accordion col">
              <div class="accordion-item">
                <div class="accordion-header">
                  <button
                    :id="currentId"
                    :aria-controls="currentId"
                    :aria-expanded="isExpanded"
                    class="accordion-button collapsed title-small-semi-bold"
                    style="border-style: none !important"
                    type="button"
                    @click="hideAccordion"
                  >
                    <div class="button-wrapper">
                      <div class="row w-100">
                        <div class="col-md-9 col-12">
                          {{ instanceTitle }}
                        </div>
                        <div class="col-md-3 col-12">
                          <div class="icon-wrapper w-100">
                            <span class="rounded-icon">
                              <svg
                                aria-hidden="true"
                                class="icon icon-primary icon-xs me-1"
                              >
                                <use :href="instanceStateIconUrl"></use>
                              </svg>
                            </span>
                            <span class="">{{ instanceStateDesc }}</span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </button>
                </div>

                <div
                  :id="currentTarget"
                  :aria-labelledby="currentId"
                  class="accordion-collapse collapse p-0 m-0 cf-accordion"
                  role="region"
                >
                  <div class="accordion-body">
                    <p class="mb-2 fw-normal">
                      Pratica:
                      <span class="label fw-bold">{{ instanceCode }}</span>
                    </p>

                    <div class="cf-icon-list my-3">
                      <div class="link-list-wrapper">
                        <ul class="link-list">
                          <li class="p-0">
                            <a
                              class="btn px-0"
                              role="button"
                              aria-label="Scarica il file"
                              @click="downloadFile(instanceCode)"
                            >
                              <span class="list-item-title-icon-wrapper">
                                <svg
                                  aria-hidden="true"
                                  class="icon icon-sm align-self-start icon-color"
                                >
                                  <use
                                    :xlink:href="
                                      $svgs.getSpriteSvgUrl('it-clip')
                                    "
                                  ></use>
                                </svg>
                                <span
                                  class="list-item-title title-small-semi-bold"
                                  >Ricevuta richiesta</span
                                >
                              </span>
                            </a>
                          </li>
                        </ul>
                      </div>
                    </div>

                    
                    <div
                      v-if="instance.stato.codice === PARTIAL.CODICE"
                      class="row gap-2 py-3"
                    >
                      <div class="col-md-auto col-12">
                        <button
                          :data-bs-target="`#${cancelModalId}`"
                          class="justify-content-center btn btn-outline-primary"
                          data-bs-toggle="modal"
                          type="button"
                          @click="onDelete()"
                        >
                          Elimina
                        </button>
                      </div>
                      <div class="col-md-auto col-12">
                        <a
                          class="justify-content-center btn btn-primary"
                          type="button"
                          :href="urlMoonEdit"
                        >
                          Perfeziona la richiesta
                        </a>
                      </div>
                    </div>
                    <div v-else class="row gap-2 py-3">
                      <div class="col-md-auto col-12">
                        <a
                          class="justify-content-center btn btn-primary"
                          type="button"
                          :href="urlMoonDetail"
                        >
                          Dettaglio richiesta
                        </a>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <cf-modal
      :id="cancelModalId"
      ref="ModalInstanceDelete"
      v-model="showCancelModal"
      title="Elimina bozza"
    >
      <p class="fw-normal">
        Stai per eliminare la pratica <strong>"{{ instanceTitle }}"</strong>.
      </p>
      <p class="fw-normal mb-0">Vuoi proseguire?</p>

      <template #actions>
        <button
          class="btn btn-primary btn-modal-action"
          data-bs-dismiss="modal"
          type="button"
          @click="confirmDelete"
        >
          Conferma
          <span v-if="isDeleting" class="ms-2">
            <cf-inner-loading :showing="isDeleting" small />
          </span>
        </button>
        <button
          class="btn btn-outline-secondary btn-modal-action"
          data-bs-dismiss="modal"
          type="button"
        >
          Annulla
        </button>
      </template>
    </cf-modal>
  </div>
</template>

<script>
import InputTextArea from "../../form/InputTestualeArea.vue";
import InputFile from "../../form/InputFile.vue";

import { useDate } from "../../../composables/date";
import { format_date } from "../../../services/utils";

import { deleteMoonInstance, downloadInstance } from "../../../services/api";
import {
  SENT,
  PUBLISHED,
  PARTIAL,
  COMPLETED,
  convertToBlobUrl,
  APPROVED,
  UNDER_PROCESSING,
} from "../../../services/business-logic";
import { useMoonUrl } from "../../../composables/moonUrl";
import CfInnerLoading from "../../generic/CfInnerLoading.vue";
import CfModal from "../../generic/CfModal.vue";

export default {
  name: "InstanceCard",
  components: { CfModal, CfInnerLoading, InputFile, InputTextArea },
  emits: ["on-delete"],
  props: {
    instance: Object,
    id: [String, Number],
    parentId: String,
  },

  data() {
    return {
      SENT,
      PUBLISHED,
      PARTIAL,
      COMPLETED,
      useDate: useDate,
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
      isLoadingDetail: false,
      isDeleting: false,
      instanceDownloaded: null,
      showCancelModal: false,
    };
  },

  async created() {
    this.currentId = "instanceCard" + this.id;
    this.currentDeleteModalId = "deleteModal" + this.id;
    this.currentEditModalId = "editModal" + this.id;
    this.detailModalId = "detailModal" + this.id;
    this.currentTarget = "#" + this.currentId;
    this.currentTargetParentId = "#" + this.parentId;
  },

  computed: {
    idMoonToken() {
      return this.$store.getters["getMoonIdToken"];
    },
    cancelModalId() {
      return `ModalInstanceDelete${this.id}`;
    },
    instanceCode() {
      return this.instance.codiceIstanza;
    },
    instanceTitle() {
      return this.instance.modulo.descrizioneModulo;
    },
    instanceStateIconUrl() {
      if (this.instanceState === SENT.CODICE) {
        return this.$svgs.getBootstrapIconsUrl("folder-symlink");
      }
      if (
        this.instanceState === PUBLISHED.CODICE ||
        this.instanceState === UNDER_PROCESSING.CODICE
      ) {
        return this.$svgs.getBootstrapIconsUrl("folder2-open");
      }
      if (this.instanceState === APPROVED.CODICE) {
        return this.$svgs.getBootstrapIconsUrl("folder-check");
      }
      if (this.instanceState === COMPLETED.CODICE) {
        return this.$svgs.getBootstrapIconsUrl("folder-x");
      }

      if (this.instanceState === PARTIAL.CODICE) {
        return this.$svgs.getBootstrapIconsUrl("folder-plus");
      } else {
        return this.$svgs.getBootstrapIconsUrl("folder");
      }
    },
    instanceStateDesc() {
      return this.instance?.stato.descrizione;
    
    },

    startDate() {
      return format_date(this.instance?.created, "dd/MM/yyyy");
    },

    instanceState() {
      return this.instance?.stato.codice;
    },
    urlMoonEdit() {
      return useMoonUrl(
        undefined,
        "istanza",
        this.instance.codiceIstanza,
        "%2Farea-personale"
      );
    },
    urlMoonDetail() {
      return useMoonUrl(
        this.instance.codiceModulo,
        "istanza",
        this.instance.codiceIstanza,
        "%2Farea-personale"
      );
    },
  },
  mounted() {
    this.accordion = document.getElementById(this.currentTarget);
    this.accordionButton = document.getElementById(this.currentId);
  },
  watch: {},
  methods: {
    goToMoonEdit() {
      let url = useMoonUrl(
        undefined,
        "istanza",
        this.instance.codiceIstanza,
        "%2Farea-personale"
      );
      document.assign(url);
    },
    goToMoonDetail() {
      document.assign(
        useMoonUrl(
          this.instance.codiceModulo,
          "istanza",
          this.instance.codiceIstanza,
          "%2Farea-personale"
        )
      );
    },
    onDelete() {
      this.showCancelModal = true;
    },
    async confirmDelete() {
      this.isDeleting = true;
      try {
        let response = await deleteMoonInstance(
          this.instanceCode,
          this.idMoonToken
        );
        this.$emit("on-delete", this.instanceCode);
        await this.updateInstances();
      } catch (e) {
        await this.$store.dispatch("setNotificationInfo", {
          body: "Non è stato possibile eliminare la pratica",
        });
      }
      this.isDeleting = false;
    },
    async updateInstances(code) {
      let instances = this.$store.getters["getInstanceList"];
      let instancesList = instances?.instances ?? [];
      let instance = instancesList.find((i) => i.codiceIstanza === code);
      if (!!instance) {
        await this.$store.dispatch("setInstanceList");
      }
    },
    async downloadFile() {
      try {
        let { data } = await downloadInstance(
          this.instanceCode,
          this.idMoonToken
        );
        let url = data ? convertToBlobUrl(data) : null;
        window.open(url);
      } catch (e) {
        await this.$store.dispatch("setNotificationInfo", {
          title: "Attenzione",
          body: "Non è stato possibile scaricare il file",
        });
      }
    },

    async updateFile(file) {
      this.file = file;
    },

    //FUNZIONE PER CREARE LA VALIDAZIONE DEL FORM
    setValidazione() {
      
      let validation = this.validation;

      validation.addField("#inputTextArea1", [
        {
          rule: "required",
          errorMessage: "Campo obbligatorio",
        },
      ]);

      //SE LA VALIDAZIONE HA SUCCESSO
      validation.onSuccess((event) => {
       
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

