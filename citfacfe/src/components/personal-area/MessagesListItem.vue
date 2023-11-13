<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="cf-card cf-card-accordion cf-card-latest-messages mb-3">
    <div class="card shadow px-4 pt-4 pb-4">
      <div class="card-header border-0 p-0 m-0">
        <div class="cf-card__date" style="margin-left: 44px">
          {{ messageDate }}
        </div>
      </div>

      <div class="card-body p-0 my-2">
        <div class="row">
          <div class="col-auto position-relative">
            <svg class="icon icon-md icon-secondary">
              <use :href="icon"></use>
            </svg>
            <div
              v-if="!isMessageRead"
              aria-label="non letto"
              class="badge bg-danger badge-not-read"
            ></div>
          </div>
          <div :id="'accordion' + id" class="accordion col">
            <div class="accordion-item p-3">
              <div class="accordion-header">
                <button
                  :id="currentId"
                  :aria-controls="currentId"
                  :aria-expanded="isExpanded"
                  class="accordion-button collapsed title-small-semi-bold text-primary"
                  type="button"
                  @click="hideAccordion"
                >
                  <div class="button-wrapper">
                    {{ messageTitle }}
                  </div>
                </button>
              </div>

              <div
                :id="currentTarget"
                :aria-labelledby="currentId"
                class="accordion-collapse collapse p-0 m-0 cf-accordion position-relative"
                role="region"
              >
                <div class="accordion-body mt-2 column">
                  <p
                    class="text-paragraph cf-accordion__body"
                    v-html="messageBody"
                  ></p>

                 

                  <div class="row gap-2 py-3">
                    <div class="col-md-auto col-12">
                      <button
                        :data-bs-target="`#${cancelModalId}`"
                        aria-label="Elimina messaggio"
                        class="btn btn-outline-primary px-5"
                        data-bs-toggle="modal"
                        style="width: auto"
                        type="button"
                      >
                        ELIMINA
                      </button>
                    </div>
                    <div class="col-md-auto col-12" v-if="callToAction">
                      <a
                        :href="callToAction"
                        alt="vai alla call to action"
                        class="btn btn-primary px-5 fw-bold"
                      
                        style="width: auto"
                        type="button"
                      >
                        VAI
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
    ref="modalNotificationCancel"
    title="Elimina notifica"
  >
    <p class="fw-normal">
      Stai per eliminare il messaggio <strong>"{{ messageTitle }}"</strong>.
    </p>
    <p class="fw-normal mb-0">Vuoi proseguire?</p>

    <template #actions>
      <button
        class="btn btn-primary btn-modal-action"
        data-bs-dismiss="modal"
        type="button"
        @click="onDelete"
      >
        Conferma
        <span v-if="isDeleting" class="ms-2">
          <cf-inner-loading :showing="isDeleting" small />
        </span>
      </button>
      <button
        class="btn btn-outline-primary btn-modal-action"
        data-bs-dismiss="modal"
        type="button"
      >
        Annulla
      </button>
    </template>
  </cf-modal>
</template>

<script>
import { useDate } from "../../composables/date";
import CfInnerLoading from "../generic/CfInnerLoading.vue";
import { deleteNotifications, setNotificationStatus } from "../../services/api";
import CfModal from "../generic/CfModal.vue";
import CfAlert from "../generic/CfAlert.vue";

export default {
  name: "MessagesListItem",
  components: { CfAlert, CfModal, CfInnerLoading },
  props: {
    message: { type: Object, default: null },
    id: [String, Number],
  },
  emits: ["on-delete", "on-read"],
  data() {
    return {
      currentId: null,
      currentTarget: null,
      isDeleting: false,
      accordion: null,
      accordionButton: null,
      isExpanded: false,
      isLoadingDetail: false,
      readStatusChanged: false,
    };
  },
  mounted() {
    this.accordion = document.getElementById(this.currentTarget);
    this.accordionButton = document.getElementById(this.currentId);
  },
  computed: {
    cancelModalId() {
      return `ModalDelete${this.id}`;
    },
    messageDate() {
      return this.message ? useDate(this.message.timestamp) : null;
    },
    messageTitle() {
      return this.message?.mex?.title ?? "-";
    },
    messageBody() {
      return this.message?.mex?.body;

    },
    isMessageRead() {
      return !!this.message?.read_at || this.readStatusChanged;
    },
    callToAction() {
      return this.message?.mex?.call_to_action;
    },
    icon() {
      let icon = "it-mail";
      if (this.isMessageRead) {
        icon += "-open";
      }
      return this.$svgs.getSpriteSvgUrl(icon)
    
    },
  },
  async created() {
    this.currentId = "msgItem" + this.id;
    this.currentTarget = "#" + this.currentId;
  },
  methods: {
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
        if (!this.isMessageRead) {
          this.setAsRead();
        }
      }
    },

    async onDelete() {
      this.isDeleting = true;
      const id = this.message.id;
      const user = this.$store.getters["getUser"];
      const taxCode = user?.codFisc;
      try {
        let response = await deleteNotifications(taxCode, id);
        this.$emit("on-delete", id);
      } catch (e) {
        await this.$store.dispatch("setNotificationInfo", {
          body: "Non è stato possibile eliminare il messaggio",
        });
      }

      this.isDeleting = false;
    },
    async setAsRead() {
      const id = this.message.id;
      const user = this.$store.getters["getUser"];
      const taxCode = user?.codFisc;

      let payload = [
        {
          id: id,
          read_at: new Date(),
        },
      ];
      try {
        let response = await setNotificationStatus(taxCode, payload);
        this.readStatusChanged = true;
        this.$emit("on-read", id);
      } catch (e) {}
    },
  },
};
</script>


