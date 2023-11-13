<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="it-list-wrapper mt-4">
    <ul class="it-list">
      <!-- ------------------------------------------------------------------------------------------------------- -->

      <li class="list-item active cf-preferences-tree--root">
        <div class="it-right-zone">
          <span class="text">
            
            <svg aria-hidden="true" class="icon icon-primary"><use 
              :xlink:href="$svgs.getSpriteSvgUrl('it-arrow-right-triangle')"
              ></use></svg>
            
            Pagamenti</span>
          <div class="it-multiple align-items-center d-flex flex-row">
            <preference-channel-checkbox
              :disabled="supportedPushServices.length <= 0"
              v-model="allPushPPay"
              label="Push"
              id="push-checkbox-ppay"
              :channel="PREFERENCES_TYPES_MAP.PUSH"
            />

            <preference-channel-checkbox
              :disabled="supportedMailServices.length <= 0"
              v-model="allEmailPPay"
              label="Email"
              id="email-checkbox-ppay"
              :channel="PREFERENCES_TYPES_MAP.EMAIL"
            />

            <preference-channel-checkbox
              :disabled="supportedSmsServices.length <= 0"
              v-model="allSmsPPay"
              label="Sms"
              id="sms-checkbox-ppay"
              :channel="PREFERENCES_TYPES_MAP.SMS"
            />
          </div>
        </div>
      </li>

      <div
        v-for="(service, index) in servicesList"
        :key="index"
        class="cf-preferences-tree--row relative-position"
      >
        <template v-if="service._enabledChannels">
          <preference-list-item
            :service-title="serviceTitle(service.name)"
            :supported-channels="service._supportedChannels"
            v-model:email="service._enabledChannels.email"
            v-model:push="service._enabledChannels.push"
            v-model:sms="service._enabledChannels.sms"
          />
        </template>
      </div>
    </ul>
  </div>
</template>

<script>
import PreferenceIcon from "./PreferenceIcon.vue";
import PreferenceListItem from "./PreferenceListItem.vue";
import { setReceiptStorageStatus } from "../../../services/api";

import CfInnerLoading from "../../generic/CfInnerLoading.vue";
import { PREFERENCES_TYPES_MAP } from "../../../services/config";
import { isPreferenceChannelActive } from "../../../services/business-logic";
import PreferenceChannelCheckbox from "./PreferenceChannelCheckbox.vue";
import { capitalizeFirst } from "../../../services/utils";

// COMPONENTE SCHEDA LISTA DELLE PREFERENZE PER I PAGAMENTI
// PER UTILIZZARE IL COMPONENTE USARE I PROPS

//@on-change = INVIA AL COMPONENTE PADRE LA LISTA DELLE PREFERENZE AGGIONATE
export default {
  name: "PreferencesPaymentSection",
  components: {
    PreferenceChannelCheckbox,
    CfInnerLoading,
    PreferenceListItem,
    PreferenceIcon,
  },
  emits: ["on-change"],
  props: {
    services: { type: Array, default: () => [] },
  },
  data() {
    return {
      PREFERENCES_TYPES_MAP,

      servicesList: [],
    };
  },
  watch: {
    servicesList: {
      deep: true,
      handler(newVal) {
        if (!this.actions) {
        
          //INVIA LA LISTA DELLE PREFERENZE AGGIORNATE
          this.$emit("on-change", this.servicesList);
        }
      },
    },
  },
  computed: {
    allPushPPay: {
      get() {
        let supportedLength = this.supportedPushServices.length;
        if (supportedLength <= 0) return false;
        if (this.enabledPushServices.length >= supportedLength) return true;
        if (this.disabledPushServices.length >= supportedLength) return false;
        return null;
      },
      set() {
        if (this.enabledPushServices.length > 0) {
          this.disableAllChannel(PREFERENCES_TYPES_MAP.PUSH);
        } else {
          this.enableAllChannel(PREFERENCES_TYPES_MAP.PUSH);
        }
      },
    },
    allEmailPPay: {
      get() {
        let supportedLength = this.supportedMailServices.length;
        if (supportedLength <= 0) return false;
        if (this.enabledEmailServices.length >= supportedLength) return true;
        if (this.disabledEmailServices.length >= supportedLength) return false;
        return null;
      },
      set() {
        if (this.enabledEmailServices.length > 0) {
          this.disableAllChannel(PREFERENCES_TYPES_MAP.EMAIL);
        } else {
          this.enableAllChannel(PREFERENCES_TYPES_MAP.EMAIL);
        }
      },
    },
    allSmsPPay: {
      get() {
        let supportedLength = this.supportedSmsServices.length;
        if (supportedLength <= 0) return false;
        if (this.enabledSmsServices.length >= supportedLength) return true;
        if (this.disabledSmsServices.length >= supportedLength) return false;
        return null;
      },
      set() {
        if (this.enabledSmsServices.length > 0) {
          this.disableAllChannel(PREFERENCES_TYPES_MAP.SMS);
        } else {
          this.enableAllChannel(PREFERENCES_TYPES_MAP.SMS);
        }
      },
    },

    selectedChannels() {
      return this.servicesList.map((service) => service._enabledChannels);
    },
    // LISTA DEI SERVIZI CHE SUPPORTANO LE MAIL
    supportedMailServices() {
      return this.servicesList.filter((service) =>
        isPreferenceChannelActive(
          service._supportedChannels,
          PREFERENCES_TYPES_MAP.EMAIL
        )
      );
    },
    // LISTA DEI SERVIZI CHE HANNO LE MAIL ABILITATE
    enabledEmailServices() {
      return this.supportedSmsServices.filter((service) =>
        isPreferenceChannelActive(
          service._enabledChannels,
          PREFERENCES_TYPES_MAP.EMAIL
        )
      );
    },
    // LISTA DEI SERVIZI CHE HANNO LE MAIL DISABILITATE
    disabledEmailServices() {
      return this.supportedSmsServices.filter(
        (service) =>
          !isPreferenceChannelActive(
            service._enabledChannels,
            PREFERENCES_TYPES_MAP.EMAIL
          )
      );
    },
    // LISTA DEI SERVIZI CHE SUPPORTANO LE NOTIFICHE PUSH
    supportedPushServices() {
      return this.servicesList.filter((service) =>
        isPreferenceChannelActive(
          service._supportedChannels,
          PREFERENCES_TYPES_MAP.PUSH
        )
      );
    },
    // LISTA DEI SERVIZI CHE HANNO LE NOTIFICHE PUSH ABILITATE
    enabledPushServices() {
      return this.supportedSmsServices.filter((service) =>
        isPreferenceChannelActive(
          service._enabledChannels,
          PREFERENCES_TYPES_MAP.PUSH
        )
      );
    },
    // LISTA DEI SERVIZI CHE HANNO LE NOTIFICHE PUSH DISABILITATE
    disabledPushServices() {
      return this.supportedSmsServices.filter(
        (service) =>
          !isPreferenceChannelActive(
            service._enabledChannels,
            PREFERENCES_TYPES_MAP.PUSH
          )
      );
    },
    // LISTA DEI SERVIZI CHE SUPPORTANO GLI SMS
    supportedSmsServices() {
      return this.servicesList.filter((service) =>
        isPreferenceChannelActive(
          service._supportedChannels,
          PREFERENCES_TYPES_MAP.SMS
        )
      );
    },
    // LISTA DEI SERVIZI CHE HANNO GLI SMS ABILITATI
    enabledSmsServices() {
      return this.supportedSmsServices.filter((service) =>
        isPreferenceChannelActive(
          service._enabledChannels,
          PREFERENCES_TYPES_MAP.SMS
        )
      );
    },
    // LISTA DEI SERVIZI CHE HANNO GLI SMS DISABILITATI
    disabledSmsServices() {
      return this.supportedSmsServices.filter(
        (service) =>
          !isPreferenceChannelActive(
            service._enabledChannels,
            PREFERENCES_TYPES_MAP.SMS
          )
      );
    },
    enabledPreferences() {
      let enabledPreferences = {};
      for (let i = 0; i < this.servicesList.length; i++) {
        const service = this.servicesList[i];
        let channels = service._enabledChannels;
        let channelsList = "";
        if (channels) {
          let channelsListArr = Object.entries(channels)
            .filter(([_, v]) => v)
            .map(([k]) => k);
          channelsList = channelsListArr.join(",");
        }
        enabledPreferences[service.name] = channelsList;
      }
      return enabledPreferences;
    },
  },
  async created() {
    this.servicesList = [...this.services];
  },
  methods: {
    serviceTitle(name) {
      let title = name?.replaceAll("_", " ");
      return capitalizeFirst(title);
    },
    disableAllChannel(channel) {
      this.servicesList.forEach((service) => {
        if (!isPreferenceChannelActive(service._supportedChannels, channel))
          return;
        service._enabledChannels[channel] = false;
      });
    },
    enableAllChannel(channel) {
      this.servicesList.forEach((service) => {
        if (!isPreferenceChannelActive(service._supportedChannels, channel))
          return;

        service._enabledChannels[channel] = true;
      });
    },
    emitPreferences() {
      let enabledPreferences = {};
      for (let i = 0; i < this.servicesList.length; i++) {
        const service = this.servicesList[i];
        let channels = service._enabledChannels;
        let channelsList = "";
        if (channels) {
          let channelsListArr = Object.entries(channels)
            .filter(([_, v]) => v)
            .map(([k]) => k);
          channelsList = channelsListArr.join(",");
        }
        enabledPreferences[service.name] = channelsList;
      }
      this.$emit("on-change", enabledPreferences);
    },
    async onInputPaymentPreference(event) {
      let value = event.target.checked;
      this.isSavingPaymentPreference = true;
      try {
        let params = {
          enableReceiptStorage: value,
        };
        let response = await setReceiptStorageStatus(this.userTaxCode, {
          params,
        });
        await this.$store.dispatch("setNotificationInfo", {
          body: "Preferenze modificate con successo.",
          positive: true,
        });
      } catch (e) {
        await this.$store.dispatch("setNotificationInfo", {
          body: "Non è stato possibile modificare le preferenze, si prega di riprovare",
        });
      }
      this.isSavingPaymentPreference = false;
    },
  },
};
</script>


