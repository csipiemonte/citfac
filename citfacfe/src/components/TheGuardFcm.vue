<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="the-guard-fcm" >
    <slot></slot>
    <!-- MODAL RICHIESTA PERMESSI -->
    <!-- ----------------------------------------------------------------------------------------------------------- -->
  
    <template v-if="notificationsEnabled">
      <div
        id="fcmPermissionModal"
        aria-labelledby="fcmModalTitle"
        class="modal fade"
        role="dialog"
        tabindex="-1"
      >
        <div class="modal-dialog" role="document">
          <div class="modal-content">
            <!-- Modal Header -->
            <div class="modal-header">
              <h5 id="fcmModalTitle" class="modal-title">Notifiche Push</h5>
            </div>
            <div class="modal-body">
              <strong>Città facile</strong> vorrebbe mandarti delle notifiche in
              caso di eventi importanti.

              <div class="row justify-content-between gap-2 py-3 mx-2">
                <div class="col-md-auto col-12">
                  <button
                    type="button"
                    class="btn btn-outline-primary justify-content-center w-100"
                    data-bs-dismiss="modal"
                    @click="hideUiPermission"
                  >
                    No, grazie
                  </button>
                </div>
                <div class="col-md-auto col-12">
                  <button
                    @click="onUiPermissionsGranted"
                    type="button"
                    class="btn btn-primary justify-content-center w-100"
                    data-bs-dismiss="modal"
                  >
                    OK
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script>
import { initializeApp } from "firebase/app";
import {
  getMessaging,
  getToken,
  onMessage,
  isSupported,
} from "firebase/messaging";
import CfModal from "./generic/CfModal.vue";
import { ASSETS_MAP } from "../services/config";

const STORAGE_KEY = "CITFAC_PWA_BANNER_FCM_DO_NOT_ASK";
export default {
  components: { CfModal },
  name: "TheGuardFcm",
  data() {
    return {
      messaging: null,
      isUiPermissionsVisible: false,
      serviceWorker: null,
      fcmPermissionModal: null,
      registrationSW: null,
      fcmConfig:null,
      fcmVapidKey:null
    };
  },
  computed: {
    user() {
      return this.$store.getters["getUser"];
    },
    userContacts() {
      return this.user?.contatti ?? null;
    },
   

    fcmToken() {
      return this.$store.getters["getFcmToken"];
    },
    watchable() {
      return {
        fcmToken: this.fcmToken,
        user: this.user,
        notifyContacts: this.userContacts,
      };
    },
    comuneConfig() {
      return this.$store.getters["getComuniConfig"];
    },
    comuneCode() {
      return this.comuneConfig?.codiceEnte;
    },
    storageKey() {
      return `CITFAC_${this.comuneCode}_BANNER_FCM_DO_NOT_ASK`;
    },
    notificationsEnabled() {
      return !!this.assetCodeList.includes(ASSETS_MAP.NOTIFICATIONS);
    },
    assetCodeList() {
      return this.$store.getters["getAssetCodeList"];
    },

  },
  async created() {
    if (!this.notificationsEnabled) return;
  
    let config = window.getFirebaseConfig()
    if(!config) return
    this.fcmConfig = config.firebase
    this.fcmVapidKey = config.vapidkey
 
 
    if (
      "serviceWorker" in navigator &&
      "Notification" in window &&
      window.Notification.permission !== "denied"
    ) {
      await this.init();

      
      this.$watch(() => this.watchable, {
        immediate: true,
        handler(val, oldVal) {
          console.debug("[FCM] watchable", { val, oldVal });
          let { fcmToken, user, notifyContacts } = val;

          if (!fcmToken && user && notifyContacts) {
            if (window.Notification.permission === "granted") {
              
              this.onPermissionGranted();
            } else {
             
              let isPermissionAsked = sessionStorage.getItem(this.storageKey);
              console.log("isPermissionAsked", isPermissionAsked);
              if (!isPermissionAsked) {
                this.askPermissions();
              }
            }
          }
        },
      });
    }
  },
  methods: {
    async init() {
      // Prendiamo il SW registrato
      return navigator.serviceWorker.ready.then((sw) => {
        console.log("[FCM] serviceWorker.ready:", { sw });
        this.registrationSW = sw
        const config = this.fcmConfig;
        const key = this.fcmVapidKey;

        // Inizializza l'app di firebase con la configurazione
        console.debug("[FCM] Initialize with config:", { config });
        const firebase = initializeApp(config);

        // Otteniamo un'istanza della FCM (Firebase Cloud Messaging)
        this.messaging = getMessaging(firebase);

  

        // Imposta un handler per le notifiche push che viene invocata quando:
        //  - un messaggio arriva in foreground
        //  - un utente clicca su una notifica creata da `setBackgroundMessageHandler` nel Service Worker.
        //    quindi si tratta di un Data Messages
        onMessage(this.messaging, (payload) => {
          let actions = [];
          let url = payload?.notification?.click_action ?? null;

          // Se nella notifica è presente la call to action => aggiungiamo il bottone per invocare la call to action
          if (url) {
            actions.push({
              label: "vai",
              handler: () => {
                window.location.assign(url);
              },s
            });
          }
          let notification = payload?.notification || payload?.data;
          const notificationTitle = payload.notification.title;
          const notificationOptions = {
            body: notification.body,
            multiLine: true,
            color: "info",
            position: "bottom-right",
            timeout: 5000,
            actions,
          };

         this.registrationSW.showNotification(
            notificationTitle,
            notificationOptions
          );
        });
      });
    },

    onTokenError(err) {
      console.error("[FCM] Token error:", { err });
    },
    askPermissions() {
      console.debug("[FCM] Asking UI permission to user");
      this.fcmPermissionMOdal = new bootstrap.Modal(
        document.getElementById("fcmPermissionModal"),
        {}
      );

      let modal = document.querySelector("#fcmPermissionModal");
      this.fcmPermissionModal = bootstrap.Modal.getOrCreateInstance(modal);
      this.fcmPermissionModal.show();
    },
    hideUiPermission() {
      console.debug("[FCM] hideUiPermission");
      sessionStorage.setItem(this.storageKey, true);
    },
    onUiPermissionsGranted() {
      this.hideUiPermission();
      console.debug("[FCM] Asking permission to user Granted");

      Notification.requestPermission()
        .then(this.onPermissionGranted)
        .catch(this.onPermissionDenied);
    },
    onPermissionDenied(err) {
      console.debug("[FCM] Permission denied: ", { err });
    },
    onPermissionGranted(result) {
      console.debug("[FCM] Permission granted");
      // console.debug("[FCM] Listening to FCM Token refresh...");
      // this.messaging.onTokenRefresh(this.onToken);

      console.debug("[FCM] FCM getting token");

      getToken(this.messaging, {
        vapidKey: this.fcmVapidKey,
        serviceWorkerRegistration: this.registrationSW,
      })
        .then(this.onToken)
        .catch(this.onTokenError);
    },
    onToken(fcmToken) {
      console.debug("[FCM] Token retrieved", { fcmToken });
      if (fcmToken) {
        this.$store.dispatch("setFcmToken", { fcmToken });
      }
    },
  },
};
</script>


