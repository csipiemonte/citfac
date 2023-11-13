<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="row">
    <div class="d-none d-sm-none d-lg-block col-lg-3">
      <cf-page-index id="documents" :menu-items="NAV_DOCUMENTS" inline/>
    </div>

    <div class="col-12 col-lg-8 offset-lg-1">
      <section
          id="documenti"
          class="it-page-section mb-5"
      >
        <filter-search-buttons
            id="documenti"
            :hidden-filters="
                      !areFilteredDocuments && documentList.length <= 0
                    "
            title="Documenti"
            @on-filter="onFilterDocuments"
            @on-cancel-filter="onCancelFilters"
            @on-search="onSearchDocuments"
        >
          <template #dropdownList>
            <ul class="link-list">
              <li>
                <a class="dropdown-item list-item"
                   @click="onSort('descrizione', 'desc')"
                ><span>Descrizione</span></a
                >
              </li>
              <li>
                <a class="dropdown-item list-item"
                   @click="onSort('dataCreazione', 'desc')"
                ><span>Data più recente</span></a
                >
              </li>
              <li>
                <a class="dropdown-item list-item"
                   @click="onSort('dataCreazione', 'asc')"
                ><span>Data meno recente</span></a
                >
              </li>
              <li>
                <a class="dropdown-item list-item"
                   @click="onSort('dataCreazione', 'desc')"
                ><span>Data di scadenza</span></a
                >
              </li>
            </ul>
          </template>

          <template #filterContent>
            <form id="formDocuments" class="row">
              <input-text
                  id="document-start-date"
                  v-model="documentFilterDate.start"
                  :error="!!startDateError"
                  :error-info="startDateError"
                  :max="maxDate"
                  class="col-12 col-md-6"
                  clearable
                  label-text="Da"
                  type="date"
              />
              <input-text
                  id="document-end-date"
                  v-model="documentFilterDate.end"
                  :error="!!endDateError"
                  :error-info="endDateError"
                  :max="maxDate"
                  class="col-12 col-md-6"
                  clearable
                  label-text="A"
                  type="date"
              />
              <cf-select
                  id="notification-service"
                  v-model="documentScope"
                  :disabled="isLoadingDocumentsScopeList"
                  :option-list="documentScopeList"
                  class="col-12 col-md-6"
                  clearable
                  label-text="Ambito"
              />
            </form>


          </template>
        </filter-search-buttons>
        <template v-if="isFilteringDocuments || isLoadingDocuments">
          <cf-inner-loading showing/>
        </template>

        <template v-else-if="documentList.length > 0">
          <div
              v-for="(document, index) in documentList"
              :key="index"
              class="my-2"
          >
            <document-card :id="index" :document="document"/>
          </div>
          <div v-if="areMoreDocuments" class="text-center pt-4">
            <button
                class="btn btn-outline-primary text-uppercase"
                type="button"
                @click="getMoreDocuments"
            >
              Carica altri risultati
            </button>
          </div>
        </template>

        <div v-else>
          <p class="ps-3 mt-4">
            <template v-if="areFilteredDocuments"
            >Non ci sono documenti in base ai filtri di
              ricerca
            </template
            >

            <template v-else>Non ci sono ancora documenti</template>
          </p>
        </div>
      </section>
    </div>
  </div>
</template>

<script>
import CfPageIndex from "../../components/generic/CfPageIndex.vue";
import FilterSearchButtons from "../../components/personal-area/FilterSearchButtons.vue";
import {isAfter} from "date-fns";
import {format_date} from "../../services/utils";
import {getDocumentScopeList, getDocumentsList} from "../../services/api";
import InputText from "../../components/form/InputText.vue";
import CfInnerLoading from "../../components/generic/CfInnerLoading.vue";
import DocumentCard from "../../components/personal-area/documents/DocumentCard.vue";
import CfSelect from "../../components/form/CfSelect.vue";
import { PERSONAL_AREA_DESKTOP } from '../../router/routes';

const NAV_DOCUMENTS = [
  {
    target: "documenti",
    nome: "Documenti",
  },
];

const LIMIT = 20
export default {
  name: "PageDocumentsList",
  components: {CfSelect, DocumentCard, CfInnerLoading, InputText, FilterSearchButtons, CfPageIndex},
  inject: ["hostname", "utente", "utenteIsLoading", "showDocuments"],
  data() {
    return {
      NAV_DOCUMENTS,
      maxDate: new Date(),
      areFilteredDocuments: false,
      documentList: [],
      documentScope: null,
      documentScopeList: [],
      documentSearchText: null,
      isFilteringDocuments: false,
      startDateError: null,
      endDateError: null,
      documentFilterDate: {
        start: null,
        end: null,
      },
      offset: 0,
      totalDocumentElements: 0,
      isLoadingDocumentsScopeList: false,
      isLoadingDocuments: false,
      sort: null,
      order: null
    }
  },
  computed: {
    userTaxCode() {
      return this.utente?.codFisc;
    },
    areMoreDocuments() {
      return this.totalDocumentElements > this.documentList?.length;
    },
  },
  watch: {
    documentFilterDate: {
      deep: true,
      handler() {
        if (this.areFilteredDocuments) this.formDocumentsValidation();
      },
    },
  },
  created() {
    if(!this.showDocuments){
      this.$router.replace(PERSONAL_AREA_DESKTOP)
      return
    }
    this.maxDate = format_date(new Date(), 'yyyy-MM-dd')
  },
  async mounted() {
    await this.fetchDocuments();
    if (this.documentScopeList?.length === 0) {
      await this.getDocumentScopeList();
    }
    await this.$nextTick(() => {
      this.formDocumentsValidation();
    });
  },
  methods: {

    formDocumentsValidation() {
      this.startDateError = null
      this.endDateError = null

      let start = this.documentFilterDate.start;
      let end = this.documentFilterDate.end;


      const inputStart = document.getElementById('inputTextdocument-start-date')
      if(!inputStart) return
      let validityStartState = inputStart.validity;
      const inputEnd = document.getElementById('inputTextdocument-end-date')
      let validityEndState = inputEnd.validity;

      if (validityStartState.valid && validityEndState.valid) {

        if (!!start && !!end) {

          let startDate = new Date(start);
          let endDate = new Date(end);
          if (isAfter(startDate, endDate)) {

            let msg = "La data iniziale deve essere precedente alla data finale";
            this.startDateError = msg
            this.endDateError = msg
          } else {
            this.areFilteredDocuments = true;
            this.sort = null
            this.order = null
            this.offset = 0;
            this.documentList = []
            this.fetchDocuments();
          }
        } else if (!!start && !end) {
          this.endDateError = "Inserire un periodo valido";
        } else if (!!end && !start) {
          this.startDateError = "Inserire un periodo valido";
        }
      }

    },
    onSort(type, order) {
      this.sort = type
      this.order = order
      this.offset = 0;
      this.documentList = []
      this.fetchDocuments();
    },
    onFilterDocuments() {
      this.formDocumentsValidation();
    },
    onCancelFilters() {
      this.documentScope = null;
      this.documentSearchText = null
      this.documentFilterDate = {start: null, end: null};
      this.sort = null
      this.order = null

      if (this.areFilteredDocuments) {
        this.offset = 0;
        this.fetchDocuments();
      }
    },
    async fetchDocuments() {
      this.isFilteringDocuments = true;
      try {
        let payload = {
          visibile: true,
        };
        if (this.documentScope) {
          payload.idAmbito = this.documentScope;
        }
        if (this.documentSearchText) {
          payload.descrizione = this.documentSearchText;
        }

        if (this.documentFilterDate.start && this.documentFilterDate.end) {
          payload.dtCreazioneDa = format_date(
              this.documentFilterDate.start,
              "dd/MM/yyyy"
          );
          payload.dtCreazioneA = format_date(
              this.documentFilterDate.end,
              "dd/MM/yyyy"
          );
        }

        let params = {
          limit: LIMIT,
          offset: this.offset,
          sort: this.sort ?? "dataCreazione",
          order: this.order ?? "desc",
        };

        let {data} = await getDocumentsList(this.userTaxCode, payload, {
          params,
        });
        const documentList = data?.documenti ?? [];
        this.documentList = [...this.documentList, ...documentList];
        this.totalDocumentElements = data?.totalElements;
      } catch (e) {
        await this.$store.dispatch("setNotificationInfo", {
          body: "Non è stato possibile caricare l'elenco dei documenti",
        });
      }
      this.isFilteringDocuments = false;
    },
    getMoreDocuments() {
      this.offsetDocuments += LIMIT;
      this.fetchDocuments();
    },
    onSearchDocuments(value) {

      if (!value || value?.length === 0) {
        this.documentSearchText = null;
      } else {
        this.areFilteredDocuments = true;
        this.documentSearchText = value;

      }
      this.documentList = [];
      this.fetchDocuments();
    },
    async getDocumentScopeList() {
      this.isLoadingDocumentsScopeList = true;
      try {
        let {data: scopes} = await getDocumentScopeList(this.userTaxCode);
        this.documentScopeList =
            scopes?.ambiti?.map((s) => {
              return {id: s.idAmbito, name: s.denominazione};
            }) ?? [];
      } catch (e) {
        await this.$store.dispatch("setNotificationInfo", {
          body: "Non è stato possibile caricare l'elenco degli ambiti",
        });
      }

      this.isLoadingDocumentsScopeList = false;
    },

  }
}
</script>


