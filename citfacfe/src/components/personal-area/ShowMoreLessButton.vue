<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="mb-3 row align-items-center justify-content-start">
    <div class="col-md-auto p-0">
      <button
        v-show="showMore"
        class="btn btn-default-primary are-more-btn"
        type="button"
        @click="$emit('onShowMore')"
      >
        <div class="row justify-content-between">
          <div class="col">{{ showMoreLabel }}</div>
          <div v-if="loadingMore" class="col-auto">
            <cf-inner-loading showing small />
          </div>
        </div>
      </button>
    </div>
    <div class="col-md-auto text-primary p-0" v-if="showLess && showMore">
      |
    </div>

    <div class="col-md-auto p-0">
      <button
        v-show="showLess"
        class="btn btn-default-primary are-more-btn"
        type="button"
        @click="$emit('onShowLess')"
      >
        <div class="row justify-content-between">
          <div class="col">{{ showLessLabel }}</div>
          <div v-if="loadingLess" class="col-auto">
            <cf-inner-loading showing small />
          </div>
        </div>
      </button>
    </div>
  </div>
</template>

<script>
import CfInnerLoading from "../generic/CfInnerLoading.vue";
export default {
  components: { CfInnerLoading },
  name: "ShowMoreLessButton",
  emits: ["onShowMore", "onShowLess"],
  props: {
    showMore: Boolean,
    showLess: Boolean,
    showMoreLabel: String,
    showLessLabel: String,
    loadingMore: Boolean,
    loadingLess: Boolean,
  },
};
</script>
