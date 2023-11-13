<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="row">
    <div class="d-none d-sm-none d-lg-block col-lg-3">
      <cf-page-index id="services" :menu-items="NAV_SERVICES" inline />
    </div>

    <div class="col-12 col-lg-8 offset-lg-1">
      <section id="servizi" class="it-page-section mb-5">
        <filter-search-buttons
          id="servizi"
          :hidden-filters="loadingServices  && !refresh"
          title="Servizi"
          hide-form
          hide-sort
          @on-search="onSearchServices"
        >
        </filter-search-buttons>
        <template v-if="loadingServices && !refresh">
          <cf-inner-loading showing />
        </template>

        <template v-else-if="filteredServiceList.length > 0">
          <div
              v-for="(service, index) in filteredServiceList"
              :key="index"
              class="my-2"
            >
              <service-card
              favourite-icon
                @updateServiceList="refreshServiceList"
                :key="index"
                :service="service"
              ></service-card>
            </div>
        </template>

        <div v-else>
          <p class="ps-3 mt-4">
            <template v-if="areFilteredServices"
              >Non ci sono servizi in base ai filtri di ricerca
            </template>

            <template v-else>Nessun risultato</template>
          </p>
        </div>
      </section>
    </div>

  
  </div>
</template>

<script>
import ServiceCard from "../../components/personal-area/ServiceCard.vue";
import CfInnerLoading from "../../components/generic/CfInnerLoading.vue";
import CfPageIndex from "../../components/generic/CfPageIndex.vue";
import FilterSearchButtons from "../../components/personal-area/FilterSearchButtons.vue";
const NAV_SERVICES = [
  {
    target: "services",
    nome: "Servizi",
  },
];
export default {
  name: "PageServicesList",
  components: { CfInnerLoading, ServiceCard, CfPageIndex, FilterSearchButtons },
  props: {
    loadingServices: Boolean,
    services: Array,
    favouriteServicesIds: Array,
  },
  data() {
    return {
      NAV_SERVICES,
      hostname: window.location.hostname,
      refresh: false,
      favouriteServiceList: [],
      areFilteredServices:false,
      filteredServiceList: [],
    };
  },
  watch:{
    servicesFullList:{
      immediate:true,
      deep:true,
      handler(val){
       this.filteredServiceList = [...val]
      }
    }
  },
  computed: {
    servicesFullList() {
      let array = [];
      this.services.forEach((a) => {
        if (this.favouriteServicesIds.includes(a.id)) {
          a.preferito = true;
        } else {
          a.preferito = false;
        }
        if (!!a.dettaglio) {
          array.push(a);
        }
      });
      return array;
    },

  },
  async created() {},
  methods: {
    refreshServiceList() {
      this.refresh = true;
      this.$emit("onRefreshService");
    },
    onSearchServices(value){
      if (value && value.length > 0) {
        this.filteredServiceList = this.servicesFullList.filter((s) => {
          let name = s.nome?.toLowerCase()
        
          return (
            name.search(value.toLowerCase()) >= 0 
          );
        });
        this.areFilteredServices = true;
      } else {
        this.filteredServiceList = this.servicesFullList;
        this.areFilteredServices = false;
      }
    }
  },
};
</script>

