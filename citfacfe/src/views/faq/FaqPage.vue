<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<script>
import ContactsSection from "../../components/generic/ContactsSection.vue";
import ValutationSection from "../../components/generic/ValutationSection.vue";
import Breadcrumbs from "../../components/generic/Breadcrumbs.vue";
import TitleSection from "../../components/generic/TitleSection.vue";
import InputSearch from "../../components/generic/InputSearch.vue";
import { getFaqList, getFaqTypeList, searchFaq } from "../../services/api";
import FaqTypeCard from "../../components/faq/FaqTypeCard.vue";
import FaqCard from "../../components/faq/FaqCard.vue";
import { capitalizeFirst, createSlug } from "../../services/utils";
import InputSelect from "../../components/form/InputSelect.vue";
import { FAQ_COD } from "../../services/business-logic";
import { FAQ_DETAIL } from "../../router/routes";

export default {
  name: "FaqPage",
  components: {
    InputSelect,
    FaqCard,
    FaqTypeCard,
    InputSearch,
    TitleSection,
    Breadcrumbs,
    ValutationSection,
    ContactsSection,
  },
  data() {
    return {
      isLoading: false,
      subFaqTypeList: [],
      timerSerach: null,
      searchOptions: null,
      faqList: [],
    };
  },

  async created() {
    scroll(0, 0);
    this.$emit("breadcrumbsLinks", []);
    this.loadFaq();
  },

  watch: {},
  computed: {
    title() {
      return capitalizeFirst(this.faqList?.category?.name);
    },
  },
  methods: {
    async searchInFaq(value) {
      if (value.length >= 0) {
        try {
          this.searchOptions = null;
          let payload = {
            knowledge_base_id: FAQ_COD,
            locale: "it-it",
            flavor: "agent",
            query: value,
          };
          let promise = await searchFaq(payload);
          this.searchOptions = promise.data;
        } catch (e) {
          await this.$store.dispatch("setNotificationInfo", {
            title: "Attenzione",
            body: "Non è stato possibile recuperare i dati",
          });
        }
      } else {
        this.searchOptions = null;
      }
    },
    async confirmSearchInFaq(option) {
      await this.goToFaq(option);
      this.searchOptions = null;
    },
    async onBack() {
      await this.$router.go(-1);
      this.faqList = [];
      setTimeout(this.loadFaq, 200);
    },
    async loadFaq() {
      this.isLoading = true;

      try {
        let typePromise = await getFaqTypeList();
        this.subFaqTypeList = typePromise.data;
      } catch (e) {
        await this.$store.dispatch("setNotificationInfo", {
          title: "Attenzione",
          body: "Non è stato possibile recuperare i dati",
        });
      }
      // }

      this.isLoading = false;
    },
    async goToFaq(type) {
      let categoryId = null;
      let answerId = null;
      let name = null;

      if (type.category_id) {
        categoryId = type.category_id;
        answerId = type.id;
        name = this.getSearchCategoryName(type);
      } else {
        categoryId = type.id;
        name = type.name;
      }
     
      let route = {
        name: FAQ_DETAIL.name,
        params: {
          id: categoryId,
          metaTitle: createSlug(name),
        },
        query: {
          answerId: answerId,
        },
      };

      await this.$router.push(route);
    },
    getSearchCategoryName(option) {
      const answerSubTitle = option.subtitle;
      let breadcrumbs = answerSubTitle.split(" > ");
      return breadcrumbs[breadcrumbs.length - 1];
    },
  },
  async mounted() {},
};
</script>

<template>
  <div>
    <section id="intro">
      <div class="mt-3">
        <div class="container">
          <div class="row justify-content-center">
           
            <div class="col-12 col-lg-10 h1">{{ title }}</div>

            <div class="col-12 col-lg-10 mt-2">
              <input-search
                id="1"
                label-text="Cerca in Assistenza"
                @updateSearch="searchInFaq"
                @confirmSearch="confirmSearchInFaq"
                search-from-list="true"
                :data-list-object="searchOptions"
              ></input-search>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section id="faq-types">
      <div class="section section-muted p-0 py-5">
        <div class="container">
          <div class="row">
            <div class="col-12 h1">Per cosa ti serve assistenza?</div>
          </div>

          <!--            CATEGORIE FAQ-->
          <template v-if="!isLoading">
            
            <div class="row">
              <!--                    SOTTO CATEGORIE FAQ-->
              <div
                class="col-xl-4 col-lg-6 col-12 gy-3"
                :key="index"
                v-for="(type, index) in subFaqTypeList"
              >
                <faq-type-card
                  @click="goToFaq(type)"
                  :type="type"
                ></faq-type-card>
              </div>
            </div>
          </template>
          <template v-else>
            <div class="row py-2 justify-content-center full-width overlay">
              <div class="col-auto">
                <div class="progress-spinner progress-spinner-active">
                  <span class="visually-hidden">Caricamento...</span>
                </div>
              </div>
            </div>
          </template>
        </div>
      </div>
    </section>
  </div>
</template>


