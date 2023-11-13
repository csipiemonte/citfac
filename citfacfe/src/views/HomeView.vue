<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div>
    <main id="main-container" class="container my-5">
      <filter-search-buttons
        id="servizi"
        title="Servizi"
        hide-form
        hide-sort
        @on-search="onSearchServices"
      >
      </filter-search-buttons>


      <p class="text-500 mt-4"><strong>{{  filteredServiceList.length}}</strong> servizi trovati in ordine alfabetico</p>
      <div class="row align-items-stretch mt-3">
        <template v-if="filteredServiceList.length > 0">
          <div
            v-for="(service, index) in filteredServiceList"
            :key="index"
            class="col-12 col-lg-4 py-3"
          >
            <service-card
              class="h-100"
              :key="index"
              :service="service"
            ></service-card>
          </div>
        </template>

        <div v-else>
          <p class="ps-3 mt-4">
            Non ci sono servizi in base ai filtri di ricerca
          </p>
        </div>
      </div>
    </main>
    <contacts-section></contacts-section>
  </div>
</template>

<script>
import ContactsSection from "../components/generic/ContactsSection.vue";
import FilterSearchButtons from "../components/personal-area/FilterSearchButtons.vue";
import ServiceCard from "../components/personal-area/ServiceCard.vue";
export default {
  components: { ContactsSection, FilterSearchButtons, ServiceCard },
  data() {
    return {
      filteredServiceList: [],
    };
  },
  created() {
    this.filteredServiceList = this.servicesList;
  },
  computed: {
    servicesList() {
      let services = this.$store.getters["getComuniConfigServices"];
      return services.filter((s) => !!s.dettaglio);
    },
  },
  methods: {
    onSearchServices(value) {
      if (value && value.length > 0) {
        this.filteredServiceList = this.servicesList.filter((s) => {
          let name = s.nome?.toLowerCase();

          return name.search(value.toLowerCase()) >= 0;
        });
      } else {
        this.filteredServiceList = this.servicesList;
      }
    },
  },
};
</script>
