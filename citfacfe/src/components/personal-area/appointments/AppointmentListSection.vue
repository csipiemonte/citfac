<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <section id="appuntamenti" class="it-page-section mb-5">
    <filter-search-buttons
      id="payments"
      :hidden-filters="appointmentList.length <= 0"
      hide-form
      hide-search
      title="Appuntamenti"
    >
      <template #dropdownList>
        <ul class="link-list">
          <li>
            <a
              class="dropdown-item list-item"
              @click="onSortAppoinments('desc')"
              ><span>Data più recente</span></a
            >
          </li>
          <li>
            <a class="dropdown-item list-item" @click="onSortAppoinments('asc')"
              ><span>Data meno recente</span></a
            >
          </li>
        </ul>
      </template>
    </filter-search-buttons>

    <!--    LOADER -->
    <template v-if="loadingAppointments">
      <cf-inner-loading :showing="true" class="p-2" />
    </template>
    <template v-else-if="appointmentList.length > 0">
      <transition-group name="list">
        <div
          v-for="(appointment, index) in appointmentList"
          v-show="!appointment.deleted"
          :key="index"
          class="my-2"
        >
          <appointment-card
            :id="index"
            :appointment="appointment"
            @on-delete="onDeleteAppointment"
          />
        </div>
      </transition-group>

      <show-more-less-button
        :show-more="areMoreAppointments"
        show-more-label="Vedi altri appuntamenti"
        show-less-label="Vedi meno appuntamenti"
        :show-less="appointmentList.length > LIMIT_ACTVITIES"
        @on-show-more="changeAppointmentsLength('+')"
        @on-show-less="changeAppointmentsLength('-')"
      />


    </template>
    <div v-else>
      <p class="ps-3 mt-4">Non ci sono ancora appuntamenti</p>
    </div>
    <div class="row flex-row-reverse">
      <div class="col-12 col-md-6">
        <router-link
          :to="SERVICE_APPOINTMENT_BOOKING"
          class="w-100 btn btn-primary"
          type="button"
        >
          PRENOTA APPUNTAMENTO
        </router-link>
      </div>
    </div>
  </section>
</template>

<script>
import { SERVICE_APPOINTMENT_BOOKING } from "../../../router/routes";
import { LIMIT_ACTVITIES } from "../../../services/config";
import { orderBy } from "../../../services/utils";

import CfInnerLoading from "../../generic/CfInnerLoading.vue";

import FilterSearchButtons from "../FilterSearchButtons.vue";
import ShowMoreLessButton from '../ShowMoreLessButton.vue';
import AppointmentCard from "./AppointmentCard.vue";

export default {
  name: "AppointmentListSection",
  components: { CfInnerLoading, FilterSearchButtons, AppointmentCard, ShowMoreLessButton },
  emits: ["onDeleteAppointment"],
  props: {
    loadingAppointments: { type: Boolean, default: false },
  },
  data() {
    return {
      LIMIT_ACTVITIES,
      SERVICE_APPOINTMENT_BOOKING,
      appointmentList: [],
      appointmentsOffset: LIMIT_ACTVITIES,
      sortedAppointments: [],
    };
  },
  watch: {
    loadingAppointments: {
      immediate: true,
      handler(val, oldval) {
        if (!val) {
          this.appointmentList = this.appointments.slice(0, LIMIT_ACTVITIES);
          this.sortedAppointments = [...this.appointments];
        }
      },
    },
  },
  computed: {
    areMoreAppointments() {
      return this.appointments?.length > this.appointmentList?.length;
    },
    appointments() {
      return this.$store.getters["getAppointmentList"];
    },
  },
  methods: {
    onSortAppoinments(order) {
      this.appointmentsSort = order;
      this.sortedAppointments = orderBy(this.appointments, ["start"], [order]);
      this.appointmentList = this.sortedAppointments.slice(
        0,
        this.appointmentsOffset
      );
    },
    changeAppointmentsLength(type) {
      if (type === "-") {
        this.appointmentsOffset -= LIMIT_ACTVITIES;
      } else {
        this.appointmentsOffset += LIMIT_ACTVITIES;
      }

      this.appointmentList = this.sortedAppointments.slice(
        0,
        this.appointmentsOffset
      );
    },
    onDeleteAppointment(id) {
      this.appointmentList = this.appointmentList.map((appointment) => {
        if (appointment.id === id) {
          appointment.deleted = true;
        }
        return appointment;
      });
    },
  },
};
</script>
