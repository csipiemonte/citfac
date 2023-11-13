<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="border user-preferences-card py-3">
    <div class="container">
      <h3 class="mb-4">Preferenze</h3>

      <!-- Preferenze di notifica -->
      <h4 id="preferenze-notifiche">Notifiche</h4>

      <p>
        Accendi l'icona relativa al canale su cui vuoi ricevere i messaggi di
        notifica. (blu = accesso, grigio = spento). <br />
        Puoi scegliere tra email, sms o push.
      </p>
      <p>
        La notifica push è una tipologia di messagistica istantanea con la quale
        il messaggio perviene al destinatario senza che questo debba effettuare
        un accesso al servizio. Tale modalità è quella tipicamente utilizzata da
        applicazioni mobile.
      </p>
      <cf-inner-loading :showing="isLoading" />
      <template v-if="!isLoading">
        <div class="it-list-wrapper">
          <ul class="it-list">
            <!-- ITEM HEADER -->
            <!-- Permette di abilitare/disabilitare bulk il canale di notifica su tutti i servizi -->
            <!-- ------------------------------------------------------------------------------------------------------- -->
            <li>
              <div class="list-item active">
                <div class="it-right-zone">
                  <span class="text"></span>
                  <div class="it-multiple align-items-center d-flex flex-row">
                    <preference-channel-checkbox
                      :disabled="supportedPushServices.length <= 0"
                      v-model="allPush"
                      label="Push"
                      id="push-checkbox"
                      :channel="PREFERENCES_TYPES_MAP.PUSH"
                    />

                    <preference-channel-checkbox
                      :disabled="supportedMailServices.length <= 0"
                      v-model="allEmail"
                      label="Email"
                      id="email-checkbox"
                      :channel="PREFERENCES_TYPES_MAP.EMAIL"
                    />

                    <preference-channel-checkbox
                      :disabled="supportedSmsServices.length <= 0"
                      v-model="allSms"
                      label="Sms"
                      id="sms-checkbox"
                      :channel="PREFERENCES_TYPES_MAP.SMS"
                    />
                  </div>
                </div>
              </div>
            </li>

            <!-- LISTA DEI SERVIZI -->
            <!-- ------------------------------------------------------------------------------------------------------- -->
            <div v-for="(service, index) in generalServices" :key="index">
              <template v-if="service._enabledChannels">
                <preference-list-item
                  :service-title="service.title"
                  :supported-channels="service._supportedChannels"
                  v-model:email="service._enabledChannels.email"
                  v-model:push="service._enabledChannels.push"
                  v-model:sms="service._enabledChannels.sms"
                />
              </template>
            </div>
          </ul>
        </div>
        <!-- LISTA DEI SERVIZI PAGAMENTI -->
        <template v-if="isPaymentEnabled">
          <preferences-payment-section
            @on-change="onChangePaymentPreferences"
            :services="ppayServices"
          ></preferences-payment-section>
        </template>

        <div class="mt-5" v-if="actions">
          <button
            class="btn btn-primary col btn-icon"
            style="min-width: 180px"
            :readonly="isSavingPreferences"
            @click="onConfirm"
          >
            <span>CONFERMA</span>
            <cf-inner-loading
              class="ms-1"
              small
              :showing="isSavingPreferences"
            />
          </button>
        </div>

        <!-- Preferenze di pagamenti -->
        <div class="my-5" v-if="isPaymentEnabled">
          <h4 id="preferenze-pagamenti">Pagamenti</h4>

          <div class="form-check">
            <div class="toggles">
              <label for="togglePayments">
                Salva ricevute sull'archivio documenti
                <input
                  type="checkbox"
                  id="togglePayments"
                  v-model="isPaymentPreference"
                  @input="onInputPaymentPreference"
                />
                <span class="lever"></span>
              </label>
              <div class="small" v-if="isSavingPaymentPreference">
                Salvataggio in corso...
              </div>
            </div>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<script>
import PreferenceIcon from "./PreferenceIcon.vue";
import PreferenceListItem from "./PreferenceListItem.vue";
import {
  getNotifyPreferencesList,
  getReceiptStorageStatus,
  saveUserContacts,
  setNotifyPreferencesList,
  setReceiptStorageStatus,
} from "../../../services/api";
import { keyBy } from "../../../services/utils";
import CfInnerLoading from "../../generic/CfInnerLoading.vue";
import { ASSETS_MAP, PREFERENCES_TYPES_MAP } from "../../../services/config";
import {
  isPreferenceChannelActive,
  mapPreferencesList,
  setNotifyPreferences,
} from "../../../services/business-logic";
import PreferenceChannelCheckbox from "./PreferenceChannelCheckbox.vue";
import PreferencesPaymentSection from "./PreferencesPaymentSection.vue";

// COMPONENTE SCHEDA LISTA DELLE PREFERENZE
// PER UTILIZZARE IL COMPONENTE USARE I PROPS
//"actions" MOSTRA BOTTONI PER MODIFICA E CONFERMA PREFERENZE
//@on-change = INVIA AL COMPONENTE PADRE LA LISTA DELLE PREFERENZE AGGIONATE
export default {
  name: "PreferencesCard",
  components: {
    PreferenceChannelCheckbox,
    CfInnerLoading,
    PreferenceListItem,
    PreferenceIcon,
    PreferencesPaymentSection,
  },
  emits: ["on-change"],
  props: {
    actions: Boolean,
  },
  data() {
    return {
      PREFERENCES_TYPES_MAP,
      isLoading: false,
      isSavingPreferences: false,
      isSavingPaymentPreference: false,
      notifyServicesList: [],
      isPaymentPreference: false,
      paymentPreferenceStatus: null,
      ppayServices: [],
      generalServices: [],
    };
  },
  watch: {
    selectedChannels: {
      deep: true,
      handler(newVal) {
        if (!this.actions) {
          //INVIA LA LISTA DELLE PREFERENZE AGGIORNATE
       
          this.$emit("on-change", this.enabledPreferences);
        }
      },
    },
  },
  computed: {
    allPush: {
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
    allEmail: {
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
    allSms: {
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
    user() {
      return this.$store.getters["getUser"];
    },
    userTaxCode() {
      return this.user?.codFisc ?? null;
    },
    comuniConfigServicesByCode() {
      let services = this.$store.getters["getComuniConfigDetailServices"];
      let servicesWithNotifications = services?.filter(
        (s) => !!s.codiceNotifiche
      );
      return servicesWithNotifications?.reduce((out, s) => {
        let code = s["codiceNotifiche"];

        out[code] = s["titolo"];
        return out;
      }, {});
    },
    servicesList() {
      return this.$store.getters["getNotifyServicesList"];
    },
    selectedChannels() {
      return [
        ...this.generalServices.map((service) => service._enabledChannels),
        ...this.ppayServices.map((service) => service._enabledChannels),
      ];
    },
    // LISTA DEI SERVIZI CHE SUPPORTANO LE MAIL
    supportedMailServices() {
      return this.notifyServicesList.filter((service) =>
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
      return this.notifyServicesList.filter((service) =>
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
      return this.notifyServicesList.filter((service) =>
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
      for (let i = 0; i < this.notifyServicesList.length; i++) {
        const service = this.notifyServicesList[i];
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
    isPaymentEnabled() {
      let assetCodeList = this.$store.getters["getAssetCodeList"];
      return assetCodeList?.includes(ASSETS_MAP.PAYMENTS);
    },
  },
  async created() {
    this.isLoading = true;
    if (!this.servicesList?.length) {
      await setNotifyPreferences();
    }
    this.notifyServicesList = [...this.servicesList];

    let preferencesList = [];

    let notifyPreferencesPromise = getNotifyPreferencesList(this.userTaxCode);
    let paymentPreferencesPromise = getReceiptStorageStatus(this.userTaxCode);
    try {
      let { data: list } = await notifyPreferencesPromise;
      preferencesList = list ?? [];
    } catch (e) {}

    this.notifyServicesList = this.mapServicePreferences(preferencesList);

    this.ppayServices = this.notifyServicesList?.filter((s) =>
      s.tag_list.includes("ppay")
    );

    this.generalServices = this.notifyServicesList?.filter(
      (s) => !s.tag_list.includes("ppay")
    );

    try {
      let { data } = await paymentPreferencesPromise;
      this.paymentPreferenceStatus = data;
      this.isPaymentPreference = this.paymentPreferenceStatus?.flag;
    } catch (e) {}

    this.isLoading = false;
    this.updateUserContacts();
  },
  methods: {
    mapServicePreferences(preferencesList) {
      let preferenceIndexedByServiceName = keyBy(
        preferencesList,
        (p) => p.service_name
      );
      let services = [...this.servicesList];

      services.forEach((service) => {
        service["_supportedChannels"] = mapPreferencesList(service.channels);

        let enabledChannels = {
          [PREFERENCES_TYPES_MAP.EMAIL]: false,
          [PREFERENCES_TYPES_MAP.SMS]: false,
          [PREFERENCES_TYPES_MAP.PUSH]: false,
        };
        let preference = preferenceIndexedByServiceName[service.name];

        if (preference && typeof preference.channels === "string") {
          enabledChannels = mapPreferencesList(preference.channels);
        }

        service["_enabledChannels"] = enabledChannels;

        service["title"] =
          this.comuniConfigServicesByCode[service.name] ?? service.name;
        service["tag_list"] = service.tags?.split(",");
        return service;
      });

      return services;
    },

    disableAllChannel(channel) {
      this.notifyServicesList.forEach((service) => {
        if (!isPreferenceChannelActive(service._supportedChannels, channel))
          return;
        service._enabledChannels[channel] = false;
      });
    },
    enableAllChannel(channel) {
      this.notifyServicesList.forEach((service) => {
        if (!isPreferenceChannelActive(service._supportedChannels, channel))
          return;

        service._enabledChannels[channel] = true;
      });
    },
    emitPreferences() {
      let enabledPreferences = {};
      for (let i = 0; i < this.notifyServicesList.length; i++) {
        const service = this.notifyServicesList[i];
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

    async onConfirm() {
      if (this.isSavingPreferences) return;

      this.isSavingPreferences = true;
      try {
        let responsePreferences = await setNotifyPreferencesList(
          this.userTaxCode,
          this.enabledPreferences
        );
        await this.updateUserContacts();
        await this.$store.dispatch("setNotificationInfo", {
          body: "Preferenze modificate con successo.",
          positive: true,
        });
      } catch (e) {
        await this.$store.dispatch("setNotificationInfo", {
          body: "Non è stato possibile modificare le preferenze, si prega di riprovare",
        });
      }

      this.isSavingPreferences = false;
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
    onChangePaymentPreferences(preferences) {
      this.ppayServices = preferences;
    },
    async updateUserContacts() {
      let notifyContacts = this.user?.contatti ?? {};
      notifyContacts["push"] = notifyContacts["push"] || {};

      let enabledServices = this.enabledPushServices?.map((s) => s.name);

      let servicesWithoutFcmToken = enabledServices.filter(
        (s) => !notifyContacts.push[s]?.length
      );

      if (!servicesWithoutFcmToken.length) return;

      const fcmTokens = [
        ...new Set([...Object.values(notifyContacts.push).flatMap((s) => s)]),
      ];

      let pushListNew = servicesWithoutFcmToken.reduce((out, s) => {
        out[s] = fcmTokens;
        return out;
      }, {});

      notifyContacts.push = { ...notifyContacts.push, ...pushListNew };

      try {
        let { data: contacts } = await saveUserContacts(
          this.userTaxCode,
          notifyContacts
        );
        this.$store.distpatch("setUserContacts", { contacts });
      } catch (e) {}
    },
  },
};
</script>
