<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="cf-card  cf-card-accordion mb-3">
    <div class="card shadow px-4 pt-4 pb-4">
      <div class="card-header border-0 p-0 m-0">
        <div class="cf-card__date" style="margin-left: 44px">
          {{ startDate }}
        </div>
      </div>
      <div class="card-body p-0 my-2">
        <div class="row">
          <div class="col-auto position-relative">

            <svg aria-hidden="true" class="icon icon-secondary icon-md ">
              <use :href="icon"></use>
            </svg>
          </div>
          <div class="accordion col">
            <div class="accordion-item no-border">
              <div class="accordion-header pe-2">

                <button
                    :id="currentId"
                    :aria-controls="currentId"
                    :aria-expanded="false"
                    class="accordion-button collapsed title-small-semi-bold  "
                    style="border-style: none !important"
                    type="button"
                    @click="useToggleAccordion(currentTarget, currentId)"

                >

                  <div class="button-wrapper">

                    {{ documentDescription }}

                  </div>
                </button>


                <div class="fw-normal">
                 Ambito: {{ documentScope }}
                </div>

              </div>

              <div
                  :id="currentTarget"
                  :aria-labelledby="currentId"
                  class="accordion-collapse collapse p-0 m-0  cf-accordion"
                  role="region"
              >
                <div class="accordion-body mt-2 row">

                  <p class="text-paragraph mb-1"><strong>Tipologia: </strong> {{ documentType }} <span
                      v-if="documentTypeExtension">(.{{ documentTypeExtension }})</span></p>

                  <p class="text-paragraph mb-1"><strong>Scadenza: </strong> {{ expiringDate }} </p>
                  <div class="mt-3 ">
                    <button aria-label="Scarica documento" class="btn btn-outline-primary px-5" style="width:auto"
                            type="button" @click="onDownload">SCARICA <span v-if="isDownloading" class="ms-2">
                         <cf-inner-loading :showing="isDownloading" small/>
                      </span>
                    </button>
                  </div>
                </div>

              </div>
            </div>
          </div>

        </div>

      </div>


    </div>
  </div>


</template>

<script>
import {useDate} from "../../../composables/date";
import {useToggleAccordion} from "../../../composables/toggleAccordion";
import {getDocumentDetail} from "../../../services/api";
import CfInnerLoading from "../../generic/CfInnerLoading.vue";
import {format_date} from "../../../services/utils";


export default {
  name: "DocumentCard",
  components: {CfInnerLoading},

  props: {
    document: Object,
    id: [String, Number],
  },
  data() {
    return {
      isExpanded: false,
      isDownloading: false
    }
  },
  computed: {
    startDate() {
      let date = this.document?.dtaCreazione
      return format_date(this.document?.dtaCreazione)
    },
    expiringDate() {
      return format_date(this.document?.dtaFine)
    },
    documentDescription() {
      return this.document?.descrizione ?? '-'
    },
    currentId() {
      return "documentItem" + this.id;
    },
    currentTarget() {
      return this.currentId + 'Target'
    },
    documentScope() {
      return this.document?.ambito?.denominazione ?? '-'
    },
    documentType() {
      return this.document?.tipologia?.descrizione ?? '-'
    },
    documentTypeExtension() {
      return this.document?.tipologia?.estensione
    },
    documentContent() {

    },

    icon() {
      let icon = "it-file"
      if (this.documentTypeExtension === 'pdf') {
        icon += "-pdf-ext"
      }
        return  this.$svgs.getSpriteSvgUrl(icon)
   

    }
  },
  methods: {
    useToggleAccordion,
    async onDownload() {
      this.isDownloading = true
      try {
        let cf = this.document.cfCittadino
        let uuid = this.document.uuid
        let params = {download: true}
        let response = await getDocumentDetail(cf, uuid, {params})
        const document = response?.data?.documento
        await this.openDocument(document)
      } catch (e) {
        await this.$store.dispatch("setNotificationInfo", {
          body: "Non è stato possibile scaricare il documento",
        });
      }
      this.isDownloading = false
    },
    openDocument(doc) {
      const link = document.createElement('a');
      const filename = doc?.filename
      const file = doc?.content
      const fileType = doc?.tipologia?.mimetype
      const url = `data:${fileType};base64, ${file}`
      link.target = '_blank';
      link.href = url;
      link.setAttribute('download', filename);

      // Append to html link element page
      document.body.appendChild(link);

      // Start download
      link.click();

      // Clean up and remove the link
      link.parentNode.removeChild(link);
      URL.revokeObjectURL(url);
    }
  },

}
</script>

