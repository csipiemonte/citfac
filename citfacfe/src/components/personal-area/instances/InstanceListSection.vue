<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <section id="pratiche" class="it-page-section mb-5">
    <filter-search-buttons hide-form hide-search hide-sort title="Pratiche" />

    <template v-if="loadingInstances">
      <cf-inner-loading showing class="p-2" />
    </template>

    <template v-else-if="instancesList.length > 0">
      <transition-group name="list">
        <div
          v-for="(instance, index) in instancesList"
          v-show="!instance.deleted"
          :key="index"
          class="my-2"
        >
          <instance-card
            :id="index"
            :instance="instance"
            parent-id="accordion-2"
            @on-delete="onDeleteInstance"
          ></instance-card>
        </div>
      </transition-group>

      <show-more-less-button
        :show-more="areMoreInstances"
        show-more-label="Vedi altre pratiche"
        show-less-label="Vedi meno pratiche"
        :show-less="areLessInstances"
        @on-show-more="getMoreIntances"
        @on-show-less="getLessInstances"
        :loading-more="isLoadingMoreInstances"
      />
    </template>

    <div v-else>
      <p class="ps-3 mt-4">
        <template v-if="areInstancesFiltered"
          >Non ci sono pratiche in base ai filtri di ricerca
        </template>

        <template v-else>Non ci sono ancora pratiche</template>
      </p>
    </div>
  </section>
</template>

<script>
import { getMoonInstances } from "../../../services/api";
import { LIMIT_ACTVITIES } from "../../../services/config";
import CfInnerLoading from "../../generic/CfInnerLoading.vue";
import FilterSearchButtons from "../FilterSearchButtons.vue";
import ShowMoreLessButton from "../ShowMoreLessButton.vue";
import InstanceCard from "./InstanceCard.vue";
export default {
  components: {
    FilterSearchButtons,
    CfInnerLoading,
    InstanceCard,
    ShowMoreLessButton,
  },
  name: "InstanceListSection",
  props: {
    loadingInstances: { type: Boolean, default: false },
  },
  data() {
    return {
      isLoadingMoreInstances: false,
      instancesList: [],
      totalInstancesElements: 0,
      areInstancesFiltered: false,
      offset: LIMIT_ACTVITIES,
    };
  },
  watch: {
    loadingInstances: {
      immediate: true,
      handler(val, oldval) {
        if (!val) {
          let defaultInstances = this.instances?.instances ?? [];
          this.instancesList = [...defaultInstances];
          this.totalInstancesElements = this.instances?.total;
        }
      },
    },
  },
  computed: {
    instances() {
      return this.$store.getters["getInstanceList"];
    },
    areMoreInstances() {
      return this.totalInstancesElements > this.instancesList?.length;
    },
    areLessInstances() {
      return this.instancesList?.length > LIMIT_ACTVITIES;
    },
    idMoonToken() {
      return this.$store.getters["getMoonIdToken"];
    },
  },
  methods: {
    onDeleteInstance(code) {
      this.instancesList = this.instancesList.map((i) => {
        if (i.codiceIstanza === code) {
          i.deleted = true;
        }
        return i;
      });
    },
    async getLessInstances() {
      this.offset -= LIMIT_ACTVITIES;
      this.instancesList = this.instancesList.slice(0, -LIMIT_ACTVITIES);
    },

    async getMoreIntances() {
      this.offset += LIMIT_ACTVITIES;

      this.isLoadingMoreInstances = true;
      let params = {
        limit: LIMIT_ACTVITIES,
        offset: this.offset,
      };

      try {
        let { data } = await getMoonInstances(this.idMoonToken, params);
        let instancesList = data?.items;
        this.instancesList = [...this.instancesList, ...instancesList];
        this.totalInstancesElements = data?.totalElements;
      } catch (e) {
        await this.$store.dispatch("setNotificationInfo", {
          body: "Non è stato possibile recuperare le pratiche",
        });
      }

      this.isLoadingMoreInstances = false;
    },
  },
};
</script>
