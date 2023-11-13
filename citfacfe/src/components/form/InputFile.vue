<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="form-group">
    <label>{{ labelText }}</label>
  </div>
  <div class="">
    <input
      type="file"
      :name="currentId"
      :id="currentId"
      class="upload form-control"
      @input="input"
      @change="onChange"
      :accept="accepted"
    />
    <label style="width: 100%" class="text-center" :for="currentId">
      <svg class="icon icon-sm" aria-hidden="true">
        <use 
        :xlink:href="$svgs.getSpriteSvgUrl('it-upload')"></use>
      </svg>
      <span class="col">Carica documento</span>
    </label>

    <small
      class="form-text text-200"
      v-for="(hint, index) in hints"
      :key="index"
    >
      {{ hint }} <br
    /></small>

    <small v-if="error" class="small text-danger">{{ errorInfo }}</small>

    <div v-if="fileName">
      <ul class="upload-file-list">
        <li class="upload-file success">
          <p>
            File "<a
              v-if="downloadable"
              :href="downloadUrl"
              :alt="`download file ${fileName}`"
              target="_blank"
              :download="fileName"
            >
              {{ fileName }}
            </a>

            <span v-else>{{ fileName }}</span
            >" caricato correttamente
          </p>
          <button>
            <span class="visually-hidden"
              >Elimina file caricato {{ fileName }}</span
            >
            <svg @click="removeFile" class="icon" aria-hidden="true">
              <use
              :xlink:href="$svgs.getSpriteSvgUrl('it-close')"
              ></use>
            </svg>
          </button>
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
import { useDownloadBlob } from "../../composables/downloadBlob";

const EXTENSIONS_DEFAULT = [
  ".doc",
  ".docx",
  ".xls",
  ".xlsx",
  ".pdf",
  ".jpg",
  ".png",
];

export default {
  // COMPONENTE PER UPDATE FILE
  // PER UTILIZZARE IL COMPONENTE USARE I PROPS
  //labelText" TESTO DELLA LABEL DEL CAMPO INSERIMENTO,
  //id" UN ID UNIVOCO PER IL COMPONENTE,
  //moreInfo" EVENTUALE INFO AGGIUNTIVA DEL CAMPO,
  //@updateFile" EMETTE IL VALORE AGGIORNATO DEL CAMPO, UN OGGETTO FILE
  name: "InputFile",
  emits: ["update-file", "valid-size"],
  props: {
    labelText: String,
    id: String,
    moreInfo: { type: String },
    prefill: Object,
    extensions: {
      type: Array,
      default: () => [".doc", ".docx", ".xls", ".xlsx", ".pdf", ".jpg", ".png"],
    },
    maxSize: String, //KB
    error: Boolean,
    errorInfo: String,
    downloadable: Boolean,
  },

  data() {
    return {
      fileForm: null,
      currentId: null,
      fileName: null,
      base64: null,
      mimeType: null,
    };
  },
  watch: {
    //SE PREFILL CAMBIA PERCHè è BOZZA, VA AGGIORNATO IL VALORE
    prefill: {
      immediate: true,
      deep: true,
      handler(val) {
        if (val) {
          this.fileName = val.filename;
          this.base64 = val.data;
          this.mimeType = val["mime-type"];
        }
      },
    },
  },
  computed: {
    hints() {
      let hint = [];

      if (this.moreInfo) {
        hint.push(this.moreInfo);
      }

      if (this.extensions.length > 0) {
        let extensions =
          this.extensions.slice(0, -1).join(", ") +
          " o " +
          this.extensions.slice(-1);
        hint.push(`Caricare un file in formato ${extensions}`);
      }

      if (this.maxSize) {
        hint.push(`Dimensione massima file ${this.maxSize} KB`);
      }

      return hint;
    },
    accepted() {
      return this.extensions.join(", ");
    },
    downloadUrl() {
      if (!this.prefill) {
        return "";
      } else {
        return `data:${this.mimeType};base64,${this.base64}`;
      }
    },
  },
  mounted() {
    this.fileForm = document.getElementById(this.currentId);
  },

  async created() {
    this.currentId = "inputFile" + this.id;
  },

  methods: {

    removeFile() {
      this.fileName = null;
      this.$emit("update-file", null);
    },
    input() {
      let file = this.fileForm.files[0];
      let isValid = this.isValidSize(file);
      if (isValid) {
        this.fileName = file?.name;
        this.$emit("update-file", file);
        this.$emit("valid-size", true);
      } else {
        this.fileName = null;
        this.$emit("update-file", null);
        this.$emit("valid-size", false);
      }
    },
    isValidSize(file) {
      if (!file || !this.maxSize) {
        return true;
      }

      const filesize = file.size;
      const maxSize = this.maxSize * 1024;

      return filesize <= maxSize;
    },

    downloadFile() {
      let blob = this.file.data;

      let filename = this.file.filename;
      return useDownloadBlob(blob, filename);
    },
  },
};
</script>
