<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <main class="main container mt-4">
    <h1 class="csi-h3 mb-4">Nuova richiesta di assistenza</h1>

    <template v-if="isLoading">
      <csi-inner-loading showing></csi-inner-loading>
    </template>

    <template v-else>
      <form @submit.prevent="onSubmit" novalidate>
        <!-- DATI RICHIESTI  -->
        <!-- ------------------------------------------------------------------------------------------ -->

        <csi-form-section
          id="datiUtente"
          title="Dati richiesti"
          class="csi-nav-target mb-4"
          accordion
        >
          <csi-input required v-model="name" readonly label="Nome" id="name" class="mb-3" />

          <csi-input
            required
            v-model="surname"
            readonly
            label="Cognome"
            id="surname"
            class="mb-3"
          />
          <csi-input
            required
            v-model="taxCode"
            readonly
            label="Codice fiscale"
            id="taxCode"
            class="mb-3"
          />
          <csi-input required v-model="email" :readonly="user.mail" label="Email" id="email" class="mb-3" />
          
        </csi-form-section>
        <!--PROBLEMA RISCONTRATO  -->
        <!-- ------------------------------------------------------------------------------------------ -->

        <csi-form-section
          id="problemaRiscontrato"
          title="Problema riscontrato"
          class="csi-nav-target mb-4"
          accordion
        >
          <csi-select
            v-model="typology"
            label="Servizio Applicativo"
            id="asset"
            :optionList="assetsList"
            option-value="codice"
            option-label="nome"
            class="mb-4 col-12"
            more-info="Selezionare il servizio"
          />

          <csi-select
            required

            v-model="typology"
            label="Tipologia"
            id="typology"
            :optionList="typologyOptions"
            emit-object
            class="mb-4 col-12"
            :aria-invalid="v$.typology.$invalid"
            :error="v$.typology.$error"
            errorMsg="Campo obbligatorio"
            more-info="Selezionare la tipologia"
          />

          <csi-textarea
            required
            v-model="description"
            label="Descrizione della richiesta"
            id="description"
            class="mb-3"
            :error="v$.description.$error"
            errorMsg="Campo obbligatorio"
            :aria-invalid="v$.description.$invalid"
          />

          <csi-input-file label="Carica documento" id="attachment" class="mb-3" max-size="2048"/>
        </csi-form-section>
      </form>
    </template>
  </main>
</template>

<script>
import CsiInnerLoading from '../../components/core/CsiInnerLoading.vue'
import CsiFormSection from '../../components/core/form/CsiFormSection.vue'
import CsiInput from '../../components/core/form/CsiInput.vue'
import { useVuelidate } from '@vuelidate/core'
import { required, requiredIf } from '@vuelidate/validators'
import CsiSelect from '../../components/core/form/CsiSelect.vue'
import CsiTextarea from '../../components/core/form/CsiTextarea.vue'
import CsiInputFile from '../../components/core/form/CsiInputFile.vue'
export default {
  name: 'PageAssistanceForm',
  components: { CsiFormSection, CsiInnerLoading, CsiInput, CsiSelect, CsiTextarea, CsiInputFile },

  data() {
    return {
      v$: useVuelidate(),
      isLoading: false,
      name: null,
      surname: null,
      taxCode: null,
      email: null,
      telephone: null,
      typology: null,
      typologyOptions: [],
      description: null,
      attachment: null
    }
  },
  validations() {
    return {
      name: { required },
      surname: { required },
      taxCode: { required },
      email: { required },
      typology: { required },
      description: { required }
    }
  },
  async created() {
    if (this.user) {
      this.name = this.user.nome
      this.surname = this.user.cognome
      this.taxCode = this.user.codFisc
      this.email = this.user.mail
    }
  },
  computed: {
    user() {
      return this.$store.getters['getUser']
    },
    getComuneConfig() {
      return this.$store.getters['getComuniConfig']
    },
    getComuneUrl() {
      return this.getComuneConfig?.homePage ?? ''
    },
    assetsList() {
      return this.$store.getters['getAssets']
    }
  },
  methods: {
    onSubmit() {},
    uploadFile(file) {
      this.attachment = file
    }
  }
}
</script>

<style scoped></style>
