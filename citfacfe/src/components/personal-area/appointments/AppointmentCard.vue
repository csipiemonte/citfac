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
              <use :xlink:href="$svgs.getSpriteSvgUrl('it-calendar')"></use>
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
                        Appuntamento ufficio {{ categoryName }}
                      </div>
         
                    </div>
                  </div>
                </button>
                <p class="text-paragraph-small text-200 mb-0">
                  <em>
                    Prenotato il: <strong>{{ bookDate }}</strong></em
                  >
                </p>
              </div>

              <div
                :id="currentTarget"
                :aria-labelledby="currentId"
                class="accordion-collapse collapse p-0 m-0 cf-accordion"
                role="region"
              >
                <div class="accordion-body mt-2 row">
                  <p class="mb-2 fw-normal">
                    <strong>{{ dateDescription }}</strong>
                  </p>

                  <p class="mb-4 fw-normal">
                    <strong>{{ serviceName }}</strong
                    ><br />

                    {{ serviceLocation }}
                  </p>

                  <p class="mb-2 fw-normal">
                    <small style="font-weight: 600"
                      >Motivo dell'appuntamento</small
                    ><br />

                    {{ appointmentReason }}
                  </p>

                  <div class="mt-3 row gap-2" v-if="!isAppointmentExpired">
                    <div class="col-md">
                      <router-link
                        :to="editAppointmentUrl"
                        aria-label="Vai alla pagina di modifica appuntamento"
                        class="btn btn-primary px-5"
                        style="width: auto"
                        type="button"
                      >
                        Modifica appuntamento
                      </router-link>
                    </div>
                    <div class="col-md">
                      <button
                        class="col-12 col-md-auto btn btn-outline-primary px-5"
                        style="width: auto"
                        type="button"
                        @click="openDeleteDialog"
                      >
                        Annulla appuntamento
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
  </div>

  <!--    MODALE DI ELIMINAZIONE-->
  <cf-modal
    :id="currentDeleteModalId"
    ref="deleteModal"
    no-action
    title="Conferma annullamento appuntamente"
  >
    <div class="alert alert-warning" role="alert">
      <div class="text-warning fw-bold">ATTENZIONE</div>
      <div class="text-500">
        Stai per eliminare l'appuntamento: <br /><strong>{{
          categoryName
        }}</strong>
      </div>
    </div>

    <div class="row justify-content-between gap-2 py-3 mx-2">
      <div class="col-md-auto col-12">
        <button
          class="btn btn-outline-primary justify-content-center w-100"
          data-bs-dismiss="modal"
          type="button"
        >
          Annulla
        </button>
      </div>
      <div class="col-md-auto col-12">
        <button
          class="btn btn-primary justify-content-center w-100"
          data-bs-dismiss="modal"
          type="button"
          @click="deleteAppointment"
        >
          Conferma
          <span v-if="isDeleting" class="ms-2">
            <cf-inner-loading showing small />
          </span>
        </button>
      </div>
    </div>
  </cf-modal>
</template>

<script>
import { format_date } from "../../../services/utils";
import { useToggleAccordion } from "../../../composables/toggleAccordion";
import { SERVICE_APPOINTMENT_BOOKING } from "../../../router/routes";
import CfModal from "../../generic/CfModal.vue";
import { deleteAppointment } from "../../../services/api";
import CfInnerLoading from "../../generic/CfInnerLoading.vue";
import { isAfter } from "date-fns";

export default {
  name: "AppointmentCard",
  components: { CfModal, CfInnerLoading },
  emits: ["onDelete"],
  props: {
    appointment: Object,
    id: [String, Number],
  },
  data() {
    return {
      isDeleting: false,
      deleteModal: null,
    };
  },
  computed: {
    currentId() {
      return "appointmentItem" + this.id;
    },
    appointmentId() {
      return this.appointment?.id;
    },
    currentTarget() {
      return this.currentId + "Target";
    },
    currentDeleteModalId() {
      return this.currentId + "Delete";
    },
    service() {
      return this.appointment?.service;
    },
    categoryName() {
      return this.appointment?.category?.name;
    },
    serviceName() {
      return this.service?.name; // o descrizione? o categoria
    },
    serviceLocation() {
      return this.service?.location;
    },
    statusColorClass() {
      return "text-success"; // da definire in base allo stato
    },
    statusIcon() {
      return "";
    },
    statusDesc() {
      return "";
    },
    bookDate() {
      return this.appointment?.book ? format_date(this.appointment?.book) : "-";
    },
    appointmentReason() {
      let notes = this.appointment?.notes ?? "";
      let reason = notes.split(" - ")[0];
      return reason.replace(/ *\([^)]*\) */g, "");
    },
    dateDescription() {
      let start = this.appointmentDate;
      const day = format_date(start, "EEEE dd MMMM yyyy");
      const startHour = format_date(start, "HH:mm");
      let msg = "";
      let end = this.appointment?.end;
      if (end) {
        const endHour = format_date(end, "HH:mm");
        msg = `${day} dalle ore ${startHour} alle ore ${endHour}`;
      } else {
        msg = `${day} alle ore ${startHour}`;
      }

      return msg;
    },
    appointmentDate() {
      return new Date(this.appointment?.start);
    },
    isAppointmentExpired() {
      return isAfter(new Date(), this.appointmentDate);
    },
    editAppointmentUrl() {
      let route = {
        name: SERVICE_APPOINTMENT_BOOKING.name,
        query: {
          id: this.appointmentId,
        },
      };
      return route;
    },
  },
  methods: {
    useToggleAccordion,
    //VISUALIZZA LA MODALE DI CANCELLAZIONE
    openDeleteDialog() {
      this.deleteModal = new bootstrap.Modal(
        document.getElementById(this.currentDeleteModalId),
        {}
      );
      this.deleteModal.show();
    },
    async deleteAppointment() {
      this.isDeleting = true;
      try {
        let response = await deleteAppointment(this.appointmentId);
        await this.$store.dispatch("setNotificationInfo", {
          body: "Appuntamento annullato con successo.",
          positive: true,
        });
        this.$emit("onDelete", this.appointmentId);
        await this.updateAppointmentList();
      } catch (e) {
        await this.$store.dispatch("setNotificationInfo", {
          body: "Non è stato possibile annullare l'appuntamento.",
        });
      }
      this.isDeleting = false;
      this.deleteModal.hide();
    },
    async updateAppointmentList(id) {
      let appointments = this.$store.getters["getAppointmentList"];
      if (!!appointments.find((a) => a.id === id)) {
        await this.$store.dispatch("setAppointmentList");
      }
    },
  },
};
</script>


