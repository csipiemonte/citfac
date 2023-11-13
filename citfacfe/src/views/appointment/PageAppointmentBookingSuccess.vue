<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <main id="main-container" class="appointment-booking-success container mb-5">
    <breadcrumbs :links="BREADCRUMBS_LINK"></breadcrumbs>
    <cf-page-section class="mb-4">
      <div class="alert alert-success my-3" role="alert">
        <h1 class="alert-heading h2 pb-2">Appuntamento confermato</h1>

        <p>Abbiamo inviato una mail di riepilogo all'email: <br>
          <strong>{{ email }}</strong>
        </p>
        <p>Puoi consultare o modificare la tua prenotazione accedendo alla tua
          <router-link :to="PERSONAL_AREA">Area riservata</router-link>
        </p>
      </div>

      <div class="mt-5" v-if="isLoading">
        <cf-inner-loading showing />
      </div>
      <template v-else>
        <h2 class="h3 fw-bold  mt-5">Dove</h2>
        <div>
          <div class="cf-card-simple office-address-card card-wrapper pb-0 rounded border border-light  ">
            <div class="card shadow-sm rounded">
              <div class="card-body border-start border-5 border-light">
                <p class="mb-0">
                  <strong>{{officeName}}</strong> <br>
                  {{address}}
                </p>

              </div>
            </div>


          </div>
        </div>

        <h2 class="h3 fw-bold  mt-5">Data e orario</h2>
        <div>
          <div class="cf-card-simple office-address-card card-wrapper pb-0 rounded border border-light  ">
            <div class="card shadow-sm rounded">
              <div class="card-body border-start border-5 border-light">
                <p class="">
                  <strong>Data</strong> <br>
                  {{dateLabel}}
                </p>
                <p class="mb-0">
                  <strong>Ora</strong> <br>
                  {{slotLabel}}
                </p>
              </div>
            </div>


          </div>
        </div>


        <h2 class="h3 fw-bold  mt-5">Aggiungi al tuo calendario</h2>

        <div>

          <a class="fw-bold" download="#">
            <svg class="icon icon-sm icon-primary me-2">
              <use 
              :xlink:href="$svgs.getSpriteSvgUrl('it-calendar')"
             ></use>
            </svg>
            Outlook, Google Calendar e iCloud Calendar</a>

        </div>
      </template>



    </cf-page-section>
  </main>

  <!--    SEZIONE VALUTAZIONI-->
  <valutation-section :asset="ASSETS_MAP.APPOINTMENTS"></valutation-section>

  <!--    SEZIONE FISSA DEI CONTATTI-->
  <contacts-section></contacts-section>
</template>

<script>
import Breadcrumbs from "../../components/generic/Breadcrumbs.vue";
import {format_date} from "../../services/utils";
import CfPageSection from "../../components/generic/CfPageSection.vue";
import ValutationSection from "../../components/generic/ValutationSection.vue";
import ContactsSection from "../../components/generic/ContactsSection.vue";
import { PERSONAL_AREA} from "../../router/routes";
import {getAppointment} from "../../services/api";
import CfInnerLoading from "../../components/generic/CfInnerLoading.vue";
import {ASSETS_MAP} from "../../services/config"

const BREADCRUMBS_LINK = [

  {url: "", name: "Prenotazione appuntamento"},
]
export default {
  name: "PageAppointmentBookingSuccess",
  components: {CfInnerLoading, ContactsSection, ValutationSection, CfPageSection, Breadcrumbs},
  data() {
    return {
      BREADCRUMBS_LINK,
      PERSONAL_AREA,
      isLoading: false,
      appointment:null,
      ASSETS_MAP,
    }
  },
  async mounted(){
    scroll(0, 0);
    this.isLoading =true
    let {id} = this.$route.params
    if(id){
      let params = {
        aggregates : true
      }
      try{
        let {data: appointment} = await getAppointment(id, {params})
        this.appointment = appointment
      }catch (e) {

      }
    }
    this.isLoading =false
  },
  computed: {
    userInfo() {
      return this.appointment?.customer
    },
    
    appointmentDate() {
      return this.appointment?.start
    },
    dateLabel(){
      return this.appointmentDate ? format_date(this.appointmentDate, "EEEE dd MMMM yyyy") : '-'
    },
    slotLabel(){
      return this.appointmentDate ? format_date(this.appointmentDate, "HH:ss") : '-'
    },
    email() {
      return this.userInfo?.email ?? '-'
    },
    office(){
      return this.appointment?.service
    },
    officeName(){
      return this.office?.name
    },
    address(){
      return this.office?.location
    }
  }
}
</script>

