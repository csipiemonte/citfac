<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div
    :class="positive ? 'success' : 'error'"
    class="notification with-icon bottom-fix"
    role="alert"
    :id="id"
  >
    <h5 id="titoloNotifica">
      <svg class="icon">
        <use
        :xlink:href="positive ? $svgs.getSpriteSvgUrl('it-check-circle') : $svgs.getSpriteSvgUrl('it-close-circle')"

       
        ></use></svg
      >{{ notificationTitle }}
    </h5>
    <p id="messaggioNotifica">{{ notificationMessage }}</p>
  </div>
</template>

<script>
export default {
  name: "NotificationToast",
  props: {
    id: String,
    notificationTitle: String,
    notificationMessage: String,
    positive: Boolean,
  },

  data() {
    return {
      currentId: null,
    };
  },
  unmounted() {
    this.$store.dispatch("resetNotificationInfo");
  },
  async created() {},

  computed: {},

  methods: {},
};
</script>

