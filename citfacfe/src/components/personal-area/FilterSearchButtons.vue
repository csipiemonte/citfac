<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="cf-filter filter-search-buttons">
    <!--   BOTTONI FILTRI    -->
    <div class="filter-section">
      <h2 class="cf-filter__title">
        {{ title }}
      </h2>
      <div
        v-if="!hiddenFilters"
        class="filter-wrapper d-flex align-items-center gap-2"
      >
        <button
          v-if="!hideForm"
          :id="filterButtonId"
          :aria-controls="filterContentId"
          :disabled="disabled"
          class="btn px-2"
          type="button"
          @click="useToggleAccordion(filterContentId, filterButtonId)"
        >
          <span class="rounded-icon">
            <svg aria-hidden="true" class="icon icon-primary icon-xs me-1">
              <use :xlink:href="$svgs.getSpriteSvgUrl('it-funnel')"></use>
            </svg>
          </span>

          <span class="">Filtra</span>
        </button>

        <cf-dropdown
          v-if="!hideSort"
          :id="dropdownButtonId"
          :disabled="disabled"
          type="button"
          button-classes=" px-2"
        >
          <template #button>
            <span> Ordina</span>
          </template>

          <template #list>
            <slot name="dropdownList"></slot>
          </template>
        </cf-dropdown>
      </div>
    </div>
    <template v-if="!hiddenFilters">
      <!--   CARD FILTRI    -->
      <div
        :id="filterContentId"
        v-if="!hideForm"
        class="collapse cf-accordion row"
      >
        <div class="card-wrapper d-block">
          <div class="cf-card border my-2">
            <form
              class="card-body"
              :id="filterformId"
              @submit.prevent="$emit('on-filter')"
              @reset.prevent="$emit('on-cancel-filter')"
            >
              <div class="row">
                <slot name="filterContent" />
              </div>

              <div class="it-card-footer row flex-md-row-reverse">
                <div class="col-12 col-md-auto mb-2">
                  <button
                    aria-label="filtra notifiche"
                    class="btn btn-primary w-100"
                    type="submit"
                  >
                    Filtra
                  </button>
                </div>

                <div class="col-12 col-md-auto mb-2">
                  <button
                    aria-label="filtra notifiche"
                    class="btn btn-outline-primary w-100"
                    type="reset"
                  >
                    Annulla filtri
                  </button>
                </div>
              </div>
            </form>
          </div>
        </div>
      </div>

      <!--   RICERCA    -->
      <div v-if="!hideSearch" class="cf-input-search mt-3">
        <div class="form-group autocomplete-wrapper">
          <div class="input-group">
            <label :for="searchId" class="visually-hidden"
              >Cerca nel sito</label
            >
            <input
              :id="searchId"
              :name="searchId"
              class="autocomplete form-control"
              data-bs-autocomplete="[]"
              placeholder="Cerca"
              v-model="text"
              type="search"
              @search="onSearch"
              @input="onSearch"
            />

            <span
              aria-hidden="true"
              class="autocomplete-icon"
              style="z-index: 1"
            >
              <svg class="icon icon-sm">
                <use :xlink:href="$svgs.getSpriteSvgUrl('it-search')"></use>
              </svg>
            </span>
            <div class="input-group-append">
              <button class="btn btn-primary" type="button" @click="onSearch">
                Invio
              </button>
            </div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script>
import { useToggleAccordion } from "../../composables/toggleAccordion";

import CfDropdown from "../generic/CfDropdown.vue";

export default {
  components: { CfDropdown },
  name: "FilterSearchButtons",
  emits: [
    "on-filter",
    "on-cancel-filter",
    "on-search",
    "filter-form-validation",
  ],
  props: {
    title: String,
    id: String,
    disabled: Boolean,
    hideSearch: Boolean,
    hiddenFilters: Boolean,
    hideForm: Boolean,
    hideSort: Boolean,
  },

  data() {
    return {
      text: "",
    };
  },
  computed: {
    filterformId() {
      return `filterForm${this.id}`;
    },
    filterButtonId() {
      return `filterButton${this.id}`;
    },
    filterContentId() {
      return `filterContent${this.id}`;
    },
    dropdownButtonId() {
      return `dropdownButton${this.id}`;
    },
    searchId() {
      return `searchInput${this.id}`;
    },
  },
  mounted() {},
  methods: {
    useToggleAccordion,
    onSearch(event) {
      this.$emit("on-search", this.text);
    },
  },
};
</script>
