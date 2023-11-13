<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <main class="main container mt-4">
    <h1 class="csi-h3 mb-4">{{ $route.meta?.title ?? '-' }}</h1>

    <template v-if="isLoading">
      <CsiInnerLoading showing></CsiInnerLoading>
    </template>

    <template v-else>
      <div ref="validationAlert">
        <CsiAlert type="error" v-if="v$.$error"
          >É necessario compilare correttamente i campi obbligatori
        </CsiAlert>
      </div>

      <form @submit.prevent="onSubmit" novalidate v-if="newTenant">
        <!-- CONFIGURAZIONE ENTE -->
        <!-- ------------------------------------------------------------------------------------------ -->
        <CsiFormSection id="ente" title="Ente" class="csi-nav-target mb-4" accordion>
          <CsiInput
            required
            v-model="newTenant.tenant"
            label="VirtualHost"
            id="virtualhost"
            class="mb-3"
            :error="v$.newTenant.tenant.$error"
            errorMsg="Campo obbligatorio"
            :aria-invalid="v$.newTenant.tenant.$invalid"
          />

          <CsiInput
            v-model="newTenant.nome"
            required
            label="Denominazione ente"
            id="name"
            class="mb-3"
            :aria-invalid="v$.newTenant.nome.$invalid"
            :error="v$.newTenant.nome.$error"
            errorMsg="Campo obbligatorio"
          />

          <CsiInput
            v-model="newTenant.placeholderAsset"
            required
            label="Placeholder"
            id="name"
            class="mb-3"
            :aria-invalid="v$.newTenant.placeholderAsset.$invalid"
            :error="v$.newTenant.placeholderAsset.$error"
            errorMsg="Campo obbligatorio"
          />

          <CsiInput
            required
            v-model="newTenant.codiceEnte"
            label="Codice Ente"
            id="ipa"
            class="mb-3"
            :aria-invalid="v$.newTenant.codiceEnte.$invalid"
            :error="v$.newTenant.codiceEnte.$error"
            errorMsg="Campo obbligatorio"
          />

          <CsiSelect
            required
            option-value="id"
            option-label="nome"
            v-model="region"
            label="Regione"
            id="region"
            :optionList="regionOptions"
            class="mb-4"
            :aria-invalid="v$.region.$invalid"
            :error="v$.region.$error"
            errorMsg="Campo obbligatorio"
          />

          <CsiInput
            required
            v-model="newTenant.cf"
            label="Codice fiscale ente"
            id="fiscalCode"
            class="mb-3"
            :aria-invalid="v$.newTenant.cf.$invalid"
            :error="v$.newTenant.cf.$error"
            errorMsg="Campo obbligatorio"
          />

          <CsiInput
            required
            v-model="newTenant.homePage"
            label="Sito web"
            id="urlHomepage"
            class="mb-3"
            :aria-invalid="v$.newTenant.homePage.$invalid"
            :error="v$.newTenant.homePage.$error"
            errorMsg="Campo obbligatorio"
          />
        </CsiFormSection>

        <!-- CONFIGURAZIONE MANUTENZIONE -->
        <!-- ------------------------------------------------------------------------------------------ -->
        <CsiFormSection
          id="manutenzione"
          title="Manutenzione"
          class="csi-nav-target mb-4"
          accordion
        >
          <CsiToggle
            label="Manutenzione"
            v-model="newTenant.maintenance"
            class="pb-3"
            id="maintenance"
          />
          <div>
            <div class="border p-3 mb-3">
              <CsiInput
                :required="isMaintenance"
                v-model="maintenance.newUsername"
                label="username"
                id="username"
                class="mb-3"
                :error="v$.maintenance.newUsername.$error"
                errorMsg="Campo obbligatorio"
                :aria-invalid="v$.maintenance.newUsername.$invalid"
              />

              <CsiInput
                :required="maintenance.newUsername !== maintenance.username"
                type="password"
                v-model="maintenance.password"
                label="Password"
                id="password"
                class="mb-3"
                :error="v$.maintenance.password.$error"
                :aria-invalid="v$.maintenance.password.$invalid"
              />
            </div>
          </div>
        </CsiFormSection>

        <!-- CONFIGURAZIONE ASSETS -->
        <!-- ------------------------------------------------------------------------------------------ -->
        <CsiFormSection accordion title="Assets" id="assets" class="csi-nav-target">
          <!-- Aggiungiamo un asset SERVIZI CMS statico -->

          <TenantAsset
            :asset="cmsAsset"
            v-model:selected="cmsAsset.selezionato"
            @onSelect="onSelectCmsServices"
          >
            <template #extraFields>
              <CsiInput
                required
                v-model="newCms.backOffice"
                label="Url Backoffice"
                id="cmsBackOffice"
                class="mb-3"
                :aria-invalid="v$.newCms.backOffice.$invalid"
                :error="v$.newCms.backOffice.$error"
                errorMsg="Campo obbligatorio"
              />

              <CsiInput
                v-model="newCms.urlServizi"
                label="API Servizi"
                id="cmsUrlServices"
                class="mb-3"
              />

              <CsiInput v-model="newCms.urlMenu" label="API Menu" id="cmsUrlMenu" class="mb-3" />
            </template>
          </TenantAsset>

          <TenantAsset
            @onSelect="onSelectAsset"
            v-for="(asset, index) in newAssets"
            :key="asset.id"
            :index="index"
            v-model:selected="asset.selezionato"
            :asset="asset"
            @onInputCredentials="onInputCredentials"
            :extra-fields="ASSETS_MAP.APPOINTMENTS === asset.codice"
          >
            <template #extraFields v-if="ASSETS_MAP.APPOINTMENTS === asset.codice">
              <CsiRadio
                boolean-options
                label="Tipo di accesso"
                id="appschedserviceType"
                v-model="newTenant.appschedPublic"
                :options="ACCESS_PUBLIC_OPTIONS"
                name="appschedserviceType"
              ></CsiRadio>
            </template>
          </TenantAsset>
        </CsiFormSection>

        <!-- CONFIGURAZIONE FIREBASE -->
        <!-- ------------------------------------------------------------------------------------------ -->
        <CsiFormSection
          v-if="notificationsActivated"
          accordion
          title="Firebase"
          id="firebase"
          class="csi-nav-target mt-4"
        >
          <CsiInput
            required
            v-model="firebase.apiKey"
            label="API key"
            id="apiKey"
            class="mb-3"
            :error="v$.firebase.apiKey.$error"
            errorMsg="Campo obbligatorio"
            :aria-invalid="v$.firebase.apiKey.$invalid"
          />

          <CsiInput
            required
            v-model="firebase.appId"
            label="APP Id"
            id="appId"
            class="mb-3"
            :error="v$.firebase.appId.$error"
            errorMsg="Campo obbligatorio"
            :aria-invalid="v$.firebase.appId.$invalid"
          />

          <CsiInput
            required
            v-model="firebase.authDomain"
            label="Auth Domain"
            id="authDomain"
            class="mb-3"
            :error="v$.firebase.authDomain.$error"
            errorMsg="Campo obbligatorio"
            :aria-invalid="v$.firebase.authDomain.$invalid"
          />

          <CsiInput
            required
            v-model="firebase.messagingSenderId"
            label="Messaging Sender Id"
            id="messagingSenderId"
            class="mb-3"
            :error="v$.firebase.messagingSenderId.$error"
            errorMsg="Campo obbligatorio"
            :aria-invalid="v$.firebase.messagingSenderId.$invalid"
          />

          <CsiInput
            required
            v-model="firebase.projectId"
            label="project Id"
            id="projectId"
            class="mb-3"
            :error="v$.firebase.projectId.$error"
            errorMsg="Campo obbligatorio"
            :aria-invalid="v$.firebase.projectId.$invalid"
          />

          <CsiInput
            required
            v-model="firebase.storageBucket"
            label="Storage Bucket"
            id="storageBucket"
            class="mb-3"
            :error="v$.firebase.storageBucket.$error"
            errorMsg="Campo obbligatorio"
            :aria-invalid="v$.firebase.storageBucket.$invalid"
          />

          <CsiInput
            required
            v-model="firebase.vapidKey"
            label="Vapid Key"
            id="vapidKey"
            class="mb-3"
            :error="v$.firebase.vapidKey.$error"
            errorMsg="Campo obbligatorio"
            :aria-invalid="v$.firebase.vapidKey.$invalid"
          />
        </CsiFormSection>

        <div class="m-0 row justify-content-end pt-3">
          <csi-button type="submit" :loading="isSavingSettings" class="col-auto">Salva</csi-button>
        </div>
      </form>

      <CsiModal
        id="modalConfirm"
        v-model="isOpenConfirmModal"
        :title="$route.meta?.title"
        @onConfirm="onConfirm"
        :loading="isSavingSettings"
      >
        <p>
          <strong>Attenzione</strong><br />
          Stai per modificare la configurazione del Comune di {{ tenant.nome }}
        </p>
      </CsiModal>

      <CsiModal id="modalCms" v-model="isOpenCmsModal" title="modalCms" no-action>
        <p>
          <strong>Attenzione</strong><br />
          disabilitando il CMS verranno cancellati tutti i servizi inseriti nella configurazione. Le
          modifiche verranno applicate al salvataggio della configurazione.
        </p>

        <div class="text-end">
          <csi-button outline @click="closeCmsModal">Chiudi</csi-button>
        </div>
      </CsiModal>
    </template>
  </main>
</template>

<script>
import CsiButton from '../../components/core/CsiButton.vue'
import CsiModal from '../../components/core/CsiModal.vue'
import CsiFormSection from '../../components/core/form/CsiFormSection.vue'
import CsiInput from '../../components/core/form/CsiInput.vue'
import CsiSelect from '../../components/core/form/CsiSelect.vue'
import CsiToggle from '../../components/core/form/CsiToggle.vue'
import CsiNavScroll from '../../components/core/CsiNavScroll.vue'
import CsiAlert from '../../components/core/CsiAlert.vue'
import TenantCredentialsForm from '../../components/TenantCredentialsForm.vue'
import { ASSETS_MAP } from '../../services/config'
import {
  updateTenant,
  getCredentials,
  getRegionList,
  updateCredential,
  getFirebaseConfig,
  updateFirebaseConfig,
  createFirebaseConfig,
  createTenantCms,
  updateTenantCms,
  deleteTenantCms,
  createCredential,
  updateMaintenance
} from '../../services/api'
import { equals, onShowNotificationToast } from '../../services/business-logic'
import { useVuelidate } from '@vuelidate/core'
import { required, requiredIf } from '@vuelidate/validators'
import CsiInnerLoading from '../../components/core/CsiInnerLoading.vue'
import TenantAsset from '../../components/TenantAsset.vue'
import CsiRadio from '../../components/core/form/CsiRadio.vue'

const EMPTY_TENANT = {
  id: null,
  nome: null,
  tenant: null,
  templateNotificatore: null,
  codiceImmagine: null,
  codiceEnte: null,
  regione: null,
  logoutUri: null,
  cf: null,
  apiManagerKey: null,
  apiManagerSecret: null,
  homePage: null,
  urlServizi: null,
  urlMenu: null,
  assets: [],
  cms: null,
  appschedPublic: false,
  placeholderAsset: null
}
const CREDENTIALS_TYPES = {
  BASIC: 'BASIC',
  JWT: 'JWT'
}

const EMPTY_FIREBASE = {
  apiKey: null,
  appId: null,
  authDomain: null,
  messagingSenderId: null,
  projectId: null,
  storageBucket: null,
  vapidKey: null
}

const EMPTY_CMS = {
  backOffice: null,
  urlServizi: null,
  urlMenu: null
}

const ACCESS_PUBLIC_OPTIONS = [
  { label: 'Pubblico', value: true },
  { label: 'Autenticato', value: false }
]

const MAINTENANCE_CODE = 'MANUTENZIONE'

export default {
  components: {
    CsiInput,
    CsiSelect,
    CsiFormSection,
    CsiToggle,
    CsiButton,
    CsiModal,
    CsiNavScroll,
    CsiAlert,
    TenantCredentialsForm,
    CsiInnerLoading,
    TenantAsset,
    CsiRadio
  },
  name: 'PageTenant',

  data() {
    return {
      ASSETS_MAP,
      ACCESS_PUBLIC_OPTIONS,
      v$: useVuelidate(),
      isLoading: false,
      virtualhost: '',
      name: '',
      ipaCode: '',
      region: '',
      regionOptions: [],
      urlPortal: '',
      fiscalCode: '',
      assistanceCrm: false,
      tenantCode: '',
      ppay: false,
      isLoadingCredentials: false,
      credentials: null,
      username: null,
      password: null,
      documents: false,
      appointments: false,
      notifications: false,
      notificationTemplate: '',
      isSavingSettings: false,
      isOpenConfirmModal: false,
      newTenant: null,
      newAssets: null,
      defaultFirebase: null,
      firebase: null,
      newCms: null,
      isOpenCmsModal: false,

      maintenance: {
        username: null,
        newUsername: null,
        password: null
      },
      cmsAsset: {
        id: 'cmsService',
        codice: 'CMS_SERVICE',
        nome: 'Servizi CMS',
        tipoCredenziali: 'CMS_SERVICES',
        descrizione: 'Abilitazione servizi CMS',
        selezionato: false,
        credenziali_info: {
          backOffice: null,
          urlServizi: null,
          urlMenu: null
        }
      }
    }
  },
  validations() {
    return {
      newTenant: {
        nome: { required },
        tenant: { required },
        codiceEnte: { required },
        cf: { required },
        homePage: { required },
        placeholderAsset: { required }
      },
      newCms: {
        backOffice: {
          requiredIfCmsSelected: requiredIf(this.cmsAsset?.selezionato)
        }
      },
      region: { required },
      firebase: {
        apiKey: {
          requiredIfNotification: this.requiredIfNotification
        },
        appId: {
          requiredIfNotification: this.requiredIfNotification
        },
        authDomain: {
          requiredIfNotification: this.requiredIfNotification
        },
        messagingSenderId: {
          requiredIfNotification: this.requiredIfNotification
        },
        projectId: {
          requiredIfNotification: this.requiredIfNotification
        },
        storageBucket: {
          requiredIfNotification: this.requiredIfNotification
        },
        vapidKey: {
          requiredIfNotification: this.requiredIfNotification
        }
      },
      maintenance: {
        newUsername: {
          requiredIfMaintenance: requiredIf(this.isMaintenance)
        },
        password: {
          requiredIfNewUsername: requiredIf(
            this.maintenance.newUsername !== this.maintenance.username
          )
        }
      }
    }
  },
  created() {
    this.v$ = useVuelidate()
  },
  async mounted() {
    this.isLoading = true

    let regionsPromise = getRegionList()

    try {
      let { data: list } = await regionsPromise
      this.regionOptions = list
    } catch (error) {
      console.error(error)
    }
    this.newTenant = this.tenant ? { ...this.tenant } : EMPTY_TENANT
    if (this.tenant) {
      await this.getTenantInfo()
      //Precompilazione form
      this.setDefaultParams()
    }

    this.isLoading = false
  },
  computed: {
    requiredIfNotification() {
      return requiredIf(this.notificationsActivated)
    },
    tenant() {
      return this.$store.getters['getTenant']
    },
    cms() {
      return this.$store.getters['getTenantCms']
    },
    tenantId() {
      return this.$store.getters['getTenantId']
    },
    userAssetsCodes() {
      return this.tenant?.assets?.map((a) => a.codice)
    },
    notificationsActivated() {
      return !!this.newAssets?.find((a) => a.codice === 'NOTIFICATIONS' && a.selezionato)
    },
    assetsList() {
      return this.$store.getters['getAssets']
    },
    isMaintenance() {
      return this.newTenant?.maintenance
    }
  },
  methods: {
    async getTenantInfo() {
      let credentialsPromise = getCredentials(this.tenantId)
      let firebasePromise = getFirebaseConfig(this.tenantId)

      try {
        let { data: credentials } = await credentialsPromise
        this.credentials = credentials
      } catch (error) {
        console.error(error)
      }

      try {
        let { data: firebase } = await firebasePromise
        this.defaultFirebase = firebase
      } catch (error) {
        console.error(error)
      }
    },
    setDefaultParams() {
      //Verifichiamo se l'asset appsched è pubblico o autenticato
      let appsched = this.tenant.assets?.find((a) => a.codice === ASSETS_MAP.APPOINTMENTS)
      this.newTenant.appschedPublic = appsched?.showPublic

      this.cmsAsset['selezionato'] = !!this.cms
      this.newCms = this.cms ? { ...this.cms } : { ...EMPTY_CMS }

      this.cmsAsset.credenziali_info.backOffice = this.newCms.backOffice
      this.cmsAsset.credenziali_info.urlServizi = this.newCms.urlServizi
      this.cmsAsset.credenziali_info.urlMenu = this.newCms.urlMenu

      if (this.regionOptions?.length > 0) {
        let tenantRegion = this.tenant?.regione?.id?.toString()

        this.region = this.regionOptions.find((r) => r.id?.toString() === tenantRegion)?.id ?? null
      }

      //Controlliamo se ci sono impostazioni di manutenzione
      let mainteanceCredentials = this.credentials.find((c) => c.api === MAINTENANCE_CODE)
      if (mainteanceCredentials) {
        this.maintenance.username = mainteanceCredentials.username
        this.maintenance.newUsername = mainteanceCredentials.username
      }
      this.newTenant["maintenance"] = this.tenant?.maintenance

      this.newAssets = this.getUserAssetsInfo()
      this.firebase = { ...this.defaultFirebase } ?? { ...EMPTY_FIREBASE }
    },

    onSelectAsset(value, index) {
      this.newAssets[index].selezionato = value
    },
    onSelectCmsServices(value) {
      this.cmsAsset['selezionato'] = value

      if (!value) {
        this.isOpenCmsModal = true
      }
    },
    onInputCredentials(value, index, type) {
      this.newAssets[index].credenziali_info[type] = value
      if ((type === 'password' && value !== '') || type !== 'password') {
        this.newAssets[index].credenziali_info.changed = true
      }
    },
    async onSubmit() {
      this.v$.$touch()

      if (this.v$.$error) {
        this.$refs.validationAlert.scrollIntoView({ block: 'start' })

        return
      }

      this.isOpenConfirmModal = true
    },
    getUserAssetsInfo() {
      return this.assetsList.map((asset) => {
        asset.selezionato = !!this.userAssetsCodes?.includes(asset.codice)
        let credentialsType = asset.tipoCredenziali
        if (credentialsType) {
          let credentialsInfo = this.credentials?.find(
            (credential) => credential.api === asset.codice
          )

          if (!credentialsInfo) {
            if (credentialsType === CREDENTIALS_TYPES.BASIC) {
              credentialsInfo = {
                api: asset.codice,
                password: null,
                type: 'Basic',
                username: null
              }
            } else if (credentialsType === CREDENTIALS_TYPES.JWT) {
              credentialsInfo = {
                api: asset.codice,
                jwt: null
              }
            }
          }

          asset.credenziali_info = credentialsInfo
        }

        return asset
      })
    },
    async onConfirm() {
      this.isSavingSettings = true

      if (this.tenant?.id) {
        await this.changeConfiguration()
      } else {
        await this.newConfiguration()
      }

      this.isSavingSettings = false
    },
    async updateCms() {
      let cmsPromise = this.cms
        ? updateTenantCms(this.tenantId, this.newCms)
        : createTenantCms(this.tenantId, this.newCms)

      try {
        let { data: cms } = await cmsPromise
        this.$store.dispatch('setTenantCms', { cms: this.newCms })
      } catch (error) {
        await onShowNotificationToast({
          body: 'Non è stato possibile salvare la configurazione del CMS',
          type: 'error'
        })
      }
    },
    async deleteCms() {
      try {
        let response = await deleteTenantCms(this.tenantId)
        this.$store.dispatch('setTenantCms', { cms: null })
      } catch (error) {}
    },

    async updateTenant() {
      let region = this.regionOptions?.find((r) => r.id.toString() === this.region?.toString())
      let payload = { ...this.newTenant }
      payload.regione = region

      const assets = this.newAssets?.filter((a) => a.selezionato)

      payload.assets = JSON.parse(JSON.stringify(assets))
      payload.assets.forEach((a) => {
        delete a.selezionato
        delete a.credenziali_info
      })

      try {
        let { data: tenant } = await updateTenant(this.tenantId, payload)

        this.$store.dispatch('setTenant', { tenant })
        await onShowNotificationToast({
          body: 'Impostazioni salvate con successo',
          type: 'success'
        })
      } catch (e) {
        await onShowNotificationToast({
          body: 'Non è stato possibile salvare le impostazioni',
          type: 'error'
        })
      }
    },
    async configureCms() {
      //se si disabilita cancelliamo il campo urlServizi e puliamo anche la lista dei servizi salvati
      if (this.cmsAsset?.selezionato) {
        await this.updateCms()
      } else {
        if (this.cms) await this.deleteCms()
        await this.$store.dispatch('setServiceCardList', { list: null })
      }
    },
    async configureMaintenance() {
      //Prima di attivare la manutenzione dobbiamo aggiornare/creare le credenziali (solo se sono cambiate)
      let maintenanceCredentials = true
      if (this.maintenance.username !== this.maintenance.newUsername || this.maintenance.password) {

        let payload = {
          username: this.maintenance.newUsername,
          password: this.maintenance.password
        }

        maintenanceCredentials = await this.updateCredential(payload, 0, MAINTENANCE_CODE) //PER la manutenzione l'asset ID sarà sempre 0
      }
  
      if (this.isMaintenance !== this.tenant.maintenance && maintenanceCredentials) {
        let payload = {
          status: this.isMaintenance
        }
        let { data } = await updateMaintenance(this.tenantId, payload)
      }
    },
    async configureAssetCredentials() {
      let assets = this.newAssets?.filter((a) => a.selezionato && a.tipoCredenziali)

      for (let i = 0; i < assets.length; i++) {
        let assetCredentials = assets[i].credenziali_info

        if (assetCredentials.changed) {
          let response = await this.updateCredential(
            assetCredentials,
            assets[i].id,
            assets[i].codice
          )
        }
      }
    },
    async changeConfiguration() {
      //GESTIONE CMS
      await this.configureCms()

      //GESTIONE MANUTENZIONE
      await this.configureMaintenance()
      //AGGIONAMENTO TENANT
      await this.updateTenant()

      //GESTIONE CREDENZIALI E ASSET
      await this.configureAssetCredentials()

      //GESTIONE FIREBASE
      if (this.notificationsActivated) {
        await this.updateFirebase()
      }
      this.isOpenConfirmModal = false
    },

    async updateCredential(credential, assetId, assetCode) {
     
      if (!credential) return

      delete credential.changed

      //Verifichiamo se la credential esiste già
      let isCredetialExists = this.credentials.find((c) => c.api === assetCode)
      let response = null
      try {
        if (isCredetialExists) {
          response = await updateCredential(this.tenantId, assetId, credential)
        } else {
          response = await createCredential(this.tenantId, assetId, credential)
        }

        await onShowNotificationToast({
          body: 'credenziali salvata con successo',
          type: 'success'
        })
        return true
      } catch (e) {
        await onShowNotificationToast({
          body: 'Non è stato possibile salvare le credenziali',
          type: 'error'
        })
        return false
      }
    },

    async updateFirebase() {
      if (equals(this.defaultFirebase, this.firebase)) {
        return
      }

      let firebasePromise = !this.defaultFirebase
        ? createFirebaseConfig(this.tenantId, this.firebase)
        : updateFirebaseConfig(this.tenantId, this.firebase)

      try {
        let { data: firebase } = await firebasePromise

        await onShowNotificationToast({
          body: 'Configurazione Firebase salvata con successo',
          type: 'success'
        })
      } catch (e) {
        await onShowNotificationToast({
          body: 'Non è stato possibile salvare le configurazione firebase',
          type: 'error'
        })
      }
    },
    async newConfiguration() {
      //TODO
    },
    closeCmsModal() {
      this.isOpenCmsModal = false
    }
  }
}
</script>
