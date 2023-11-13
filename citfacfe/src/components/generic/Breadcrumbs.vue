<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<script>
//PER COMPORRE IL BREADCRUMBS BASTA UN ARRAY DI OGGETTI {url: "/url/del/link", name: "nome del link", type: "tipo di link (router o link esterno)"}  DA PASSARE Al
// PROPS "link"
import {LINK_TYPES_MAP} from "../../services/config"

export default {
  name: "Breadcrumbs",
  props: {
    links: Array,
  },
  data() {
    return {
      LINK_TYPES_MAP
    };
  },
  async created() {},
  computed: {
    getComuneUrl() {
      const comuneConfig = this.$store.getters["getComuniConfig"];
      let home = comuneConfig?.homePage ?? "";
      return home.replace(/\/$/, "");
    },
  },
  methods: {
    async goToUrl(url) {
      await this.$router.push({ path: url });

      if (this.$route.params) {
        window.location.reload();
      }
    },
    getLinkUrl(link){
      return `${this.getComuneUrl}/${link.url}`
    }
  },
  async mounted() {},
};
</script>

<template>
  <div class="container" id="breadcrumbs">
    <div class="row justify-content-center">
      <div class="col-12 col-lg-10">
        <div class="cf-breadcrumbs " role="navigation">
          <nav
            aria-label="Percorso di navigazione"
            class="breadcrumb-container"
          >
            <ol class="breadcrumb p-0" data-element="breadcrumb">
              <li class="breadcrumb-item">
                <a :href="getComuneUrl">Home </a>
              </li>

              <li
                v-for="(link, index) in links"
                :key="index"
                :class="{ active: index === links.length - 1 }"
                class="breadcrumb-item"
              ><template   v-if="index < links.length - 1">
                <a  v-if="link.type === LINK_TYPES_MAP.LINK"
                :href="getLinkUrl(link)"
                :data-element="link.data_element ?? null">
                <span aria-hidden="true" class="separator">/</span
                >{{ link.name }}
              </a>
                <router-link
                v-else
                :to="link.url"
                :data-element="link.data_element ?? null"
                ><span aria-hidden="true" class="separator">/</span
                >{{ link.name }}
              </router-link>
             
              </template>
                
                <template v-else
                  ><span class="separator">/</span>{{ link.name }}
                </template>
              </li>
            </ol>
          </nav>
        </div>
      </div>
    </div>
  </div>
</template>


