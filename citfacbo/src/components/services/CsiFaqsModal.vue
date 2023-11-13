<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <CsiModal
    id="faqModal"
    :modelValue="modelValue"
    @onHide="onHide"
    title="FAQ CRM"
    @onConfirm="onConfirmFaq"
  >
    <CsiInnerLoading :showing="loading" />
    <template v-if="faqOptions.length > 0">
      <CsiFaqTree @onSelect="onSelectFaq" :defaultNodes="faqOptions"></CsiFaqTree>
    </template>
  </CsiModal>
</template>

<script>
import CsiFaqTree from '../services/CsiFaqTree.vue'
import CsiInnerLoading from '../core/CsiInnerLoading.vue'
import CsiModal from '../core/CsiModal.vue'

export default {
  components: { CsiModal, CsiInnerLoading, CsiFaqTree },
  name: 'CsiFaqsModal',
  emits: ['onConfirmFaq', 'update:modelValue'],
  props: {
    faqOptions: Array,
    modelValue: Boolean,
    loading:Boolean
  },
  data() {
    return {
      selectedFaq: []
    }
  },

  computed: {},
  methods: {
    onHide(val) {
      this.$emit('update:modelValue', val)
    },
    onSelectFaq(breadcrumb) {
      this.selectedFaq = breadcrumb
    },
    onConfirmFaq() {
      this.$emit('onConfirmFaq', this.selectedFaq)
    }
  }
}
</script>

