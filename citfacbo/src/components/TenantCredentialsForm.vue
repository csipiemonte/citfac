<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <CsiInput
    :modelValue="username"
    label="user API"
    id="username"
    class="pb-5"
    @input="onInputUsername"
    :error="v$.username.$error"
    errorMsg="Campo obbligatorio"
    :aria-invalid="v$.username.$invalid"
  />

  <CsiInput
    type="password"
    :modelValue="password"
    label="password API"
    id="password"
    class=""
    @input="onInputPassword"
    :error="v$.password.$error"
    errorMsg="Campo obbligatorio"
    :aria-invalid="v$.password.$invalid"
  />
</template>

<script>
import { useVuelidate } from '@vuelidate/core'
import {  required, requiredIf } from '@vuelidate/validators'
import CsiInput from '../components/core/form/CsiInput.vue'
export default {
  name: 'TenantCredentialsForm',
  components: { CsiInput },
  emits: ['update:username', 'update:password', 'update:newUsername'],
  props: {
    username: String,
    password: String,
    newUsername:Boolean
  },
  data() {
    return {
      v$: useVuelidate(),
      previousUsername: ''
    }
  },
  validations() {
    return {
      username: {
        required
      },
      password: {
        requiredIfNewUsername: requiredIf(this.newUsername)
      }
    }
  },
  created() {
    this.previousUsername = this.username
  },

  methods: {
    onInputUsername(value) {
      this.$emit('update:username', value)
      this.$emit('update:newUsername', this.previousUsername !== value)

    },
    onInputPassword(value) {
      this.$emit('update:password', value)
    }
  }
}
</script>

