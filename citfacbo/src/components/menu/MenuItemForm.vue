<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <csi-modal
    noAction
    id="menu-item-form"
    :modelValue="modelValue"
    @onHide="onHide"
    :title="titleModal"
  >
    <form @submit.prevent="onSubmit" novalidate>
      <csi-alert ref="validationAlert" type="error" v-show="v$.$error"
        >É necessario compilare correttamente i campi obbligatori
      </csi-alert>

      <csi-input
        required
        v-model="title"
        label="Titolo"
        id="title"
        class="mb-3 col-12"
        :aria-invalid="v$.title.$invalid"
        :error="v$.title.$error"
        errorMsg="Campo obbligatorio"
      />

      <csi-input
        required
        v-model="url"
        label="Url"
        id="url"
        class="mb-3 col-12"
        :aria-invalid="v$.url.$invalid"
        :error="v$.url.$error"
        errorMsg="Campo obbligatorio"
      />

      <csi-input v-model="dataElement" label="Data element" id="dataElement" class="mb-3 col-12" />

      <div class="m-0 row justify-content-end pt-3 gap-3">
        <csi-button outline class="col-auto" @click="onHide">Annulla</csi-button>
        <csi-button type="submit" class="col-auto">Conferma</csi-button>
      </div>
    </form>
  </csi-modal>
</template>

<script>
import CsiAlert from '../core/CsiAlert.vue'
import CsiFormSection from '../core/form/CsiFormSection.vue'
import { useVuelidate } from '@vuelidate/core'
import { required } from '@vuelidate/validators'
import CsiButton from '../core/CsiButton.vue'
import CsiModal from '../core/CsiModal.vue'
import CsiInput from '../core/form/CsiInput.vue'
export default {
  components: { CsiFormSection, CsiAlert, CsiButton, CsiModal, CsiInput },
  name: 'MenuItemForm',
  emits: ['onEdit', 'onCreate', 'update:modelValue'],
  props: {
    modelValue: Boolean,
    menuItem: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      v$: useVuelidate(),
      dataElement: null,
      title: null,
      url: null
    }
  },
  validations() {
    return {
      title: { required },
      url: { required }
    }
  },
  watch: {
    modelValue: {
      immediate: true,
      handler(val) {
        if (val && this.menuItem) {
          this.title = this.menuItem.title
          this.url = this.menuItem.url
          this.dataElement = this.menuItem?.atributes
            ? this.menuItem?.atributes['data-element']
            : null
        }
      }
    }
  },

  computed: {
    titleModal() {
      return !!this.menuItem ? 'Modifica voce' : 'Nuova voce'
    }
  },

  created() {},
  methods: {
    onHide() {
      this.$emit('update:modelValue', false)
    },

    onSubmit() {
  
      this.v$.$touch()

      if (this.v$.$error) {
        setTimeout(() => {
          this.$refs.validationAlert.$el.scrollIntoView({ block: 'start' })
        }, 300)

        return
      }

      let newMenuItem = {
        title: this.title,
        url: this.url
      }
      if (this.dataElement?.length > 0) {
        newMenuItem.atributes = newMenuItem.atributes || {}
        newMenuItem.atributes['data-element'] = this.dataElement
      }

      if (!!this.menuItem) {
        this.$emit('onEdit', newMenuItem)
      } else {
        this.$emit('onCreate', newMenuItem)
      }

      this.onHide()
    }
  }
}
</script>
