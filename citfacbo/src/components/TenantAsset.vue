<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="tenant-asset">
    <CsiToggle
      :label="assetName"
      v-model="selected"
      class="pb-3"
      :id="assetCode"
      @input="onSelect"
    />

    <Transition name="slide">
      <div class="border p-3 mb-3" v-if="selected && (hasCredentials || extraFields)">
        <template v-if="credentialsType === CREDENTIALS_TYPES.BASIC">
          <CsiInput
            v-model="credentials.username"
            label="user API"
            id="username"
            class="mb-3"
            :error="v$.credentials.username.$error"
            @input= "event => onInputCredential(event, index, 'username')"
            errorMsg="Campo obbligatorio"
            :aria-invalid="v$.credentials.username.$invalid"
          />

          <CsiInput
            type="password"
            v-model="credentials.password"
            label="password API"
            id="password"
            class="mb-3"
            :error="v$.credentials.password.$error"
            @input= "event => onInputCredential(event, index, 'password')"
            errorMsg="Campo obbligatorio"
            :aria-invalid="v$.credentials.password.$invalid"
          />
        </template>
        <template v-else-if="credentialsType === CREDENTIALS_TYPES.JWT">
          <CsiInput
            v-model="credentials.jwt"
            label="JWT"
            id="jwt"
            class="mb-3"
            :error="v$.credentials.jwt.$error"
            @input= "event => onInputCredential(event, index, 'jwt')"
            errorMsg="Campo obbligatorio"
            :aria-invalid="v$.credentials.jwt.$invalid"
          />
        </template>

        
        <slot name="extraFields"></slot> 
       
      </div>
    </Transition>
  </div>
</template>

<script>
import { useVuelidate } from '@vuelidate/core'
import { required, requiredIf } from '@vuelidate/validators'
import CsiInput from '../components/core/form/CsiInput.vue'
import CsiToggle from '../components/core/form/CsiToggle.vue'

const CREDENTIALS_TYPES = {
  BASIC: 'BASIC',
  JWT: 'JWT',
  CMS_SERVICES: 'CMS_SERVICES'
}
export default {
  name: 'TenantAsset',
  components: { CsiInput, CsiToggle },
  emits: ['onSelect', 'onInputCredentials', 'update:newUsername', 'update:selezionato'],
  props: {
    asset: Object,
    index: Number,
    extraFields:Boolean,
  },
  data() {
    return {
      CREDENTIALS_TYPES,
      v$: useVuelidate(),
      selected: false,
      credentials: null,
      lastUsername: null,
      lastJwt: null,
      urlCmsService:null
    }
  },
  validations() {
    return {
      credentials: {
        username: {
          requiredIfBasic: requiredIf(this.selected && this.credentialsType === CREDENTIALS_TYPES.BASIC)
        },
        password: {
          requiredIfNewUsername: requiredIf(this.selected &&
            this.newUsername && this.credentialsType === CREDENTIALS_TYPES.BASIC
          )
        },
        jwt: {
          requiredIfJwt: requiredIf(this.selected && this.credentialsType === CREDENTIALS_TYPES.JWT)
        }
      }
    }
  },
  created() {
    this.selected = this.asset.selezionato
    this.credentials = { ...this.asset.credenziali_info }

    this.lastUsername = this.credentials.username
  },
 
  computed: {
    assetName() {
      return this.asset.nome
    },
    assetCode() {
      return this.asset.codice
    },
    hasCredentials() {
      return this.credentialsType && this.asset.credenziali_info
    },
    credentialsType() {
      return this.asset.tipoCredenziali
    },
    newUsername() {
      return this.credentials.username !== this.lastUsername
    }
  },
  methods: {
    onSelect(val) {
      this.$emit('onSelect', val, this.index)
    },
    onInputCredential(value, index, type){

    
         this.$emit('onInputCredentials', value, index, type) 
     
    }
  }
}
</script>

