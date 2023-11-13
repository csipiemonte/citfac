<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="csi-input-file" :class="customClasses">
    <label :for="currentId" class="form-label">
      <csi-icon class="me-2" :src="$svgs.getBootstrapIconsUrl('upload')"></csi-icon>

      {{ label }}<sup v-if="required" aria-hidden="true">*</sup></label
    >
    <div class="input-group">
      <div v-if="prepend" class="input-group-prepend">
        <div class="input-group-text">
          <slot name="prepend"></slot>
        </div>
      </div>

      <input
        type="file"
        :id="currentId"
        :aria-label="ariaLabel"
        :class="{ 'text-uppercase': isUppercase }"
        :required="required"
        :name="currentId"
        class="form-control upload"
        @focusin="activateLabel"
        @focusout="activateLabel"
        @input="input"
        v-bind="$attrs"
        :aria-describedby="`${hintId} ${errorId}`"
        :aria-invalid="ariaInvalid"
        :accept="accepted"
      />

      <div v-if="(append || isClearable) && isLabelActive" class="input-group-append">
        <div class="input-group-text">
          <slot name="append" />
          <a v-if="isClearable" alt="azzera campo" @click="onClearInput"> </a>
        </div>
      </div>
    </div>
    <template v-if="hints">
      <small
        v-for="(hint, index) in hints"
        :key="index"
        :id="`hint-${id}-${index}`"
        class="form-text small"
        >{{ hint }}<br
      /></small>
    </template>

    <small v-if="error" :id="errorId" class="small text-danger">{{ errorMsg }}</small>

    <div v-if="fileName" class="w-100">
      <ul class="upload-file-list">
        <li class="upload-file success">
          <p >
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
          <button >
            <span class="visually-hidden">Elimina file caricato {{ fileName }}</span>
            <svg @click="removeFile" class="icon" aria-hidden="true">
              <use :xlink:href="$svgs.getSpriteSvgUrl('it-close')"></use>
            </svg>
          </button>
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
import { useDownloadBlob } from '../../../services/business-logic'
import CsiIcon from '../CsiIcon.vue'
// COMPONENTE PER INPUT TESTUALE LIBERO
// PER UTILIZZARE IL COMPONENTE USARE I PROPS
//"modelValue" valore dell'input
//label" TESTO DELLA LABEL DEL CAMPO INSERIMENTO,
//id" UN ID UNIVOCO PER IL COMPONENTE,
//moreInfo" EVENTUALE INFO AGGIUNTIVA DEL CAMPO,
//errorInfo" EVENTUALE MESSAGGIO DI ERRORE  DEL CAMPO,
//"error" TRUE SE IL VALORE INSERITO E' ERRATO
//isUppercase" SE TRUE IL TESTO è MAIUSCOLO,
//prefill" SE PRESENTE PRCOMPILA IL CAMPO
//"type" TIPO DI INPUT
//"maxlength" LUNGHEZZA MASSIMA
//"minlength" LUNGHEZZA MINIMA
//"prepend" ELEMENTO INSERITO PRIMA DELL'INPUT
//"append" ELEMENTO INSERITO DOPO DELL'INPUT
//"step" STEP dell'input
export default {
  components: { CsiIcon },
  name: 'CsiInputFile',
  props: {
    prefill: Object,
    label: String,
    id: String,
    moreInfo: String,
    error: Boolean,
    errorMsg: String,
    isUppercase: Boolean,
    required: Boolean,
    containerClasses: Array,
    prepend: Boolean,
    append: Boolean,
    clearable: Boolean,
    ariaInvalid: Boolean,
    extensions: {
      type: Array,
      default: () => ['.doc', '.docx', '.xls', '.xlsx', '.pdf', '.jpg', '.png']
    },
    maxSize: String //KB
  },
  emits: ['input', 'update-file', 'valid-size'],
  data() {
    return {
      text: null,
      currentId: null,
      isLabelActive: true,
      file: null,
      currentId: null,
      fileName: null,
      base64: null,
      mimeType: null
    }
  },
  async created() {
    this.currentId = 'inputText' + this.id
  },
  computed: {
    customClasses() {
      let classes = this.containerClasses ?? []

      if (this.error) {
        classes.push('invalid')
      }
      return classes
    },

    ariaLabel() {
      return `${this.label} ${this.modelValue ?? ''} ${this.moreInfo ?? ''}`
    },
    isClearable() {
      return !!this.modelValue && this.modelValue !== '' && this.clearable
    },
    attrs() {
      const { ...attrs } = this.$attrs
      return attrs
    },
    hintId() {
      return `hint-${this.id}`
    },
    errorId() {
      return `error-${this.id}`
    },
    accepted() {
      return this.extensions?.join(', ')
    },
    hints() {
      let hint = []

      if (this.moreInfo) {
        hint.push(this.moreInfo)
      }

      if (this.extensions.length > 0) {
        let extensions = this.extensions.slice(0, -1).join(', ') + ' o ' + this.extensions.slice(-1)
        hint.push(`Caricare un file in formato ${extensions}`)
      }

      if (this.maxSize) {
        hint.push(`Dimensione massima file ${this.maxSize} KB`)
      }

      return hint
    }
  },
  mounted() {},
  watch: {
    prefill: {
      immediate: true,
      handler(val) {
        if (val) {
          this.file = val
          this.fileName = val.filename
          this.base64 = val.data
          this.mimeType = val['mime-type']

          this.activateLabel()
        }
      }
    }
  },
  methods: {
    //POSIZIONA LA LABEL PER PERMETTERE LA SCRITTURA
    activateLabel() {
      if (!!this.text || this.type === 'date') {
        this.isLabelActive = true
      } else {
        this.isLabelActive = !this.isLabelActive
      }
    },
    input(event) {
      let files = event.target.files
      this.file = files ? files[0] : null

   
      let isValid = this.isValidSize(this.file)
      if (isValid) {
       
        this.fileName = this.file?.name
        this.$emit('update-file', this.file)
        this.$emit('input', this.file)
        this.$emit('valid-size', true)
      } else {
        this.fileName = null
        this.$emit('update-file', null)
        this.$emit('input', this.file)
        this.$emit('valid-size', false)
      }
    },
    onClearInput() {
      this.$emit('update-file', null)
      this.$emit('input', null)
    },
    removeFile() {
      this.fileName = null
      this.$emit('update-file', null)
    },

    isValidSize(file) {
      if (!file || !this.maxSize) {
        return true
      }

      const filesize = file.size
      const maxSize = this.maxSize * 1024

      return filesize <= maxSize
    },

    downloadFile() {
      let blob = this.file.data

      let filename = this.file.filename
      return useDownloadBlob(blob, filename)
    }
  }
}
</script>
<style lang="scss">
.csi-input-file {
  .upload {
    width: 0.1px;
    height: 0.1px;
    opacity: 0;
    overflow: hidden;
    position: absolute;
    z-index: -1;
  }
  label {
    text-align: center;
    font-size: 1rem;
    font-weight: 700;
    cursor: pointer;
    display: inline-block;
    overflow: hidden;
    padding: 7px 24px;
    border-radius: 4px;
    color: #fff;
    background-color: var(--bs-primary);
    transition: background-color 0.15s;
    .icon {
      width: 24px;
      height: 24px;
      fill: #fff;
    }
  }
  .upload-file-list {
    padding: 0;
    .upload-file {
      list-style-type: none;
      display: flex;
      flex-direction: row;
      align-items: center;
      max-width: 375px;
      margin-bottom: 16px;
      position: relative;
      justify-content: space-between;
      width: 100%;
      p {
        flex: 1 0 0%;
        margin: 0;
        font-size: 0.889rem;
        font-weight: 600;
        text-overflow: ellipsis;
        white-space: nowrap;
      
        overflow: hidden;
      }
      button {
        background: transparent;
        border: 0;
        padding: 0px;
       
        flex: 0 0 auto;
        width: auto;
        svg {
          flex-shrink: 0;
          width: 32px;
          height: 32px;
        }
      }
    }
  }
}
</style>
