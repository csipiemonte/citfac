<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <CsiFormSection v-if="serviceCard" id="serviceCard" title="Scheda servizio">
    <CsiInput
      required
      v-model="serviceCard.nome"
      label="Nome scheda"
      id="scNome"
      class="mb-3 col-12 col-md-6"
      :aria-invalid="v$.serviceCard.nome.$invalid"
      :error="v$.serviceCard.nome.$error"
      errorMsg="Campo obbligatorio"
    />

    <CsiInput
      required
      v-model="serviceCard.descrizione"
      label="Descrizione"
      id="scDescrizione"
      class="mb-3 col-12 col-md-6"
      :aria-invalid="v$.serviceCard.descrizione.$invalid"
      :error="v$.serviceCard.descrizione.$error"
      errorMsg="Campo obbligatorio"
    />

    <CsiSelect
      required
      option-value="id"
      option-label="name"
      v-model="serviceCard.categoriaServizio"
      label="Categoria"
      id="scCategoria"
      :optionList="categoriesList"
      class="mb-3 col-12 col-md-6"
      more-info="Seleziona categoria"
      :aria-invalid="v$.serviceCard.categoriaServizio.$invalid"
      :error="v$.serviceCard.categoriaServizio.$error"
      errorMsg="Campo obbligatorio"
    />
    <CsiInput
      v-model="serviceCard.linkScheda"
      label="Link scheda"
      id="scLinkScheda"
      class="mb-3 col-12 col-md-6"
    />

    <CsiInput
      v-model="serviceCard.linkServizio"
      label="Link servizio"
      id="scLinkServizio"
      class="mb-3 col-12 col-md-6"
    />

    <CsiInput
      v-model="serviceCard.messaggioManutenzione"
      label="Messaggio di Manutenzione"
      id="scMessaggioManutenzione"
      class="mb-3 col-12 col-md-6"
    />

    <CsiToggle
      label="Servizio Città Facile"
      v-model="serviceCard.servizioCittaFacile"
      class="mb-3"
      id="scServizioCittaFacile"
    />

    <CsiToggle
      label="Servizio Attivo"
      v-model="serviceCard.servizioAttivo"
      class="mb-3"
      id="scServizioAttivo"
    />
  </CsiFormSection>
</template>

<script>
import CsiButton from '../core/CsiButton.vue'
import CsiModal from '../core/CsiModal.vue'
import CsiFormSection from '../core/form/CsiFormSection.vue'
import CsiInput from '../core/form/CsiInput.vue'
import CsiSelect from '../core/form/CsiSelect.vue'

import CsiInnerLoading from '../core/CsiInnerLoading.vue'

import CsiCard from '../core/CsiCard.vue'
import CsiToggle from '../core/form/CsiToggle.vue'

const EMPTY_SERVICE_CARD = {
  nome: null,
  descrizione: null,
  categoriaServizio: null,
  linkScheda: null,
  linkServizio: null,
  servizioCittaFacile: false,
  servizioAttivo: false,
  messaggioManutenzione: null
}
export default {
  components: {
    CsiModal,
    CsiFormSection,
    CsiSelect,
    CsiButton,
    CsiInput,
    CsiInnerLoading,
    CsiCard,
    CsiToggle
  },

  CsiCarde: 'CsiServiceCardForm',
  emits: ['onConfirm'],
  props: {
    defaultServiceCard: Object,
    loading: Boolean
  },
  data() {
    return {
      serviceCard: null,
      categoriesList: []
    }
  },
  created() {
    this.serviceCard = { ...this.defaultServiceCard } || { ...EMPTY_SERVICE_CARD }
  },
  computed: {
    tenantCms() {
      return  this.$store.getters['getTenantCms']
    },

    serviceId() {
      return this.service?.id
    },
    activeServiceCards() {
      let services = this.$store.getters['getServicesList']

      return services.map((service) => service.idServizio)
    },
    serviceCardList() {
      return this.$store.getters['getServiceCardList']
    }
  },
  methods: {}
}
</script>
