<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="toast-container position-fixed bottom-0 end-0 p-3">
    <csi-toast v-for="toast in toasts" :key="toast.index" :toast="toast"></csi-toast>
  </div>
</template>

<script>
import CsiToast from './CsiToast.vue'

export default {
  name: 'CsiToastList',
  components: { CsiToast },
  emits: ['update:modelValue'],
  props: {
    toasts: { type: Array, required: true, default: () => [] }
  },

  data() {
    return {
      currentId: null
    }
  },

  async created() {},

 
}
</script>
