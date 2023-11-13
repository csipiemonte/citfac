<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<script>
import InputSearch from "../../components/generic/InputSearch.vue";
import { getFaqList,  } from "../../services/api";
import FaqTypeCard from "../../components/faq/FaqTypeCard.vue";
import FaqCard from "../../components/faq/FaqCard.vue";
import { capitalizeFirst, createSlug } from "../../services/utils";
import InputSelect from "../../components/form/InputSelect.vue";
import {  } from "../../services/business-logic";
import {  FAQ_DETAIL } from "../../router/routes";
import CfPageSection from "../../components/generic/CfPageSection.vue";

export default {
  name: "FaqPageDetail",
  components: {
    InputSelect,
    FaqCard,
    FaqTypeCard,
    InputSearch,
    CfPageSection,
  },

  emits: ["breadcrumbsLinks"],

  data() {
    return {
      breadcrumbsLinks: [],
      typeId: null,
      isLoading: false,
      isLoadingFaqlist: false,
      subFaqTypeList: [],
      faqList: [],
      metadata: null,
      answerId: null,
    };
  },

  async created() {
    scroll(0, 0);
    this.loadFaq();
  },

  computed: {
    title() {
      return capitalizeFirst(this.faqList?.category?.name) ?? "";
    },
  },

  methods: {
    async onBack() {
      await this.$router.go(-1);
    },

    async loadFaq() {
      this.isLoading = true;
      this.typeId = this.$route.params.id;
      let { id, metaTitle } = this.$route.params;
      this.typeId = id;
      this.answerId = this.$route.query?.answerId;
      if (this.typeId) {
        await this.findFaq(this.typeId);
      }
      if (!metaTitle) {
        let route = this.getFaqDetailUrl(this.typeId, this.title);
        this.$router.replace(route);
      }

      this.isLoading = false;
    },
    async goToFaq(type) {
      let categoryId = null;

      if (type.category_id) {
        categoryId = type.category_id;
      } else {
        categoryId = type.id;
      }

      let route = this.getFaqDetailUrl(categoryId, type.name);

      await this.$router.push(route);
    },
    getFaqDetailUrl(id, name) {
      let route = {
        name: FAQ_DETAIL.name,
        params: {
          id: id,
          metaTitle: createSlug(name),
        },
      };

      return route;
    },

    async findFaq(typeId) {
      scroll(0, 0);
      let answerCardId = null;
      try {
        this.isLoadingFaqlist = true;

        this.breadcrumbsLinks = [];

        this.faqList = [];
        this.subFaqTypeList = [];
        let promise = await getFaqList(typeId);
        this.faqList = promise.data;
        if (this.answerId) {
          this.faqList.answers.forEach((a, index) => {
            if (a.id.toString() === this.answerId) {
              a.isOpen = true;
              answerCardId = index;
            }
          });
        }

        let breadcrumbsLinks = this.faqList.breadcrumb;
        breadcrumbsLinks.forEach((a) => {
          let route = this.getFaqDetailUrl(a.id, a.name);
          this.breadcrumbsLinks.push({
            url: route,
            name: capitalizeFirst(a.name),
          });
        });

        this.subFaqTypeList = this.faqList.children;
      } catch (e) {
        await this.$store.dispatch("setNotificationInfo", {
          title: "Attenzione",
          body: "Non è stato possibile recuperare i dati",
        });
      }

      this.$emit("breadcrumbsLinks", this.breadcrumbsLinks);
      this.isLoadingFaqlist = false;
      this.$nextTick(() => {
        let faqCard = document.getElementById("faqCard" + answerCardId);

        if (faqCard) {
          faqCard.scrollIntoView();
        }
      });
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
          </div>
        </div>
      </div>
    </section>
    <section id="faq" v-if="faqList && faqList.answers">
      <div class="section section-muted p-0 py-2">
        <div class="container">
          <template v-if="!isLoadingFaqlist">
            <cf-page-section id="faq-types">
              <div
                v-if="subFaqTypeList && subFaqTypeList.length"
                class="section section-muted p-0 py-5"
              >
                <div class="container">
                  <!--            CATEGORIE FAQ-->

                 
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
                </div>
              </div>

              <div class="">
                <div
                  v-for="(faq, index) in faqList.answers"
                  class="faq-accordion"
                  :key="index"
                >
                  <div class="accordion" id="accordion-1">
                    <faq-card
                      :faq="faq"
                      :category="faqList.category"
                      :id="index"
                      parent-id="accordion-1"
                    ></faq-card>
                  </div>
                </div>
              </div>

              <div class="row my-4">
                <div class="col-md-auto col-12 text-center">
                  <svg class="icon ms-1">
                    <use
                      :xlink:href="
                        $svgs.getSpriteSvgUrl('it-arrow-left-triangle')
                      "
                    ></use>
                  </svg>
                  <a class="back-button" @click="onBack">Indietro</a>
                </div>
              </div>
            </cf-page-section>
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


