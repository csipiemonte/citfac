<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <li>
    <div class="list-item active">
      <div class="it-right-zone">
        <span class="text">{{ serviceTitle || "-" }}</span>
        <div class="it-multiple d-flex flex-row align-items-center">
          <preference-icon
            :value="push"
            :disabled="!isChannelSupported(PREFERENCES_TYPES_MAP.PUSH)"
            :type="PREFERENCES_TYPES_MAP.PUSH"
            @on-change="onChangePush"
          />

          <preference-icon
            :value="email"
            :disabled="!isChannelSupported(PREFERENCES_TYPES_MAP.EMAIL)"
            :type="PREFERENCES_TYPES_MAP.EMAIL"
            @on-change="onChangeEmail"
          />
          <preference-icon
            :value="sms"
            :disabled="!isChannelSupported(PREFERENCES_TYPES_MAP.SMS)"
            :type="PREFERENCES_TYPES_MAP.SMS"
            @on-change="onChangeSms"
          />
        </div>
      </div>
    </div>
  </li>
</template>

<script>
import PreferenceIcon from "./PreferenceIcon.vue";
import { PREFERENCES_TYPES_MAP } from "../../../services/config";
import { isPreferenceChannelActive } from "../../../services/business-logic";
// COMPONENTE CHE CONSENTE DI ATTIVARE/DISABILITARE PREFERENZE DI UN SINGOLO SERVIZIO
// PER UTILIZZARE IL COMPONENTE USARE I PROPS
//sms = ABILITA/DISABILITA SMS
//email = ABILITA/DISABILITA EMAIL
//push = ABILITA/DISABILITA PUSH
//"supportedChannels" : LA LISTA DEI CANALI SUPPORTATI
//"serviceName" NOME DEL SERVIZIO
export default {
  name: "PreferenceListItem",
  components: { PreferenceIcon },
  props: {
    sms: Boolean,
    email: Boolean,
    push: Boolean,
    supportedChannels: Object,
    serviceTitle: String,
  },
  data() {
    return {
      PREFERENCES_TYPES_MAP,
    };
  },
  created() {},
  watch: {},
  computed: {},
  methods: {
    isChannelSupported(channel) {
      return isPreferenceChannelActive(this.supportedChannels, channel);
    },
    onChangeSms(value) {
      this.$emit("update:sms", value);
    },
    onChangeEmail(value) {
      this.$emit("update:email", value);
    },
    onChangePush(value) {
      this.$emit("update:push", value);
    },
  },
};
</script>

