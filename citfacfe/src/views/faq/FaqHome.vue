<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<script>
import ContactsSection from "../../components/generic/ContactsSection.vue";
import ValutationSection from "../../components/generic/ValutationSection.vue";
import Breadcrumbs from "../../components/generic/Breadcrumbs.vue";
import TitleSection from "../../components/generic/TitleSection.vue";
import { FAQ } from '../../router/routes';
import { capitalizeFirst } from '../../services/utils';


const BREADBRUMBS = [
        { url: FAQ, name: "FAQ" },
      ]
export default {
  name: "FaqHome",
  components: {
    TitleSection,
    Breadcrumbs,
    ValutationSection,
    ContactsSection,
  },

  data() {
    return {
      breadcrumbsLinks: [],
     
    };
  },

  async created() {
    scroll(0, 0);

    this.breadcrumbsLinks= [...BREADBRUMBS]
  },

  watch: {},
  computed: {
    title() {
      return capitalizeFirst(this.faqList?.category?.name);
    },
    valutationTitle(){
      return this.title ? `FAQ ${this.title}` : 'FAQ'
    }
  },
  methods: {

    setBreadcrumbsLinks(breadcrumbs){
       
        this.breadcrumbsLinks =  [...BREADBRUMBS]
        this.breadcrumbsLinks = [...this.breadcrumbsLinks, ...breadcrumbs]

       
    },

  },
  async mounted() {},
};
</script>

<template>
  <main>
    <breadcrumbs :links="breadcrumbsLinks"></breadcrumbs>
    <title-section
      title="FAQ"
      subtitle="Elenco di risposte alle domande più frequenti raccolte dalle richieste di assistenza dei cittadini"
    ></title-section>

    <router-view  :key="$route.fullPath" @breadcrumbsLinks="setBreadcrumbsLinks" ></router-view>

  </main>

  
  <!--    SEZIONE VALUTAZIONI-->
  <valutation-section  page faq></valutation-section>

  <!--    SEZIONE FISSA DEI CONTATTI-->
  <contacts-section></contacts-section>
</template>


