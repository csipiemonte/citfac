<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div
    :id="'notification-toast' + index"
    :class="typeClass"
    class="csi-toast notification-toast toast"
    role="alert"
    aria-live="assertive"
    aria-atomic="true"
  >
    <div class="toast-header">
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" class="notify-icon me-2 col-auto">
        <path :d="imgPath"></path>
      </svg>

      <h3 class="col">{{ title ?? 'Attenzione' }}</h3>
      <button
        @click="dismissToast"
        type="button"
        class="btn-close col-auto"
        data-bs-dismiss="toast"
        aria-label="chiudi"
      ></button>
    </div>
    <p class="toast-body">
      {{ body }}
    </p>
  </div>
</template>

<script>
export default {
  name: 'CsiToast',
  emits: ['update:modelValue'],
  props: {
    toast: { type: Object, required: true, default: null }
  },

  data() {
    return {
      currentId: null
    }
  },

  async created() {
    setTimeout(()=>{
      this.dismissToast()
    }, 4000)
  },

  computed: {
    index() {
      return this.toast?.index
    },
    type() {
      return this.toast?.type
    },
    title() {
      return this.toast?.title
    },
    typeClass() {
      return `csi-toast__${this.type}`
    },
    imgPath() {
      return this.$svgs.getAlertSvg(this.type)
    },
    body() {
      return this.toast?.body
    }
  },

  methods: {
    dismissToast() {
      this.$store.dispatch('clearNotificationToast', this.index)
    }
  }
}
</script>
