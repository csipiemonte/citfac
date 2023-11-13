<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<script>
import { NEW_REQUEST, SERVICE_APPOINTMENT_BOOKING } from "../../router/routes";
import { ASSETS_MAP } from "../../services/config";

const ICONS_MAP = {
  ["appuntamento"]: "it-calendar",
  ["faq"]: "it-help-circle",
  ["assistenza"]: "it-mail",
  ["numero-verde"]: "it-hearing",
  ["disservizio"]: "it-map-marker-circle"
}

export default {
  name: "ContactsSection",
  props: {
    serviceId: [Number, String],
  },
  data() {
    return {
      SERVICE_APPOINTMENT_BOOKING,
    };
  },
  async created() {},
  computed: {
    assetCodeList() {
      return this.$store.getters["getAssetCodeList"];
    },
    showAppointmentLink() {
      return this.assetCodeList?.includes(ASSETS_MAP.APPOINTMENTS);
    },
    showAssistanceLink() {
      return this.assetCodeList?.includes(ASSETS_MAP.ASSISTANCE);
    },
    assistanceRequestUrl() {
  

      let route = {
        name: NEW_REQUEST.name,
      };
      if (this.serviceId) {
        route.query = { servizio: this.serviceId };
      }
      return route;
    },
    contactsMenuList() {
      let configMenu = this.$store.getters["getComuniConfigMenu"];

      return configMenu ? configMenu["contatta-comune"] : [];
    },
  },
  methods: {
    getMenuItemIcon(classItem){

      let icon = ICONS_MAP[classItem] ?? "it-arrow-right"
      return this.$svgs.getSpriteSvgUrl(icon)
    },
    getMenuItemUrl(menuItem){
      if(menuItem.attributes.class === "assistenza"){
        menuItem.url += `?servizio=${this.serviceId}`
      }

      return  menuItem.url
    },
    getDataElement(menuItem){
      return menuItem.attributes ? menuItem.attributes["data-element"] : ""
    }
  },
  async mounted() {},
};
</script>

<template>
  <div class="bg-gray-card shadow-contacts">
    <div class="container">
      <div class="row d-flex justify-content-center p-contacts">
        <div class="col-12 col-lg-6">
          <div class="cf-contacts">
            <div class="card w-100">
              <div class="card-body">
                <div
                  class="mb-4"
                  v-for="(menuSection, index) in contactsMenuList"
                  :key="index"
                >
                  <h2 class="">{{ menuSection.title }}</h2>
                  <ul class="contact-list p-0">
                    <li
                      v-for="(menuItem, index) in menuSection.children"
                      :key="index"
                    >
                      <a
                        class="list-item"
                        :href="getMenuItemUrl(menuItem)"
                        :data-element="getDataElement(menuItem)"
                      >
                        <svg
                          class="icon icon-primary icon-sm"
                          aria-hidden="true"
                        >
                          <use
                            :xlink:href="
                             getMenuItemIcon(menuItem.attributes?.class)
                            "
                          ></use></svg
                        ><span>{{ menuItem.title }}</span></a
                      >
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
</template>
