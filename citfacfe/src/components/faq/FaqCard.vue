<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="accordion-item p-3">
    <div class="accordion-header">
      <button
        :id="currentId"
        @click="hideAccordion"
        style="border-style: none !important"
        class="accordion-button collapsed title-small-semi-bold"
        type="button"
        :aria-expanded="isExpanded"
        :aria-controls="currentId"
      >
        <div class="button-wrapper">
          {{ faqTitle }}
        </div>
      </button>
    </div>

    <div
      :id="currentTarget"
      class="accordion-collapse collapse p-0 m-0"
      role="region"
      :aria-labelledby="currentId"
    >
      <template v-if="!isLoadingDetail">
        <div class="accordion-body">
          <p class="mb-2 fw-normal">
            <span class="label fw-bold">{{
              $filters.stripHTML(answerText)
            }}</span>
          </p>
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
</template>

<script>
import InputTextArea from "../form/InputTestualeArea.vue";
import InputFile from "../form/InputFile.vue";

import { getFaqAnswer } from "../../services/api";

export default {
  name: "FaqCard",
  components: { InputFile, InputTextArea },
  props: {
    faq: Object,
    category: Object,
    id: [String, Number],
    parentId: String,
  },

  data() {
    return {
      currentId: null,
      currentTarget: null,
      currentTargetParentId: null,
      accordion: null,
      isExpanded: false,
      isLoadingDetail: false,
      answer: null,
    };
  },
  watch: {
    category() {
      if (this.accordion && this.accordionButton) {
        this.accordionButton.classList.add("collapsed");
        this.accordionButton.classList.remove("showed");

        this.accordion.classList.add("collapse");
        this.accordion.classList.remove("show");
        this.isExpanded = false;
        this.answer = null;
      }
    },
  },
  async created() {
    this.currentId = "faqCard" + this.id;
    this.currentTarget = "#" + this.currentId;
    this.currentTargetParentId = "#" + this.parentId;
  },

  computed: {
    faqTitle() {
      return this.faq?.title;
    },
    answerText() {
      return this.answer?.body;
    },
  },
  mounted() {
    this.accordion = document.getElementById(this.currentTarget);
    this.accordionButton = document.getElementById(this.currentId);
    if (this.faq.isOpen === true) {
      this.hideAccordion();
    }
  },

  methods: {
    async loadFaqDetail() {
      this.isLoadingDetail = true;
      try {
        let promise = await getFaqAnswer(this.category.id, this.faq.id);
        this.answer = promise.data;
      } catch (e) {
        await this.$store.dispatch("setNotificationInfo", {
          body: "Non è stato possibile visualizzare i dati",
        });
      }
      this.isLoadingDetail = false;
    },

    hideAccordion() {
      if (this.accordionButton.classList.contains("collapsed")) {
        this.accordionButton.classList.add("showed");
        this.accordionButton.classList.remove("collapsed");
      } else {
        this.accordionButton.classList.add("collapsed");
        this.accordionButton.classList.remove("showed");
      }
      if (this.accordion.classList.contains("show")) {
        this.accordion.classList.add("collapse");
        this.accordion.classList.remove("show");
        this.isExpanded = false;
      } else {
        this.accordion.classList.add("show");
        this.accordion.classList.remove("collapse");
        this.isExpanded = true;
      }
      if (!this.answer) {
        this.loadFaqDetail();
      }
    },
  },
};
</script>
