<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="row">
    <div class="d-none d-sm-none d-lg-block col-lg-3">
      <cf-page-index id="messages" :menu-items="NAV_MESSAGES" inline/>
    </div>

    <div class="col-12 col-lg-8 offset-lg-1">
      <section
          id="messages"
          class="it-page-section mb-5"
      >
        <filter-search-buttons
          id="messages"
          :hidden-filters="!areMessagesFiltered && messagesList.length <= 0"
          hide-search
          title="Notifiche"
          @on-filter="onFilterMessages"
          @on-cancel-filter="onCancelFilters"
          @filter-form-validation="onFilterValidation"
        >
          <template #dropdownList>
         
              <li>
                <a class="dropdown-item list-item" @click="onSort('-timestamp')"
                  ><span>Data più recente</span></a
                >
              </li>
              <li>
                <a class="dropdown-item list-item" @click="onSort('+timestamp')"
                  ><span>Data meno recente</span></a
                >
              </li>
          
          </template>

          <template #filterContent>
            <input-text
              id="message-start-date"
              v-model="filterDate.start"
              :max="maxDate"
              class="col-12 col-md-6"
              clearable
              label-text="Da"
              type="date"
              :error-info="startDateError"
              :error="!!startDateError"
            />
            <input-text
              id="message-end-date"
              v-model="filterDate.end"
              :max="maxDate"
              class="col-12 col-md-6"
              clearable
              label-text="A"
              type="date"
              :error-info="endDateError"
              :error="!!endDateError"
            />
            <cf-select
              id="message-service"
              v-model="service"
              :disabled="isLoadingServices"
              :option-list="servicesList"
              class="col-12 col-md-6"
              clearable
              label-text="Servizio"
            />
            <cf-select
              id="message-status"
              v-model="readStatus"
              :option-list="READ_STATUS_LIST"
              class="col-12 col-md-6"
              clearable
              label-text="Stato lettura"
            />

            <div
              v-if="messagesFilterError"
              id="filterErroContainer"
              aria-live="polite"
            >
              <div
                class="alert alert-danger alert-dismissible fade show"
                role="alert"
              >
                {{ messagesFilterError }}
              </div>
            </div>
          </template>
        </filter-search-buttons>

        <template v-if="messagesList.length > 0">
          <TransitionGroup name="list">
            <div
              v-for="(message, index) in messagesList"
              v-show="!message.deleted"
              :key="index"
              class="py-2"
            >
              <messages-list-item
                :id="index"
                :message="message"
                @on-delete="onDeleteMessage"
                @on-read="onReadMessage"
              />
            </div>
          </TransitionGroup>

          <button
              v-if="areMoreMessages"
              class="btn  btn-default-primary mb-2 pt-3 text-primary are-more-btn ps-lg-3"
              type="button"
              @click="getMoreMessages"
          >
            <div class="row justify-content-between">
              <div class="col">Vedi altre notifiche</div>
              <div v-if="isLoadingMoreMessages" class="col-auto">
                <cf-inner-loading showing small />
              </div>
            </div>
          </button>
        </template>

        <div v-else>
          <p class="ps-3 mt-4">
            <template v-if="areMessagesFiltered"
              >Non ci sono notifiche in base ai filtri di ricerca
            </template>

            <template v-else>Non ci sono ancora notifiche</template>
          </p>
        </div>
      </section>
    </div>
  </div>
</template>

<script>
import CfPageIndex from "../../components/generic/CfPageIndex.vue";
import FilterSearchButtons from "../../components/personal-area/FilterSearchButtons.vue";
import { format_date } from "../../services/utils";
import { getNotifications, getNotifyServicesList } from "../../services/api";
import InputText from "../../components/form/InputText.vue";
import CfSelect from "../../components/form/CfSelect.vue";
import MessagesListItem from "../../components/personal-area/MessagesListItem.vue";
import CfInnerLoading from "../../components/generic/CfInnerLoading.vue";
import { setNotifyPreferences } from "../../services/business-logic";
import { isAfter } from "date-fns";
import { DATE_REGEX } from "../../services/config";
import { PERSONAL_AREA_DESKTOP } from "../../router/routes";

const NAV_MESSAGES = [
  {
    target: "messages",
    nome: "Notifiche",
  },
];

const READ_STATUS_LIST = [
  {
    id: "false",
    name: "Non letti",
  },
  {
    id: "true",
    name: "Letti",
  },
];
const LIMIT = 15;
export default {
  name: "PageMessagesList",
  inject: ["hostname", "utente", "utenteIsLoading", "showNotifications"],
  components: {
    CfInnerLoading,
    MessagesListItem,
    CfSelect,
    InputText,
    FilterSearchButtons,
    CfPageIndex,
  },
  props: {
    loadingMessages: Boolean,
    messages: Object,
  },
  emits: ["onDeleteMessage", "onReadMessage"],
  data() {
    return {
      NAV_MESSAGES,
      READ_STATUS_LIST,
      areMessagesFiltered: false,
      messagesList: [],
      messagesFilterError: null,
      offset: 1, // la prima chiamata è già stata fatta dalla homepage
      service: null,
      filterDate: {
        start: null,
        end: null,
      },
      sorting: null,
      readStatus: null,
      isFilteringMessages: false,
      totalElements: 0,
      totalUnreadMessages: 0,
      maxDate: new Date(),
      isLoadingServices: false,
      isLoadingMoreMessages: false,
      startDateError: null,
      endDateError: null,
      filterFormValidation: null,
    };
  },
  computed: {
    getComuneConfig() {
      return this.$store.getters["getComuniConfig"];
    },
    userTaxCode() {
      return this.utente?.codFisc;
    },

    areMoreMessages() {
      return this.totalElements > this.messagesList?.length;
    },
    servicesList() {
      let services = this.$store.getters["getNotifyServicesList"] ?? [];
      return services.map((s) => {
        return {
          id: s.uuid,
          name: s.name,
        };
      });
    },
  },

  async created() {
    if (!this.showNotifications) {
      this.$router.replace(PERSONAL_AREA_DESKTOP);
    }
    this.maxDate = format_date(new Date(), "yyyy-MM-dd");
    if (!this.servicesList?.length) {
      this.isLoadingServices = true;
      await setNotifyPreferences();
      this.isLoadingServices = false;
    }
  },
  watch: {
    loadingMessages: {
      immediate: true,
      handler(val, oldval) {
        if (!val) {
          let defaultList = this.messages?.messages ?? [];
          this.messagesList = [...defaultList];
          this.totalElements = this.messages?.total;
        }
      },
    },
    filterDate: {
      deep: true,
      handler() {
        if (this.areMessagesFiltered) this.formMessagesValidation();
      },
    },
  },
  methods: {
    formMessagesValidation() {
      this.startDateError = null;
      this.endDateError = null;
      this.messagesFilterError = null;
      let start = this.filterDate.start;
      let end = this.filterDate.end;
      const inputStart = document.getElementById("inputTextmessage-start-date");
      let validityStartState = inputStart.validity;
      const inputEnd = document.getElementById("inputTextmessage-end-date");
      let validityEndState = inputEnd.validity;

      inputStart.reportValidity();
      inputEnd.reportValidity();

      if (validityStartState.badInput) {
        this.startDateError = "Inserire una data valida";
      }
      if (validityEndState.badInput) {
        this.endDateError = "Inserire una data valida";
      }

      if (validityStartState.valid && validityEndState.valid) {
        if (!!start && !!end) {
          let startDate = new Date(start);
          let endDate = new Date(end);
          if (isAfter(startDate, endDate)) {
            let msg =
              "La data iniziale deve essere precedente alla data finale";
            this.startDateError = msg;
            this.endDateError = msg;
          } else {
            this.areMessagesFiltered = true;
            this.offset = 0;
            this.messagesList = [];
            this.fetchMessages();
          }
        } else if (!!start && !end) {
          this.endDateError = "Inserire un periodo valido";
        } else if (!!end && !start) {
          this.startDateError = "Inserire un periodo valido";
        } else if (this.service || this.readStatus) {
          this.areMessagesFiltered = true;
          this.offset = 0;
          this.messagesList = [];
          this.fetchMessages();
        }
      }
    },
    onFilterValidation(validation) {
      this.filterFormValidation = validation;
    },
    onFilterMessages() {
      this.formMessagesValidation();
    },

    async fetchMessages() {
    
      let filters = `"tag":{"match": "+citfac ${this.getComuneConfig.codiceEnte} -deleted" }`;
      if (this.service) {
        filters += `, "sender": {"eq": "${this.service}"}`;
      }

      if (this.filterDate.start && this.filterDate.end) {
        let startDate = new Date(this.filterDate.start).toISOString();
        let endDate = new Date(this.filterDate.end).toISOString();
        filters += `, "timestamp": {"lte": "${endDate}" , "gte": "${startDate}"}`;
      }

      if (this.readStatus) {
        filters += `, "read_at": {"null": ${this.readStatus}}`;
      }
      this.isFilteringMessages = true;

      try {
        let params = {
          offset: this.offset,
          limit: LIMIT,
          filter: `{${filters}}`,
          sort: this.sorting ?? null,
        };

        let response = await getNotifications(this.userTaxCode, params);
        let messagesList = response?.data;

        this.messagesList = [...this.messagesList, ...messagesList];

        this.totalElements = response.headers["total-elements"];
        this.totalUnreadMessages = response.headers["total-elements-not-read"];
      } catch (e) {
        await this.$store.dispatch("setNotificationInfo", {
          body: "Non è stato possibile ottenere le notifiche",
        });
      }
      this.isFilteringMessages = false;
    },
    onCancelFilters() {
      this.service = null;
      this.filterDate = { start: null, end: null };
      this.readStatus = null;
      if (this.areMessagesFiltered) {
        this.offset = 0;
        this.fetchMessages();
      }
    },
    onSort(value) {
      this.sorting = value;
      this.offset = 0;
      this.messagesList = [];
      this.fetchMessages();
    },
    onDeleteMessage(id) {
      setTimeout(() => {
        this.messagesList = this.messagesList.map((n) => {
          if (n.id === id) {
            n.deleted = true;
          }
          return n;
        });
      }, 500);
      this.$emit("onDeleteMessage", id);
    },
    onReadMessage(id) {
      this.$emit("onReadMessage", id);
    },
    async getMoreMessages() {
      this.isLoadingMoreMessages = true;
      this.offset += LIMIT;
      await this.fetchMessages();
      this.isLoadingMoreMessages = false;
    },
  },
};
</script>

