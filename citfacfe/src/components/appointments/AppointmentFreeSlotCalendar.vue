<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="appointment-free-slot-calendar">
    <div class="row px-2">
      <!-- DATE PICKER -->
      <!-- --------------------------------------------------------------------------------------------------------- -->
      <div class="col-12 col-md">
        <div class="py-3 text-center fw-bold">
          Seleziona una data disponibile
        </div>

        <!-- MONTH PICKER -->
        <!-- ------------------------------------------------------------------------------------------------------- -->
        <div
          class="appointment-free-slot-calendar__month-selector p-2 text-center bg-gray"
        >
          <div class="align-items-center row justify-content-between">
            <div class="col-auto">
              <button
                :disabled="isLoadingDates || !isPreviousMonthButtonEnabled"
                aria-label="Vai a mese precedente"
                class="btn btn-outline-primary appointment-free-slot-calendar__month-selector__previous-button"
                type="button"
                @click="slotTemporaleDown"
              >
                <svg class="icon right icon-primary">
                  <use
                  :xlink:href="$svgs.getSpriteSvgUrl('it-chevron-left')"    
                  ></use>
                </svg>
              </button>
            </div>
            <template v-if="!isFirstLoading">
              <div class="col fw-semibold text-capitalize">
                {{ mounthYearLabel }}
              </div>
            </template>
            <div class="col-auto">
              <button
                :disabled="isLoadingDates || !isNextMonthButtonEnabled"
                aria-label="Vai a mese successivo"
                class="btn btn-outline-primary appointment-free-slot-calendar__month-selector__next-button"
                type="button"
                value="Input"
                @click="slotTemporaleUp"
              >
                <svg class="icon left icon-primary">
                  <use
                  :xlink:href="$svgs.getSpriteSvgUrl('it-chevron-right')"
                  ></use>
                </svg>
              </button>
            </div>
          </div>
        </div>

        <!-- CARICAMENTO PERIODI LIBERI -->
        <!-- ------------------------------------------------------------------------------------------------------- -->
        <template v-if="isLoadingDates">
          <cf-inner-loading showing class="mb-2" small />
        </template>

        <!-- SLOT AGENDA NON SELEZIONABILI o NESSUNA DATA LIBERA -->
        <!-- ------------------------------------------------------------------------------------------------------- -->
        <template
          v-else-if="
            !isAgendaSlotSelectable ||
            availableDateList.length <= 0 ||
            errorInUnavailableDateList
          "
        >
          <template v-if="!errorInUnavailableDateList">
            <div class="text-center my-2">
              Non esiste nessuna disponibilità nel mese selezionato.
            </div>
          </template>
          <template v-else>
            Non è stato possibile caricare le date disponibili, si prega di
            riprovare più tardi
          </template>
        </template>

        <!-- CI SONO DATE LIBERE E SELEZIONABILI -->
        <!-- ------------------------------------------------------------------------------------------------------- -->
        <template v-else>
          <div class="appointment-free-slot-calendar__month-view p-3 mb-4">
            <div class="row">
              <div class="col small">Lu</div>
              <div class="col small">Ma</div>
              <div class="col small">Me</div>
              <div class="col small">Gi</div>
              <div class="col small">Ve</div>
              <div class="col small">Sa</div>
              <div class="col small">Do</div>
            </div>

            <div
              v-for="(week, index) in dateInMonthMatrix"
              :key="index"
              class="row my-1"
            >
              <div v-for="(date, index) in week" :key="index" class="col">
                <div v-if="date" class="">
                  <appointment-free-slot-calendar-button
                    :available="isDateAvailable(date)"
                    :active="isDateSelected(date)"
                    :date="date"
                    @on-selected="onDateSelected"
                  />
                </div>
              </div>
            </div>
          </div>
        </template>
      </div>

      <!-- TIME PICKER -->
      <!-- --------------------------------------------------------------------------------------------------------- -->
      <div class="col-12 col-md">
        <template v-if="dateSelected">
          <div class="py-3 text-center fw-bold">
            Seleziona orario disponibile
          </div>
          <!-- DAY RESUME -->
          <!-- ----------------------------------------------------------------------------------------------------- -->
          <div
            class="appointment-free-slot-calendar__month-selector p-2 text-center"
          >
            <div class="align-items-center row justify-content-between">
              <div class="col-auto">
                <button
                  :disabled="selectedDateIndex <= 0"
                  aria-label="Vai alla precedente data diponibile "
                  class="btn btn-outline-primary appointment-free-slot-calendar__month-selector__previous-button"
                  type="button"
                  @click="previousDate"
                >
                  <svg class="icon right icon-primary">
                    <use
                    :xlink:href="$svgs.getSpriteSvgUrl('it-chevron-left')"
                  
                    ></use>
                  </svg>
                </button>
              </div>

              <div class="col fw-semibold text-capitalize">
                {{ dateSelectedLabel }}
              </div>
              <div class="col-auto">
                <button
                  :disabled="selectedDateIndex >= availableDateList.length - 1"
                  aria-label="Vai alla successiva data disponibile"
                  class="btn btn-outline-primary appointment-free-slot-calendar__month-selector__next-button"
                  type="button"
                  value="Input"
                  @click="nextDate"
                >
                  <svg class="icon left icon-primary">
                    <use
                    :xlink:href="$svgs.getSpriteSvgUrl('it-chevron-right')"
                    
                    ></use>
                  </svg>
                </button>
              </div>
            </div>
          </div>

          <div
            class="appointment-free-slot-calendar__slot-list row align-items-stretch p-3 mb-4"
          >
            <!-- CARICAMENTO ORARI DISPONIBILI -->
            <!-- ------------------------------------------------------------------------------------------------------- -->
            <template v-if="isLoadingSlotList">
              <cf-inner-loading showing class="mb-2" small />
            </template>

            <!-- SLOT AGENDA NON SELEZIONABILI o NESSUNA DATA LIBERA -->
            <!-- ------------------------------------------------------------------------------------------------------- -->
            <template v-else-if="slotList.length <= 0">
              <div class="text-center my-2">
                Non esiste nessuna disponibilità per la data selezionata.
              </div>
            </template>

            <template v-else>
              <div style="max-height: 220px" class="overflow-auto">
                <div
                  v-for="(slot, index) in slotList"
                  :key="index"
                  class="col-12 text-center py-1 scroll-filter-wrapper"
                >
                  <appoint-free-slot-time-button
                    :slot="slot"
                    @on-select="onSlotSelected"
                    :active="isSlotSelected(slot)"
                  />
                </div>
              </div>
            </template>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<script>
import { format_date, groupBy, orderBy } from "../../services/utils";
import CfInnerLoading from "../generic/CfInnerLoading.vue";
import {
  addDays,
  addMonths,
  differenceInCalendarMonths,
  differenceInDays,
  differenceInMinutes,
  endOfMonth,
  getDay,
  startOfMonth,
  subMonths,
  isSameDay,
  isAfter,
} from "date-fns";
import AppointmentFreeSlotCalendarButton from "./AppointmentFreeSlotCalendarButton.vue";
import AppointFreeSlotTimeButton from "./AppointFreeSlotTimeButton.vue";
import {
  getServiceAvailableHours,
  getServiceUnavailableDates,
} from "../../services/api";

export default {
  name: "AppointmentFreeSlotCalendar",
  components: {
    AppointFreeSlotTimeButton,
    AppointmentFreeSlotCalendarButton,
    CfInnerLoading,
  },
  props: {
    minDate: { type: Date, required: false, default: () => new Date() },
    maxDate: { type: Date, required: false, default: null },
    month: {
      type: Number,
      required: false,
      default: () => new Date().getMonth(),
    },
    year: {
      type: Number,
      required: false,
      default: () => new Date().getFullYear(),
    },
    firstFreeDate: { type: Date, required: false, default: null },
    selectFirstDate: { type: Boolean, required: false, default: false },
    serviceId: [String, Number],
    defaultDateSlot: Object,
  },
  emits: ["onSelect"],
  data() {
    return {
      isFirstLoading: false,
      isLoadingSlotList: false,
      slotList: [],
      calendarDate: null,
      dateSelected: null,
      slotSelected: null,
      selectedDateIndex: null,
      unavailableDatesList: [],
      isLoadingDates: false,
      errorInUnavailableDateList: false,
    };
  },
  created() {
    if (this.defaultDateSlot?.date) {
      this.calendarDate = this.defaultDateSlot?.date;
    } else {
      this.calendarDate = isAfter(this.firstFreeDate, new Date())
        ? this.firstFreeDate
        : new Date();
    }
  },
  mounted() {
    //USO UNA PRIMA VARIABILE DI CARICAMENTO PER NASCONDERE IL MESE PRIMA CHE VENGA FISSATO
    this.isFirstLoading = true;

    //SE HO GIà UN APPUNTAMENTO LO SETTO E CALCOLO LE DATE DI NON VALIDITà ALTRIMENTI CALCOLO SOLO LE DATE DI NON VALIDITA
    if (this.defaultDateSlot?.date && this.defaultDateSlot?.slot) {
      this.setDefaultDateSlot();
    } else {
      this.getUnavailableDateList();
    }
    this.isFirstLoading = false;
  },
  watch: {
    dateSelected: {
      handler(val, oldVal) {
        if (!!val) {
          this.getSlotList();
        }
      },
    },
  },
  computed: {
    startOfMonth() {
      return startOfMonth(this.calendarDate);
    },
    endOfMonth() {
      return endOfMonth(this.calendarDate);
    },
    isPreviousMonthButtonEnabled() {
      return differenceInCalendarMonths(this.minDate, this.calendarDate) < 0;
    },
    isNextMonthButtonEnabled() {
      return (
        !this.maxDate ||
        differenceInCalendarMonths(this.maxDate, this.calendarDate) > 0
      );
    },
    mounthYearLabel() {
      return this.calendarDate
        ? format_date(this.calendarDate, "MMMM yyyy")
        : "";
    },
    dateInMonthList() {
      let result = [];
      let date = new Date(this.startOfMonth);

      while (differenceInDays(this.endOfMonth, date) >= 0) {
        result.push(date);
        date = addDays(date, 1);
      }

      return result;
    },
    isAgendaSlotSelectable() {
      let isAfterOrSameMinMonth =
        differenceInCalendarMonths(this.minDate, this.calendarDate) <= 0;
      let isBeforeOrSameMaxMonth =
        !this.maxDate ||
        differenceInCalendarMonths(this.maxDate, this.calendarDate) >= 0;
      return isAfterOrSameMinMonth && isBeforeOrSameMaxMonth;
    },
    dateInMonthMatrix() {
      // aggiungiamo alla lista dei giorni del mese dei placeholder prima del primo giorno
      // e dopo l'ultimo giorno così da poter creare una matrice
      let startDayOfWeek = getDay(this.startOfMonth);

      let endDayOfWeek = getDay(this.endOfMonth);

      let before = new Array(startDayOfWeek);
      if (startDayOfWeek > 0) {
        before = new Array(startDayOfWeek - 1);
      }

      let after = new Array(6 - endDayOfWeek);

      let list = [...before, ...this.dateInMonthList, ...after];

      // creiamo ora una matrice che ha N righe ma ogni riga ha 7 colonne
      let matrix = [];
      let size = 7;

      for (let i = 0; i < list.length; i = i + size) {
        matrix.push(list.slice(i, i + size));
      }
      //LA CREAZIONE DEL CALENDARIO PRENDE IL PRIMO GIORNO DEL MESE SUCCESSIVO DA RIMUOVERE
      //matrix.pop();
      let lastWeek = matrix[matrix.length - 1];
      lastWeek[endDayOfWeek] = undefined;
      return matrix;
    },
    dateSelectedLabel() {
      return this.dateSelected
        ? format_date(this.dateSelected, "EEEE dd MMMM yyyy")
        : "";
    },
    availableDateList() {
      return this.dateInMonthList.filter((date) => this.isDateAvailable(date));
    },
  },
  methods: {

    async setDefaultDateSlot() {
      this.dateSelected = this.defaultDateSlot?.date;

      //SE CI SONO DATE VALIDE CALCOLO GLI SLOT
      if (this.availableDateList.length > 0) {
        await this.getSlotList();
        this.onSlotSelected(this.defaultDateSlot?.slot);
      }
      //SE ENTRANDO NON HO DATE LIBERE NEL MESE, CARICO SUBITO IL MESE SUCCESSIVO
      let i = 0;
      while (this.availableDateList.length === 0 && i < 3) {
        await this.slotTemporaleUp();
        i++;
      }
    },
    async getUnavailableDateList() {
      this.isLoadingDates = true;
      this.errorInUnavailableDateList = false;

      try {
        let date = this.startOfMonth;
        let newDate = new Date();
        newDate.setDate(15);
        newDate.setMonth(date.getMonth());
        newDate.setFullYear(date.getFullYear());

        let params = {
          date: newDate,
        };

        let { data: unavailableDates } = await getServiceUnavailableDates(
          this.serviceId,
          { params }
        );
        this.unavailableDatesList = unavailableDates?.map((d) => new Date(d));
      } catch (e) {
        this.errorInUnavailableDateList = true;
      }

      this.isLoadingDates = false;
    },
    async getSlotList() {
      this.isLoadingSlotList = true;
      try {
        const date = format_date(this.dateSelected, "yyyy-MM-dd");
        let params = {
          date: date,
        };
        let { data: list } = await getServiceAvailableHours(this.serviceId, {
          params,
        });
        this.slotList = list;
      } catch (e) {}

      this.isLoadingSlotList = false;
    },
    isDateAvailable(date) {
      // Escludiamo le date <= minDate

      if (differenceInMinutes(date, this.minDate) < 0) return false;

      // Escludiamo le date > maxDate
      if (this.maxDate && differenceInDays(date, this.maxDate) > 0) {
        return false;
      }

      return !this.unavailableDatesList?.find((d) => isSameDay(d, date));
    },
    isDateSelected(date) {
      return isSameDay(date, this.dateSelected);
    },
    onDateSelected(date) {
      this.onSlotUnselected();
      this.dateSelected = this.availableDateList?.find((d) => {
        if (isSameDay(d, date)) {
          this.selectedDateIndex = this.availableDateList.indexOf(d);
          return true;
        }
        return false;
      });
      this.calendarDate = this.dateSelected;
    },
    onSlotSelected(slot) {
      this.slotSelected = slot;
      //Passiamo data e ora
      let date = {
        date: this.dateSelected,
        slot: this.slotSelected,
      };

      this.$emit("onSelect", date);
    },
    onSlotUnselected() {
      if (!this.slotSelected) return;
      this.slotSelected = null;
      this.$emit("onSelect", null);
    },
    onDateUnselected() {
      if (!this.dateSelected) return;
      this.dateSelected = null;
      this.$emit("onSelect", null);
    },
    async slotTemporaleDown() {
      this.onSlotUnselected();
      this.onDateUnselected();
      this.calendarDate = subMonths(this.calendarDate, 1);
      await this.getUnavailableDateList();
    },
    async slotTemporaleUp() {
      this.onSlotUnselected();
      this.onDateUnselected();
      this.calendarDate = addMonths(this.calendarDate, 1);
      await this.getUnavailableDateList();
    },
    async previousDate() {
      if (this.selectedDateIndex === 0) {
        this.calendarDate = subMonths(this.calendarDate, 1);
        await this.getUnavailableDateList();
      }
      let selectedSlot = this.availableDateList[this.selectedDateIndex - 1];
      this.onDateSelected(selectedSlot);
    },
    async nextDate() {
      if (this.selectedDateIndex === this.availableDateList.length - 1) {
        this.calendarDate = addMonths(this.calendarDate, 1);
        await this.getUnavailableDateList();
      }
      let selectedSlot = this.availableDateList[this.selectedDateIndex + 1];
      this.onDateSelected(selectedSlot);
    },
    isSlotSelected(slot) {
      return slot === this.slotSelected;
    },
  },
};
</script>


