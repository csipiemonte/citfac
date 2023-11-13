<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div id="" class="cf-card mb-4">
    <div class="card shadow px-4 pt-4 pb-4 rounded h-100">
      <div class="border-0 p-0">
        <div class="row justify-content-between align-items-center mb-1">
          <div class="col">
            <p
              class="mb-0 text-decoration-none category text-uppercase text-primary"
            >
              <span v-for="(category, index) in categories" :key="index">
                <u>{{ category }}</u>
                <template v-if="index < categories.length - 1">
                  ,&nbsp;
                </template>
              </span>
            </p>
          </div>
          <div class="col-auto" v-if="favouriteIcon">
            <template v-if="!isLoadingFavourite">
              <div class="btn btn-flat text-primary p-0">
                <template v-if="isFavourite">
                  <svg
                    @click="removeFavourite"
                    aria-hidden="true"
                    class="icon icon-primary icon-sm"
                  >
                    <use
                      :xlink:href="$svgs.getSpriteSvgUrl('it-star-full')"
                    ></use>
                  </svg>
                </template>
                <template v-else>
                  <svg
                    aria-hidden="true"
                    @click="setFavourite"
                    class="icon icon-primary icon-sm"
                  >
                    <use
                      :xlink:href="$svgs.getSpriteSvgUrl('it-star-outline')"
                    ></use>
                  </svg>
                </template>
              </div>
            </template>
            <template v-else>
              <cf-inner-loading class="justify-self-end" showing small />
            </template>
          </div>
        </div>
      </div>
      <div class="card-body d-flex flex-column p-0 my-2">
        <h3 class="green-title-big text-primary col-auto">
          {{ title }}
        </h3>
        <p class="text-paragraph col">
          {{ description }}
        </p>
        <div
          class="row mt-sm-3 justify-content-between align-items-center g-3 col-auto"
        >
          <div class="col-12 col-sm text-center text-sm-start">
            <a
              v-if="urlScheda"
              class="btn px-0 btn-default-primary"
              :href="urlScheda"
              >Maggiori info
            </a>
          </div>

          <div class="col-12 col-sm text-center text-sm-end">
            <a
              class="btn btn-primary"
              type="button"
              :href="urlServizio"
              data-element="service-link"
              @click.prevent="goToService"
            >
              Vai al servizio
            </a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { SERVICE_MOON } from "../../router/routes";
import CfInnerLoading from "../../components/generic/CfInnerLoading.vue";
import {
  removeFavouriteService,
  setFavouriteService,
} from "../../services/api";

export default {
  name: "ServiceCard",
  components: { CfInnerLoading },
  props: {
    service: Object,
    favouriteIcon: Boolean,
  },
  data() {
    return {
      isLoadingFavourite: false,
    };
  },
  computed: {
    serviceId() {
      return this.service.id;
    },
    categories() {
      return this.service.categorie?.map((cat) => cat.nome) ?? [];
    },
    isFavourite() {
      return this.service.preferito;
    },
    title() {
      return this.service.nome;
    },
    urlScheda() {
      return this.service.link_scheda;
    },
    urlServizio() {
      return "servizi/" + this.serviceId;
    },
    description() {
      return this.service.descrizione;
    },
    isMoonService(){
      return this.service?.dettaglio?.moduloMoon
    }
  },
  methods: {
    goToService() {
      let service = this.service;
      this.$store.dispatch("setActiveService", { service });

      let route = {
        name: SERVICE_MOON.name,
        params: {
          id: this.serviceId,
        },
      };
      this.$router.push(route);

    
    },
    async setFavourite() {
      this.isLoadingFavourite = true;

      try {
        let id = this.service.id;
        let promise = await setFavouriteService(id);
        this.$emit("updateServiceList");
      } catch (e) {
        await this.$store.dispatch("setNotificationInfo", {
          body: "Non è stato possibile modificare i preferiti",
        });
      }
      this.isLoadingFavourite = false;

      
    },
    async removeFavourite() {
      this.isLoadingFavourite = true;

      try {
        let id = this.service.id;
        let promise = await removeFavouriteService(id);
        this.$emit("updateServiceList");
      } catch (e) {
        await this.$store.dispatch("setNotificationInfo", {
          body: "Non è stato possibile modificare i preferiti",
        });
      }
      this.isLoadingFavourite = false;

     
    },
  },
};
</script>


