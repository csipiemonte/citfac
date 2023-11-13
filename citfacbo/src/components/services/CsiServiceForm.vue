<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="csi-service-form">
    <form @submit.prevent="onSubmit" novalidate>
      <csi-alert ref="validationAlert" type="error" v-show="v$.$error"
        >É necessario compilare correttamente i campi obbligatori
      </csi-alert>

      <csi-form-section id="serviceCard" title="Scheda servizio">
        <csi-toggle label="Servizio di tipo asset" v-model="isAsset" class="mb-5" id="scIsAsset" />
        <transition name="slide">
          <csi-select
            v-if="isAsset"
            required
            option-value="codice"
            option-label="nome"
            v-model="serviceAsset"
            label="Seleziona l'asset"
            id="scIdAsset"
            :optionList="assetsOptions"
            emit-object
            class="mb-4 col-12"
            :aria-invalid="v$.service.asset.$invalid"
            :error="v$.service.asset.$error"
            errorMsg="Campo obbligatorio"
            @on-select="onSelectAsset"
          />
        </transition>

        <csi-select
          v-if="tenantCms"
          option-value="id"
          option-label="nome"
          v-model="service.idServizio"
          label="Scheda servizio"
          id="descriptionCard"
          :optionList="serviceCards"
          class="mb-4"
          more-info="Seleziona scheda esistente"
          @on-select="onSelectService"
        />

        <csi-input
          required
          v-model="service.titolo"
          label="Nome servizio"
          id="title"
          class="mb-4 col-12"
          :aria-invalid="v$.service.titolo.$invalid"
          :error="v$.service.titolo.$error"
          errorMsg="Campo obbligatorio"
          @blur="setServiceName"
        />

        <template v-if="!tenantCms">
          <csi-input
            required
            v-model="serviceCard.descrizione"
            label="Descrizione"
            id="scDescrizione"
            class="col-12"
            :container-classes="['mb-4']"
            more-info="Inserire una descrizione breve del servizio"
            :aria-invalid="v$.serviceCard.descrizione.$invalid"
            :error="v$.serviceCard.descrizione.$error"
            errorMsg="Campo obbligatorio"
          />

          <csi-select
            required
            option-value="id"
            option-label="nome"
            v-model="serviceCardCategory"
            label="Categoria"
            id="scCategoria"
            :optionList="serviceCategories"
            emit-object
            @on-select="onSelectCategory"
            class="mb-4 col-12"
            more-info="Seleziona categoria"
            :aria-invalid="v$.serviceCard.categoriaServizio.$invalid"
            :error="v$.serviceCard.categoriaServizio.$error"
            errorMsg="Campo obbligatorio"
          />

          <csi-input
            v-model="serviceCard.linkScheda"
            label="Url scheda servizio"
            id="scLinkScheda"
            class="col-12"
            :container-classes="['mb-4']"
            more-info="Esempio: https://esempio.it"
          />

          <csi-input
            v-model="serviceCard.linkServizio"
            label="Url servizio"
            id="scLinkServizio"
            class="col-12"
            :container-classes="['mb-4']"
            more-info="Esempio: https://esempio.it"
          />

          <csi-radio
            boolean-options
            label="Stato servizio"
            id="scServizioAttivo"
            v-model="serviceCard.servizioAttivo"
            name="scServizioAttivo"
            class="mt-2 mb-5 col-12"
            :options="ACTIVE_SERVICE_OPTIONS"
          ></csi-radio>

          <csi-input
            v-model="serviceCard.messaggioManutenzione"
            label="Messaggio di Manutenzione"
            id="scMessaggioManutenzione"
            class="mb-4 col-12"
          />
        </template>

        <template v-if="!isAsset">
          <csi-radio
          v-if="!tenantCms"
          boolean-options
          label="È un servizio Città facile?"
          id="scServizioCittaFacile"
          v-model="serviceCard.servizioCittaFacile"
          name="scServizioCittaFacile"
          class="mt-2 mb-5 col-12"
        ></csi-radio>

        <transition name="slide">
          <div v-if="isVisibleMoon" class="mb-4 col-12">
            <csi-select
              option-value="codiceModulo"
              option-label="descrizioneModuloNew"
              v-model="service.moduloMoon"
              label="Modulo Moon"
              id="moonModule"
              :optionList="moonModules"
              class="mb-1"
            />
            <template v-if="urlMoon">
              <a :href="urlMoon"  class="small" target="_blank"
              >Vai a Moon per creare un nuovo modulo</a
            >
            </template>
           
          </div>

   
        </transition>
      </template>
  

        <csi-select
          option-value="id"
          option-label="name"
          v-model="service.codiceAssistenza"
          label="Assistenza CRM"
          id="assistanceCrm"
          :optionList="assistanceServices"
          class="mb-4 col-12"
          more-info="Seleziona servizio"
        />

        <csi-select
          option-value="id"
          option-label="name"
          v-model="service.idSportello"
          label="Sportello Appuntamenti"
          id="appointmentOffice"
          :optionList="apppointmentOffices"
          class="mb-4 col-12"
          more-info="Seleziona sportello"
        />

        <csi-select
          option-value="id"
          option-label="name"
          v-model="service.codiceNotifiche"
          label="Notificatore"
          id="notification"
          :optionList="notifyServices"
          class="mb-4 col-12"
          more-info="Seleziona servizio"
        />

        <div class="my-3">
          <div class="pb-2">
            <small><strong>FAQ CRM</strong></small>
          </div>
          <div class="row justify-content-between align-items-center px-3">
            <template v-if="faqBreadcrumb.length > 0">
              <ol class="breadcrumb col m-0" style="--bs-breadcrumb-divider: '|'">
                <li class="breadcrumb-item" v-for="(faq, index) in faqBreadcrumb" :key="faq.id">
                  <template v-if="index === faqBreadcrumb.length - 1">
                    <strong class="text-secondary">
                      {{ faq.name }}
                    </strong>
                  </template>
                  <template v-else>
                    {{ faq.name }}
                  </template>
                </li>
              </ol>
            </template>

            <template v-else>
              <div class="form-text small col p-0">Aggiungi FAQ</div>
            </template>

            <csi-button
              type="button"
              no-label
              outline
              class="text-right col-auto p-1 mx-2"
              data-bs-toggle="tooltip"
              data-bs-placement="top"
              data-bs-title="This top tooltip is themed via CSS variables."
              @click.prevent="onChangeFaq"
              aria-label="modifica"
              title="modifica"
              :icon="$svgs.getBootstrapIconsUrl('pencil')"
              no-min-width
            >
            </csi-button>

            <csi-button
              type="button"
              no-label
              color="danger"
              outline
              class="text-right col-auto p-1"
              @click.prevent="onCancelFaq"
              aria-label="elimina"
              title="elimina"
              :icon="$svgs.getBootstrapIconsUrl('trash')"
              no-min-width
            >
            </csi-button>
          </div>
        </div>
      </csi-form-section>

      <div class="m-0 row justify-content-end pt-3">
        <csi-button type="submit" :loading="loading" class="col-auto">Salva</csi-button>
      </div>
    </form>
    <!-- MODIFICA FAQ -->
    <csi-faqs-modal
      :loading="isLoadingFaqCategories || isLoadingFaqs"
      v-model="isOpenFaqModal"
      @onConfirmFaq="onConfirmFaq"
      :faqOptions="faqOptions"
    ></csi-faqs-modal>
    <!-- CANCELLA FAQ -->
    <csi-modal
      id="faqDeleteModal"
      v-model="isOpenFaqDeleteModal"
      title="Cancella FAQ CRM"
      @onConfirm="onDeleteFaq"
    >
      <p>
        <strong>Attenzione</strong><br />
        Vuoi cancellare le FAQ assiciate al servizio <strong>{{ service.titolo }}</strong> ?
      </p>
    </csi-modal>
    <!-- CONFERMA MODIFICHE -->
    <csi-modal
      id="serviceModal"
      v-model="isOpenConfirmModal"
      title="Modifica servizio"
      @onConfirm="onConfirm"
    >
      <p>
        <strong>Attenzione</strong><br />
        <template v-if="serviceId">
          Stai per modificare la configurazione del servizio <strong>{{ serviceTitle }}</strong>
        </template>

        <template v-else>
          Stai per creare il servizio <strong>{{ service.titolo }}</strong>
        </template>
      </p>
    </csi-modal>
  </div>
</template>

<script>
import CsiButton from '../core/CsiButton.vue'
import CsiModal from '../core/CsiModal.vue'
import CsiFormSection from '../core/form/CsiFormSection.vue'
import CsiInput from '../core/form/CsiInput.vue'
import CsiSelect from '../core/form/CsiSelect.vue'
import { equals, onShowNotificationToast } from '../../services/business-logic'
import { getFaqDetail, getFaqTypeList } from '../../services/api'
import CsiInnerLoading from '../core/CsiInnerLoading.vue'
import CsiFaqTree from './CsiFaqTree.vue'
import CsiFaqsModal from './CsiFaqsModal.vue'
import CsiCard from '../core/CsiCard.vue'
import FaqBreadcrumbs from './FaqBreadcrumbs.vue'
import CsiAlert from '../core/CsiAlert.vue'
import { useVuelidate } from '@vuelidate/core'
import { required, requiredIf } from '@vuelidate/validators'
import CsiToggle from '../core/form/CsiToggle.vue'
import CsiIcon from '../core/CsiIcon.vue'
import CsiRadio from '../core/form/CsiRadio.vue'
import { SERVICES_ASSETS_LIST } from '../../services/config'

const EMPTY_SERVICE = {
  titolo: null,
  idServizio: null,
  moduloMoon: null,
  codiceNotifiche: null,
  codiceAssistenza: null,
  idFaq: null,
  idBot: null,
  asset: null,
  idSportello:null
}

const EMPTY_SERVICE_CARD = {
  nome: null,
  descrizione: null,
  categoriaServizio: '',
  linkScheda: null,

  servizioCittaFacile: false,
  servizioAttivo: false,
  messaggioManutenzione: null
}

export const ACTIVE_SERVICE_OPTIONS = [
  { label: 'Attivo', value: true },
  { label: 'Bozza', value: false }
]

export default {
  components: {
    CsiModal,
    CsiFormSection,
    CsiSelect,
    CsiButton,
    CsiInput,
    CsiInnerLoading,
    CsiFaqTree,
    CsiFaqsModal,
    CsiCard,
    FaqBreadcrumbs,
    CsiAlert,
    CsiToggle,
    CsiIcon,
    CsiRadio
  },

  name: 'CsiServiceForm',
  emits: ['onConfirm'],
  props: {
    defaultService: Object,
    loading: Boolean
  },
  data() {
    return {
      ACTIVE_SERVICE_OPTIONS,
      v$: useVuelidate(),
      service: null,
      serviceCard: null,
      selectedFaq: [],
      faqBreadcrumb: [],
      faqOptions: [],
      isLoadingFaqs: false,
      isLoadingFaqCategories: false,
      isOpenConfirmModal: false,
      isOpenFaqModal: false,
      isOpenFaqDeleteModal: false,
      serviceCardCategory: null,
      serviceAsset: null,
      isAsset: false,
      isActive: null
    }
  },
  validations() {
    let validations = {
      service: {
        titolo: { required },
        asset: { requiredIf: this.isAsset }
      }
    }
    if (!this.tenantCms) {
      validations['serviceCard'] = {
        nome: { required },
        descrizione: { required },
        categoriaServizio: { required }
      }
    }

    return validations
  },
  created() {
    this.service = { ...this.defaultService } || { ...EMPTY_SERVICE }
    this.serviceCard = { ...EMPTY_SERVICE_CARD }

    if (!this.tenantCms && this.service?.idServizio) {
      this.serviceCard = this.serviceCardList?.find(
        (s) => s.id.toString() === this.service.idServizio.toString()
      )
      this.serviceCardCategory = this.serviceCard['categoriaServizio']?.id
    }

    if (this.service.asset) {
      this.isAsset = true
      this.serviceAsset = this.service.asset?.codice
    }
  },
  async mounted() {
    if (this.serviceId) {
      if (this.service.idFaq) {
        this.isLoadingFaqs = true
        try {
          let { data: faq } = await getFaqDetail(this.service.idFaq)
          this.faqBreadcrumb = faq.breadcrumb
        } catch (error) {}
        this.isLoadingFaqs = false
      }
    }
  },
  computed: {
    urlMoon() {
      let assets = this.$store.getters['getAssets']
      let moonAsset = assets.find((a) => a.codice === 'MOON')
      return moonAsset?.urlBackoffice
    },
    tenantCms() {
      return this.tenant?.cms
    },
    tenant() {
      return this.$store.getters['getTenant']
    },
    seletedAssets() {
      const services = this.$store.getters['getServicesList']
      let filteredServices = services.filter((s) => !!s.asset)
      return filteredServices.map((s) => s.asset?.codice)
    },
    assetsOptions() {
      let assets = this.tenant.assets ?? []

      let filteredAssets = assets.filter((a) => SERVICES_ASSETS_LIST.includes(a.codice))

      //controllo se sono già selezionati da altri servizi

      let servicesWithAsset = filteredAssets.map((a) => {
        if (this.seletedAssets.includes(a.codice) && this.service.asset?.codice !== a.codice) {
          a.disabled = true
        }
        return a
      })

      return servicesWithAsset
    },

    isVisibleMoon() {
      return this.serviceCard?.servizioCittaFacile || this.tenantCms
    },
    formTitle() {
      return !this.tenantCms ? 'Dettaglio servizio' : null
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
    },
    serviceCards() {
      //solo  selezionabili (scarta quelle già selezionate per altri)

      return this.serviceCardList.map((card) => {
        card.disabled = this.activeServiceCards.includes(card.id)
        return card
      })
    },
    assistanceServices() {
      return this.$store.getters['getAssistanceServiceList']
    },
    moonModules() {
      let modules = this.$store.getters['getMoonModulesList']
      return modules.map((m) => {
        let status = m.stato?.descrizione ?? '-'
        m.descrizioneModuloNew = `${m.descrizioneModulo} (${status})`
        return m
      })
    },
    notifyServices() {
      return this.$store.getters['getNotifyServiceList']
    },
    faqCategories() {
      return this.$store.getters['getFaqCategoriesList']
    },
    serviceCategories() {
      return this.$store.getters['getServiceCategoriesList']
    },
    apppointmentOffices(){
      return this.$store.getters['getAppointmentOfficesList']
    }
  },
  methods: {
    async getFaq(value) {
      this.isLoadingFaqs = true

      try {
        let { data: category } = await getFaqDetail(value)
        if (category?.children?.length > 0) {
          this.faqOptions.push(category.children)
        }
      } catch (error) {
        await onShowNotificationToast({
          body: 'Non è stato possibile recuperare la lista delle FAQ.',
          type: 'error'
        })
      }

      this.isLoadingFaqs = false
    },

    async getFaqCategories() {
      this.isLoadingFaqCategories = true
      try {
        let { data: list } = await getFaqTypeList()
        await this.$store.dispatch('setFaqCategoriesList', { list })
      } catch (error) {
        await onShowNotificationToast({
          body: 'Non è stato possibile recuperare la lista delle FAQ.',
          type: 'error'
        })
      }
      this.isLoadingFaqCategories = false
    },
    onConfirm() {
      this.service.idFaq = this.faqBreadcrumb[this.faqBreadcrumb.length - 1]?.id

      this.isOpenConfirmModal = false

      let serviceCard = !equals(this.serviceCard, EMPTY_SERVICE_CARD) ? this.serviceCard : null
      if (this.isAsset) {
        this.service['moduloMoon'] = null
        if (serviceCard) {
          serviceCard['servizioCittaFacile'] = false
        }
      }


      this.$emit('onConfirm', this.service, serviceCard)
    },
    async onChangeFaq() {
      this.isOpenFaqModal = true

      this.isLoadingFaqs = true
      if (this.faqCategories?.length === 0) {
        await this.getFaqCategories()
      }

      this.faqOptions = this.faqCategories

      this.isLoadingFaqs = false
    },

    onConfirmFaq(faqs) {
      this.faqBreadcrumb = faqs
      this.isOpenFaqModal = false
    },
    onCancelFaq() {
      this.isOpenFaqDeleteModal = true
    },
    onDeleteFaq() {
      this.faqBreadcrumb = []
      this.service.idFaq = null
      this.isOpenFaqDeleteModal = false
    },
    onSelectService(val) {
      let card = this.serviceCards.find((card) => card.id.toString() === val.toString())
      this.service.titolo = card.nome
    },
    onSelectCategory(value) {
      this.serviceCard['categoriaServizio'] = value
    },
    onSelectAsset(value) {
      this.service['asset'] = value
    },
    setServiceName(event) {
      this.serviceCard.nome = event.target.value
    },
    onSelectActiveStatus(value) {
      this.serviceCard.servizioAttivo = value === '1'
    },
    async onSubmit() {
      this.v$.$touch()

      if (this.v$.$error) {
        setTimeout(() => {
          this.$refs.validationAlert.$el.scrollIntoView({ block: 'start' })
        }, 300)

        return
      }

      this.isOpenConfirmModal = true
    }
  }
}
</script>
