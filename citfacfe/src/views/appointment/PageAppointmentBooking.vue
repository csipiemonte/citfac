<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <main id="main-container" class="container mb-5">
    <breadcrumbs :links="BREADCRUMBS_LINK"></breadcrumbs>

    <!--    COMPONENTE TITOLO-->
    <title-section title="Prenotazione appuntamento"></title-section>

    <cf-page-section class="my-4">
      <div v-if="isLoading" class="my-4">
        <cf-inner-loading showing />
      </div>

      <stepper v-else v-model="activeStep" :steps="STEP_LIST">
        <!--    STEP LUOGO  -->
        <!-- -------------------------------------------------------------------  -->
        <template v-slot:[STEPS.PLACE]>
          <div class="mt-5 mx-1">
            <small>
              I campi contraddistinti dal simbolo asterisco (*) sono
              obbligatori</small
            >
            <form
              id="office-form"
              ref="officeForm"
              class="needs-validation"
              @submit.prevent="onNextPlaceSelection"
            >
              <div class="bg-gray pb-2 my-2">
                <div class="row m-4">
                  <div class="col-12 px-0 py-3">
                    <h3>Ufficio *</h3>
                  </div>

                  <!--     UFFICIO -->
                  <div class="col-12 bg-white pt-5 mb-4">
                    <cf-select
                      id="office"
                      v-model="selectedCategory"
                      :option-list="officeList"
                      label-text="Ufficio"
                      more-info="Scegli l'ufficio a cui vuoi richiedere l'appuntamento"
                      required
                      @on-select="onSelectOffice"
                    />
                  </div>
                  <!--     SPORTELLI -->
                  <div id="frontOfficeListContainer" class="p-0 col-12 mb-2">
                    <template v-if="selectedCategory">
                      <template v-if="isLoadingServices">
                        <cf-inner-loading showing />
                      </template>

                      <template v-else>
                        <div
                          v-for="frontOffice in frontOfficeList"
                          :key="frontOffice.id"
                          class="cf-card bg-white"
                        >
                          <appointment-front-office-item
                            v-model="selectedService"
                            :location="frontOffice"
                            @on-select="onSelectFrontOffice"
                          />
                        </div>
                      </template>
                    </template>
                  </div>
                </div>
              </div>

              <stepper-navigation
                :loading="isLoadingDates"
                first
                form-id="office-form"
                submit
                @prev="stepPrev"
              />
            </form>
          </div>
        </template>

        <!--    STEP  DATA E ORARIO -->
        <!-- -------------------------------------------------------------------  -->
        <template v-slot:[STEPS.DATETIME]>
          <div class="mt-5 mx-1">
            <small>
              I campi contraddistinti dal simbolo asterisco (*) sono
              obbligatori</small
            >

            <form-bg no-padding title="Appuntamenti disponibili *">
              <appointment-free-slot-calendar
                :default-date-slot="selectedDateSlot"
                :first-free-date="firstAvailableDate"
                :min-date="firstAvailableDate"
                :max-date="lastAvailableDate"
                :service-id="selectedService"
                @on-select="onDateSelected"
              />
            </form-bg>

            <form-bg v-if="office && frontOffice" no-padding title="Ufficio">
              <div class="p-3">
                <div class="it-list-wrapper">
                  <ul class="it-list">
                    <li>
                      <div class="list-item pb-3">
                        <h4 class="fw-bold mb-0 h4 text-black">
                          {{ office.name }}
                        </h4>
                      </div>
                    </li>
                    <li>
                      <div class="list-item">
                        <div class="py-2">
                          <small>Sportello</small>
                          <div class="fw-bold">{{ frontOffice?.name }}</div>
                        </div>
                      </div>
                    </li>
                    <li>
                      <div class="list-item">
                        <div class="py-2">
                          <small>Indirizzo</small>
                          <div class="fw-bold">{{ frontOffice?.location }}</div>
                        </div>
                      </div>
                    </li>
                    <li>
                      <div class="list-item">
                        <div class="py-2">
                          <small>Informazioni</small>
                          <div class="fw-bold">
                            {{ frontOffice?.additionalInformations }}
                          </div>
                        </div>
                      </div>
                    </li>
                  </ul>
                </div>
              </div>
            </form-bg>

            <div v-if="dateError" class="alert alert-danger" role="alert">
              <div class="text-warning fw-bold">ATTENZIONE</div>
              <div class="text-500">Selezionare data e orario disponibili</div>
            </div>

            <stepper-navigation @next="stepDateNext" @prev="stepPrev" />
          </div>
        </template>

        <!--    STEP DETTAGLI APPUNTAMENTO  -->
        <!-- -------------------------------------------------------------------  -->
        <template v-slot:[STEPS.DETAILS]>
          <div class="mt-5 mx-1">
            <small>
              I campi contraddistinti dal simbolo asterisco (*) sono
              obbligatori</small
            >
            <form
              id="details-form"
              ref="detailsform"
              novalidate
              @submit.prevent="onNextDetailsSelection"
            >
              <form-bg title="Dettagli">
                <div class="col-12 bg-white mb-4">
                  <cf-select
                    id="reason"
                    v-model="selectedReason"
                    :option-list="reasonList"
                    label-text="Motivo"
                    option-value="id"
                    option-label="name"
                    more-info="Scegli il  motivo dell'appuntamento"
                    required
                  />
                </div>

                <div class="col-12 bg-white pt-5 mb-4">
                  <input-textarea
                    id="details"
                    v-model="appointmentDetail"
                    cols="600"
                    label-text="Dettagli"
                    maxlength="600"
                    more-info="Aggiungi ulteriori dettagli. Inserire massimo 600 caratteri"
                    required
                    type="textarea"
                    @input="excludeDetailChars"
                  />
                </div>
              </form-bg>

              <stepper-navigation
                form-id="details-form"
                submit
                @prev="stepPrev"
              />
            </form>
          </div>
        </template>

        <!--    STEP RICHIEDENTE  -->
        <!-- -------------------------------------------------------------------  -->
        <template v-slot:[STEPS.USER]>
          <div class="mx-1">
            <form
              id="user-form"
              ref="userform"
              novalidate
              @submit.prevent="onNextUserSelection"
            >
              <form-bg no-padding title="Richiedente">
                <div v-if="user" class="p-3">
                  <div class="it-list-wrapper">
                    <ul class="it-list">
                      <li>
                        <div class="list-item">
                          <div class="py-2">
                            <small>Nome</small>
                            <div class="fw-bold">{{ name }}</div>
                          </div>
                        </div>
                      </li>
                      <li>
                        <div class="list-item">
                          <div class="py-2">
                            <small>Cognome</small>
                            <div class="fw-bold">{{ surname }}</div>
                          </div>
                        </div>
                      </li>
                      <li v-if="userContacts">
                        <div class="list-item">
                          <div class="py-2">
                            <small>Email</small>
                            <div class="fw-bold">{{ email }}</div>
                          </div>
                        </div>
                      </li>
                    </ul>
                  </div>
                </div>

                <div class="col-12 bg-white pt-4" v-else>
                  <input-text
                    id="name"
                    v-model="name"
                    label-text="Nome"
                    more-info="Inserisci il nome del richiedente"
                    required
                  ></input-text>

                  <input-text
                    id="surname"
                    v-model="surname"
                    label-text="Cognome"
                    more-info="Inserisci il cognome del richiedente"
                    required
                  ></input-text>

                  <input-text
                    id="taxcode"
                    v-model="taxCode"
                    label-text="Codice fiscale"
                    more-info="Inserisci il codice fiscale del richiedente"
                    required
                    is-uppercase
                  ></input-text>

                  <input-text
                    id="email"
                    v-model="email"
                    label-text="Email"
                    more-info="Inserisci l'indirizzo email del richiedente"
                    required
                    type="email"
                  ></input-text>
                </div>
              </form-bg>

              <stepper-navigation form-id="user-form" submit @prev="stepPrev" />
            </form>
          </div>
        </template>

        <!--    STEP RIEPILOGO  -->
        <!-- -------------------------------------------------------------------  -->
        <template v-slot:[STEPS.SUMMARY]>
          <div class="mx-1">
            <div class="bg-gray pb-2 my-2">
              <div class="row m-4">
                <div class="col-12 px-0 py-3">
                  <h3>Riepilogo</h3>
                </div>

                <!--     UFFICIO     -->
                <div class="col-12 bg-white p-3 mb-4">
                  <div class="it-list-wrapper">
                    <ul class="it-list">
                      <li>
                        <div class="list-item">
                          <div class="it-right-zone">
                            <h4 class="fw-bold mb-0 h4 text-black">Ufficio</h4>
                            <a
                              class="fw-bold small"
                              href="#ufficio"
                              @click.prevent.stop="goToOfficeStep"
                              >Modifica</a
                            >
                          </div>
                        </div>
                      </li>
                      <li>
                        <div class="list-item">
                          <div class="py-2">
                            <small>Tipologia ufficio</small>
                            <div class="fw-bold">
                              {{ office ? office.name : "-" }}
                            </div>
                          </div>
                        </div>
                      </li>
                      <li>
                        <div class="list-item">
                          <div class="py-2">
                            <small>Sportello</small>
                            <div class="fw-bold">
                              {{ frontOffice ? frontOffice.name : "-" }}
                            </div>
                          </div>
                        </div>
                      </li>
                    </ul>
                  </div>
                </div>

                <!--     DATA E ORARIO     -->
                <div class="col-12 bg-white p-3 mb-4">
                  <div class="it-list-wrapper">
                    <ul class="it-list">
                      <li>
                        <div class="list-item">
                          <div class="it-right-zone">
                            <h4 class="fw-bold mb-0 h4 text-black">
                              Data e orario
                            </h4>
                            <a
                              class="fw-bold small"
                              href="#dataorario"
                              @click.prevent.stop="goToDateStep"
                              >Modifica</a
                            >
                          </div>
                        </div>
                      </li>
                      <li>
                        <div class="list-item">
                          <div class="py-2">
                            <small>Data</small>
                            <div class="fw-bold">{{ dateLabel || "-" }}</div>
                          </div>
                        </div>
                      </li>
                      <li>
                        <div class="list-item">
                          <div class="py-2">
                            <small>Ora</small>
                            <div class="fw-bold">{{ timeLabel || "-" }}</div>
                          </div>
                        </div>
                      </li>
                    </ul>
                  </div>
                </div>

                <!--     DETTAGLI     -->
                <div class="col-12 bg-white p-3 mb-4">
                  <div class="it-list-wrapper">
                    <ul class="it-list">
                      <li>
                        <div class="list-item">
                          <div class="it-right-zone">
                            <h4 class="fw-bold mb-0 h4 text-black">
                              Dettagli appuntamento
                            </h4>
                            <a
                              class="fw-bold small"
                              href="#dettagli"
                              @click.prevent.stop="goToDetailsStep"
                              >Modifica</a
                            >
                          </div>
                        </div>
                      </li>

                      <li>
                        <div class="list-item">
                          <div class="py-2">
                            <small>Motivo</small>
                            <div class="fw-bold">
                              {{
                                appointmentReason ? appointmentReason.name : "-"
                              }}
                            </div>
                          </div>
                        </div>
                      </li>

                      <li>
                        <div class="list-item">
                          <div class="py-2">
                            <small>Dettagli</small>
                            <div class="fw-bold">
                              {{ appointmentDetail || "-" }}
                            </div>
                          </div>
                        </div>
                      </li>
                    </ul>
                  </div>
                </div>

                <!--     RICHIEDENTE     -->
                <div class="col-12 bg-white p-3 mb-4">
                  <div class="it-list-wrapper">
                    <ul class="it-list">
                      <li>
                        <div class="list-item">
                          <div class="it-right-zone">
                            <h4 class="fw-bold mb-0 h4 text-black">
                              Richiedente
                            </h4>
                            <a
                              v-if="!user"
                              class="fw-bold small"
                              href="#dettagli"
                              @click.prevent.stop="goToUserStep"
                              >Modifica</a
                            >
                          </div>
                        </div>
                      </li>

                      <li>
                        <div class="list-item">
                          <div class="py-2">
                            <small>Nome</small>
                            <div class="fw-bold">
                              {{ name ?? "-" }}
                            </div>
                          </div>
                        </div>
                      </li>
                      <li>
                        <div class="list-item">
                          <div class="py-2">
                            <small>Cognome</small>
                            <div class="fw-bold">
                              {{ surname ?? "-" }}
                            </div>
                          </div>
                        </div>
                      </li>
                      <li v-if="!user">
                        <div class="list-item">
                          <div class="py-2">
                            <small>Codice Fiscale</small>
                            <div class="fw-bold">
                              {{ taxCode ?? "-" }}
                            </div>
                          </div>
                        </div>
                      </li>

                      <li>
                        <div class="list-item">
                          <div class="py-2">
                            <small>Email</small>
                            <div class="fw-bold">
                              {{ email ?? "-" }}
                            </div>
                          </div>
                        </div>
                      </li>
                    </ul>
                  </div>
                </div>
              </div>
            </div>

            <stepper-navigation
              :loading="isSendingAppointment"
              form-id="details-form"
              next-label="Invia"
              @next="confirmAppointment"
              @prev="stepPrev"
            />
          </div>
        </template>
      </stepper>
    </cf-page-section>
  </main>

  <!--    SEZIONE FISSA DEI CONTATTI-->
  <contacts-section></contacts-section>
</template>

<script>
import Breadcrumbs from "../../components/generic/Breadcrumbs.vue";
import TitleSection from "../../components/generic/TitleSection.vue";
import Stepper from "../../components/stepper/Stepper.vue";
import CfPageSection from "../../components/generic/CfPageSection.vue";
import FormBg from "../../components/form/FormBg.vue";
import InputSelect from "../../components/form/InputSelect.vue";
import StepperNavigation from "../../components/stepper/StepperNavigation.vue";
import CfSelect from "../../components/form/CfSelect.vue";
import AppointmentFreeSlotCalendar from "../../components/appointments/AppointmentFreeSlotCalendar.vue";
import InputText from "../../components/form/InputText.vue";
import InputTextarea from "../../components/form/InputTextarea.vue";
import JustValidate from "just-validate";
import { format_date, isValidCF, login } from "../../services/utils";
import ContactsSection from "../../components/generic/ContactsSection.vue";
import AppointmentFrontOfficeItem from "../../components/appointments/AppointmentFrontOfficeItem.vue";
import {
  createAppointment,
  editAppointment,
  getAppointment,
  getAppointmentCategories,
  getAppointmentService,
  getCategoryServices,
} from "../../services/api";

import { startOfMonth, addMinutes } from "date-fns";
import {
  SERVICE_APPOINTMENT_BOOKING_SUCCESS,
} from "../../router/routes";
import CfInnerLoading from "../../components/generic/CfInnerLoading.vue";
import { ASSETS_MAP, LINK_TYPES_MAP } from "../../services/config";
import { isAssetPublic } from "../../services/business-logic";

const BREADCRUMBS_LINK = [
  {
    url: "servizi",
    name: "Servizi",
    data_element: "all-services",
    type: LINK_TYPES_MAP.LINK,
  },
  { url: "", name: "Prenotazione appuntamento", type: LINK_TYPES_MAP.ROUTER },
];

const STEPS = {
  PLACE: 1,
  DATETIME: 2,
  DETAILS: 3,
  USER: 4,
  SUMMARY: 5,
};

const STEP_LIST = [
  { id: STEPS.PLACE, name: "LUOGO" },
  { id: STEPS.DATETIME, name: "DATA E ORARIO" },
  { id: STEPS.DETAILS, name: "DETTAGLI APPUNTAMENTO" },
  { id: STEPS.USER, name: "RICHIEDENTE" },
  { id: STEPS.SUMMARY, name: "RIEPILOGO" },
];

export default {
  name: "PageAppointmentBooking",
  components: {
    CfInnerLoading,
    AppointmentFrontOfficeItem,
    ContactsSection,
    InputTextarea,
    InputText,
    AppointmentFreeSlotCalendar,
    CfSelect,
    StepperNavigation,
    InputSelect,
    FormBg,
    CfPageSection,
    Stepper,
    TitleSection,
    Breadcrumbs,
  },
  data() {
    return {
      STEPS,
      STEP_LIST,
      BREADCRUMBS_LINK,
      activeStep: STEPS.PLACE,
      appointment: null,
      appointmentId: null,
      isLoading: false,
      isLoadingServices: false,
      isLoadingAppointment: false,
      isSendingAppointment: false,
      selectedService: null,
      selectedCategory: null,
      appointmentDetail: "",
      frontOfficeValidation: false,
      userValidation: null,
      isSelectedCategory: false,
      officeValidation: false,
      detailsValidation: null,
      isLoadingDates: false,
      firstAvailableDate: null,
      lastAvailableDate: null,
      selectedDate: null,
      selectedDateSlot: {
        date: null,
        slot: null,
      },
      selectedReason: null,
      reasonValidation: false,
      dateError: false,
      officeForm: null,
      office: null,
      frontOffice: null,
      unavailableDateList: [],
      officeList: [],
      frontOfficeList: [],
      appointmentReasonId: null,
      name: null,
      surname: null,
      email: null,
      taxCode: null,
      serviceDetail: null
    };
  },

  async mounted() {
    scroll(0, 0);
    this.isLoading = true;
    if (!this.isActivePage) {
      window.open(this.getComuneUrl, "_self");
      return;
    }

    if (!this.isApiPublic && !this.user) {
      login(this.$route.path);
      return;
    }

    const { id, servizio , nid} = this.$route.query;
    if (id) {
      this.appointmentId = id;
    } else if (servizio) {
      this.selectedService = servizio;
    }

    if (this.user) {
      this.name = this.user.nome;
      this.surname = this.user.cognome;
      this.email = this.userContacts?.email;
      this.taxCode = this.user.codFisc
    }

    try {
      //LISTA CATEGORIE (UFFICI)
      let { data: categories } = await getAppointmentCategories();
      this.officeList = categories;
    } catch (e) {
      await this.$store.dispatch("setNotificationInfo", {
        body: "Non è stato possibile caricare l'elenco degli uffici",
      });
    }
    //GESTIONE MODIFICA - SE è presente l'id dell'appuntamento si tratta di modifica
    if (this.appointmentId) {
      await this.getAppointmentToEdit();
    } else {
      // Se è presente solo un ufficio lo selezioniamo in automatico
      if (this.officeList.length === 1) {
        this.selectedCategory = this.officeList[0]?.id;
        await this.onSelectOffice(this.selectedCategory);
      }

      // La query "servizio" indica il tipo di sotto-sportello 
      if (this.selectedService) {
        await this.selectDefaultFrontOffice();
      }
    // La query "nid" indica il servizio CMS da cui ricavare lo sportello preselezionato
      if(nid){
        await this.getOfficeFromService(nid)
      }
    }

    this.isLoading = false;
    await this.$nextTick(() => {
      let form = document.getElementById("office-form");
      if (form) {
        this.officeValidation = new JustValidate("#office-form", {
          validateBeforeSubmitting: true,
        });
      }
    });
  },
  watch: {},
  computed: {
    comuneConfig() {
      return this.$store.getters["getComuniConfig"];
    },
    isApiPublic() {
      return isAssetPublic(ASSETS_MAP.APPOINTMENTS);
    },
    getComuneUrl() {
      return this.comuneConfig?.homePage ?? "";
    },
    user() {
      return this.$store.getters["getUser"];
    },
    userContacts() {
      return this.user?.contatti ?? null;
    },

    dateLabel() {
      return format_date(this.selectedDate, "EEEE dd MMMM yyyy");
    },
    timeLabel() {
      return format_date(this.selectedDate, "HH:ss");
    },
    reasonList() {
      return this.frontOffice?.activitiesRelated ?? [];
    },
    appointmentReason() {
      return this.reasonList.find((r) => r.id === this.selectedReason);
    },
    isActivePage() {
      let assetCodeList = this.$store.getters["getAssetCodeList"];
      return assetCodeList?.includes(ASSETS_MAP.APPOINTMENTS);
    },
    servicesDetailList(){
      return this.$store.getters["getComuniConfigDetailServices"]
    }
  },
  methods: {
    async getAppointmentToEdit() {
      this.isLoadingAppointment = true;
      let params = {
        aggregates: true,
      };

      try {
        let { data: appointment } = await getAppointment(this.appointmentId, {
          params,
        });
        this.appointment = appointment;
      } catch (e) {
        await this.$store.dispatch("setNotificationInfo", {
          body: "Non è stato possibile recuperare l'appuntamento.",
        });
      }

      
      if (this.appointment) {
        this.selectedCategory = this.appointment.category?.id;

        this.selectedService = this.appointment.serviceId;
        this.office = this.appointment.category;
        this.frontOffice = this.appointment.service;
        let appointmentDate = new Date(this.appointment.start);

        this.selectedDateSlot = {
          date: appointmentDate,
          slot: format_date(appointmentDate, "HH:mm"),
        };

        this.selectedDate = `${format_date(appointmentDate, "yyyy-MM-dd")} ${
          this.selectedDateSlot.slot
        }:00`;

        //Separiamo il campo motivo dell'appuntamento con il campo note
        this.getEditAppointmentReason();

        this.activeStep = STEPS.SUMMARY;
      }
      this.isLoadingAppointment = false;
    },
    getEditAppointmentReason() {
      let notes = this.appointment.notes;
      //prendiamo le informazioni sul motivo di appuntamento
      let appointmentReason = notes.split("-");

      if (appointmentReason) {
        //prediamo l'id del campo motivo appuntamento:
        this.selectedReason = notes.substring(
          notes.indexOf("(") + 1,
          notes.lastIndexOf(")")
        );

        this.appointmentDetail = appointmentReason[1];
      }
    },
    async selectDefaultFrontOffice() {
      try {
        //Servizio selezionato dalla query
        let { data: service } = await getAppointmentService(
          this.selectedService
        );

        this.selectedCategory = service?.categoryId;
        this.office = this.officeList?.find(
          (o) => o.id === this.selectedCategory
        );
        this.frontOffice = service;
        this.goToDateStep();
      } catch (e) {
        await this.$store.dispatch("setNotificationInfo", {
          body: "Non è stato possibile caricare i dati dello sportello",
        });
      }
    },

    async getOfficeFromService(nid){
      let serviceDetail = this.servicesDetailList.find(s => s.idServizio?.toString() === nid.toString())
      this.selectedCategory = serviceDetail?.idSportello

      if(this.selectedCategory){
        await this.onSelectOffice(this.selectedCategory)
      }
    },
    createAriaLabel(label, value, small) {
      return `${label} ${value ?? ""} ${small}`;
    },
    stepNext() {
      this.activeStep += 1;
    },
    stepPrev() {
      this.activeStep -= 1;
    },
    goToDetailsStep() {
      this.activeStep = STEPS.DETAILS;
      this.$nextTick(() => {
        this.detailsValidation = new JustValidate("#details-form");
      });
    },
    goToUserStep() {
      this.activeStep = STEPS.USER;
      this.$nextTick(() => {
        this.userValidation = new JustValidate("#user-form");
      });
    },
    onDateSelected(date) {
      let selectedDay = date?.date
        ? format_date(date?.date, "yyyy-MM-dd")
        : null;
      let selectedSlot = date?.slot;
      this.selectedDateSlot = date;
      this.selectedDate = `${selectedDay} ${selectedSlot}:00`;

      if (!!selectedDay && !!selectedSlot) {
        this.dateError = false;
      }
    },
    onNextPlaceSelection() {
      this.setOfficeFormValidation();
    },
    onNextDetailsSelection() {
      this.setDetailsFormValidation();
    },
    onNextUserSelection() {
      if (this.user) {
        this.stepNext();
      } else {
        this.setUserFormValidation();
      }
    },
    stepDateNext() {
      this.dateError = false;
      if (this.selectedDate) {
        this.stepNext();

        this.$nextTick(() => {
          this.detailsValidation = new JustValidate("#details-form");
        });
      } else {
        this.dateError = true;
      }
    },
    setOfficeFormValidation() {
      let validation = this.officeValidation;

      validation.addField("#inputSelectoffice", [
        {
          rule: "required",
          errorMessage: "Campo obbligatorio",
        },
      ]);
      if (!!this.selectedCategory) {
        validation.addField("#frontOfficeListContainer", [
          {
            validator: () => {
              return !!this.selectedService;
            },
            errorMessage: "Selezionare uno sportello",
          },
        ]);
      }

      //SE LA VALIDAZIONE HA SUCCESSO
      validation.revalidate().then((isValid) => {
        if (isValid) {
          this.onNextDateSelection();
        }
      });
    },
    async getUnavailableDates() {
      this.isLoadingDates = true;
      //Chiamiamo prima il dettaglio servizio per vedere la prima data disponibile:
      try {
        let { data: service } = await getAppointmentService(
          this.selectedCategory
        );

        this.firstAvailableDate = service?.firstAvailableDate
          ? new Date(service?.firstAvailableDate)
          : null;

        this.lastAvailableDate = service?.lastAvailableDate
          ? new Date(service?.lastAvailableDate)
          : null;
      } catch (e) {
        await this.$store.dispatch("setNotificationInfo", {
          body: "Non è stato possibile recuperare i dettagli dello sportello",
        });
      }

      this.isLoadingDates = false;
    },
    async onNextDateSelection() {
      await this.getUnavailableDates();
      this.stepNext();
    },
    setDetailsFormValidation() {
      let validation = this.detailsValidation;

      validation.addField("#inputTextareadetails", [
        {
          validator: (value) => {
            return value.trim().length > 0;
          },
          errorMessage: "Campo obbligatorio",
        },
      ]);
      //SE LA VALIDAZIONE HA SUCCESSO
      validation.revalidate().then((isValid) => {
        if (isValid) {
          this.stepNext();

          this.$nextTick(() => {
            this.userValidation = new JustValidate("#user-form");
          });
        }
      });
    },
    setUserFormValidation() {
      let validation = this.userValidation;

      validation.addField("#inputTextname", [
        {
          rule: "required",
          errorMessage: "Campo obbligatorio",
        },
      ]);
      validation.addField("#inputTextsurname", [
        {
          rule: "required",
          errorMessage: "Campo obbligatorio",
        },
      ]);
      validation.addField("#inputTexttaxcode", [
        {
          rule: "required",
          errorMessage: "Campo obbligatorio",
        },

        {
          validator: (value) => isValidCF(value),
          errorMessage: " Inserire un codice fiscale valido",
        },
      ]);
      validation.addField("#inputTextemail", [
        {
          rule: "required",
          errorMessage: "Campo obbligatorio",
        },
        {
          rule: "email",
          errorMessage: "Deve essere un'email",
        },
      ]);

      //SE LA VALIDAZIONE HA SUCCESSO
      validation.revalidate().then((isValid) => {
        if (isValid) {
          this.stepNext();
        }
      });
    },
    goToOfficeStep() {
      this.activeStep = STEPS.PLACE;
      this.onSelectOffice(this.selectedCategory);
      this.$nextTick(() => {
        let form = document.getElementById("office-form");
        if (form) {
          this.officeValidation = new JustValidate("#office-form", {
            validateBeforeSubmitting: true,
          });
        }
      });
    },
    async confirmAppointment() {
      this.isSendingAppointment = true;
      if (this.appointmentId) {
        await this.editAppointment();
      } else {
        await this.createNewAppointment();
      }
      this.isSendingAppointment = false;
    },
    async createNewAppointment() {
     
      let payload = {
        book: new Date(),
        start: this.selectedDate,
        notes: `(${this.appointmentReason.id}) ${this.appointmentReason.name} - ${this.appointmentDetail}`,
        customer: {
          firstName: this.name,
          lastName: this.surname,
          email: this.email,
          codice_fiscale: this.taxCode,
        },

        providerId: "any-provider",
        serviceId: this.selectedService,
      };

      try {
        let { data: appointment } = await createAppointment(payload);

        let route = {
          name: SERVICE_APPOINTMENT_BOOKING_SUCCESS.name,
          params: {
            id: appointment.id,
          },
        };
        this.$router.replace(route);
      } catch (error) {
        let message = "Non è stato possibile salvare l'appuntamento.";
        if (error?.response?.statu === 422) {
          message = `${message} Nessuna disponibilità per la data e ora selezionata.`;
        }
        await this.$store.dispatch("setNotificationInfo", {
          body: message,
        });
      }
    },
    async editAppointment() {
    
      let payload = {
        start: this.selectedDate,
        notes: `(${this.appointmentReason.id}) ${this.appointmentReason.name} - ${this.appointmentDetail}`,
        providerId: "any-provider",
        serviceId: this.selectedService,
      };

      try {
        let { data: appointment } = await editAppointment(
          this.appointmentId,
          payload
        );

        let route = {
          name: SERVICE_APPOINTMENT_BOOKING_SUCCESS.name,
          params: {
            id: appointment.id,
          },
        };
        this.$router.replace(route);
      } catch (error) {
        let message = "Non è stato possibile modificare l'appuntamento.";
        if (error?.response?.statu === 422) {
          message = `${message} La data dell'appuntamento è superiore a 6 mesi.`;
        }
        await this.$store.dispatch("setNotificationInfo", {
          body: message,
        });
      }
    },
    async onSelectOffice(id) {
      this.office = this.officeList.find(
        (office) => office.id?.toString() === id?.toString()
      );
      this.isLoadingServices = true;
      try {
        let { data: services } = await getCategoryServices(id);
        this.frontOfficeList = services;
      } catch (e) {
        await this.$store.dispatch("setNotificationInfo", {
          body: "Non è stato possibile caricare l'elenco degli sportelli",
        });
      }
      this.isLoadingServices = false;
    },
    onSelectFrontOffice(val) {
      this.selectedService = val;
      this.frontOffice = this.frontOfficeList.find(
        (office) => office.id === val
      );
    },
    async goToDateStep() {
      await this.getUnavailableDates();
      this.activeStep = STEPS.DATETIME;
    },
   
    excludeDetailChars(event) {
      let val = event.target.value;
      this.appointmentDetail = val.replace("-", "");
    },
  },
};
</script>


