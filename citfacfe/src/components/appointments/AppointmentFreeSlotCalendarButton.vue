<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <button :class="classes" class="btn appointment-free-slot-calendar__button" type="button" :disabled="!available"
          @click="onDateSelected">
    {{ formatDate }}
  </button>
</template>

<script>
import {format_date} from "../../services/utils";

export default {
  name: "AppointmentFreeSlotCalendarButton",
  props: {
    date: [Date, String],
    active: Boolean,
    available: Boolean
  },
  computed: {
    formatDate() {
      return format_date(this.date, "d")
    },
    classes() {
      let result = [];
      if (this.available && !this.active)
        result.push("appointment-free-slot-calendar__date--available btn-outline-primary");
      if (this.active) result.push("appointment-free-slot-calendar__date--selected  btn-primary");
      return result;
    },
  },
  methods: {
    onDateSelected() {
      this.$emit('on-selected', this.date)
    }

  }
}
</script>

