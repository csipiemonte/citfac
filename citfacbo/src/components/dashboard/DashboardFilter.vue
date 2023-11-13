<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="dashboard-filter d-flex">
   

    <Transition name="slide">
      <form
        @submit.prevent="onFilter"
        novalidate
        class="col csi-dashboard-filter row align-items-start"
        v-if="isOpen"
      >
        <CsiInput
          class="col-12 col-md-6 col-lg mb-2"
          v-model="startDate"
          label="Da"
          type="date"
          :error="v$.startDate.$error"
          @blur="onFilter"
          @keyup.enter="$event.target.blur()"
          id="startDate"
        >
        </CsiInput>

        <CsiInput
          class="col-12 col-md-6 col-lg mb-2"
          v-model="endDate"
          label="A"
          type="date"
          @blur="onFilter"
          @keyup.enter="$event.target.blur()"
          id="endDate"
        >
        </CsiInput>

        <csi-select
          v-if="!hideOrder"
          clearable
          class="col-12 col-lg mb-2"
          v-model="orderBy"
          label="Ordina per"
          :optionList="orderByList"
          @input="onOrderBy"
          :disabled="disabled"
        ></csi-select>

       
        <csi-alert type="error" v-if="v$.endDate.$errors.length">Periodo non valido</csi-alert>
      </form>
    </Transition>
  </div>
</template>

<script>
import CsiSelect from '../core/form/CsiSelect.vue'
import CsiInput from '../core/form/CsiInput.vue'

import { useVuelidate } from '@vuelidate/core'
import { required, requiredIf, helpers } from '@vuelidate/validators'
import CsiAlert from '../core/CsiAlert.vue'
import { DEFAULT_FEEDBACK_PERIOD, ORDER_BY_MAP } from '../../services/config'

const ORDER_LIST = [
  { id: ORDER_BY_MAP.NAME, name: 'Nome' },
  { id: ORDER_BY_MAP.VOTE_HIGHT, name: 'Voto crescente' },
  { id: ORDER_BY_MAP.VOTE_LOW, name: 'Voto decrescente' }
]

export default {
  components: { CsiSelect, CsiInput, CsiAlert },
  name: 'CsiDashboardFilter',
  emits: ['setPeriod', 'orderBy'],
  props: {
    hideOrder: { type: Boolean, default: false },
    disabled: { type: Boolean, default: false },
    default: { type: Object, default: null }
  },
  data() {
    return {
      v$: useVuelidate(),
      isOpen: true,
      orderBy: null,
      orderByList: ORDER_LIST,
      startDate: null,
      endDate: null
    }
  },
  validations() {
    return {
      startDate: {
        required: helpers.withMessage('Data non valida', required),
        maxValue(val, { endDate }) {
          return new Date(endDate) >= new Date(val)
        }
      },
      endDate: {
        required: helpers.withMessage('Data non valida', required),
        minValue(val, { startDate }) {
          return new Date(val) >= new Date(startDate)
        }
      }
    }
  },

  created() {
    if (this.default) {
      this.endDate = this.default.endDate
      this.startDate = this.default.startDate
    } else {
      this.endDate = DEFAULT_FEEDBACK_PERIOD.endDate
      this.startDate = DEFAULT_FEEDBACK_PERIOD.startDate
    }

    this.setPeriod()
  },
  computed: {},
  methods: {
    openFilters() {
      this.isOpen = !this.isOpen
    },
    async onFilter(event) {
    
      this.v$.$touch()

      if (this.v$.$error) {
        return
      }

      this.setPeriod()
    },
    setPeriod() {
      this.$emit('setPeriod', { startDate: this.startDate, endDate: this.endDate })
    },
    onOrderBy(event) {
      let val = event.target.value
      this.$emit('orderBy', val)
    }
  }
}
</script>
