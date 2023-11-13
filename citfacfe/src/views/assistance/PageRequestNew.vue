<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<script>
import InputTestuale from "../../components/form/InputTestuale.vue";
import FormBg from "../../components/form/FormBg.vue";
import InputSelect from "../../components/form/InputSelect.vue";
import InputTestualeArea from "../../components/form/InputTestualeArea.vue";
import InputFile from "../../components/form/InputFile.vue";
import InputCheckbox from "../../components/form/InputCheckbox.vue";
import CfPageSection from "../../components/generic/CfPageSection.vue";
import JustValidate from "just-validate";
import { isValidCF, login, toBase64 } from "../../services/utils";

import {
  createPartialTicket,
  createTicket,
  createTicketPublic,
  deletePartialTicket,
  getListaServizi,
  getListaTipologieServizio,
  getPartialTicketlist,
  getPrivacyText,
  updatePartialTicket,
} from "../../services/api";
import CheckOtp from "../../components/form/checkOtp.vue";
import ContactsSection from "../../components/generic/ContactsSection.vue";
import Breadcrumbs from "../../components/generic/Breadcrumbs.vue";
import TitleSection from "../../components/generic/TitleSection.vue";
import {
  LOGIN_URL,

  PARTIAL_TICKET,
  TICKET,
  TICKET_NO_AUTH,
} from "../../services/business-logic";
import NotificationToast from "../../components/notify/NotificationToast.vue";
import { ASSETS_MAP, MOBILE_PHONE_PREFIX_ITALY } from "../../services/config";
import CfModal from "../../components/generic/CfModal.vue";
import CfSelect from "../../components/form/CfSelect.vue";

export default {
  name: "PageRequestNew",
  components: {
    CfModal,
    NotificationToast,
    TitleSection,
    Breadcrumbs,
    ContactsSection,
    CheckOtp,
    InputCheckbox,
    InputFile,
    InputTestualeArea,
    InputSelect,
    FormBg,
    InputTestuale,
    CfSelect,
    CfPageSection,
  },
  data() {
    return {
      LOGIN_URL,
      breadcrumbsLinks: [
        { url: "", name: "Assistenza" },
      ],
      isLoadingPolicy: false,
      privacyText: "",
      file: null,
      notificationTitle: null,
      notificationMessage: null,
      startTimer: 1800000,
      isLoading: false,
      validation: null,
      dettagli: null,
      servizioSelezionato: null,

      tipologiaSelezionata: null,
  
      nome: null,
      cognome: null,
      cf: null,
      telefono: null,
      email: null,
      listaServizi: [],
      listaTipologie: [],
      isConfirmOtpModalVisible: false,
      currentPartialTicket: null,
      partialTickettBody: null,
      defaultServiceId: null,
      fileSizeError: false,
    };
  },

  computed: {
    assetsList() {
      return this.$store.getters["getAssetsList"];
    },
    prefillTelephoneLabel() {
      if (!this.prefillMobile) return "";
      let pattern = "^(" + MOBILE_PHONE_PREFIX_ITALY + ")+";
      let regex = new RegExp(pattern, "g");
      return this.prefillMobile.replace(regex, "");
    },
    getDevLogout() {
      return this.$store.getters["getDevLogout"];
    },
    isPartialTicket() {
      return !!this.$route.query.idBozza;
    },
    isUserAuth() {
      if (this.user && !this.getDevLogout) {
        return true;
      } else return false;
    },
    prefillService() {
      if (this.getDevLogout) {
        return null;
      }
      if (this.currentPartialTicket) {
        return this.partialTickettBody?.service_catalog_item_id;
      } else if (this.defaultServiceId) {
        return parseInt(this.defaultServiceId);
      }

      return null;
    },
    prefillType() {
      if (this.getDevLogout) {
        return null;
      }
      if (this.currentPartialTicket) {
        return this.partialTickettBody?.service_catalog_item_id;
      }
      return null;
    },
    prefillDetails() {
      if (this.getDevLogout) {
        return null;
      }
      return this.partialTickettBody?.article.body;
    },
    prefillFile() {
      if (this.getDevLogout) {
        return null;
      }
      if (this.partialTickettBody?.article?.attachments) {
        return this.partialTickettBody.article.attachments[0];
      }
      return null;
    },
    prefillEmail() {
      if (this.getDevLogout) {
        return null;
      }
      if (!this.currentPartialTicket) {
        return this.userMail;
      }
      return this.partialTickettBody?.customer?.email;
    },
    prefillMobile() {
      if (this.getDevLogout) {
        return null;
      }
      if (!this.currentPartialTicket) {
        return this.userPhone;
      }
      return this.partialTickettBody?.customer?.mobile;
    },
    comuniServices() {
      return this.$store.getters["getComuniConfigServices"];
    },
    user() {
      return this.$store.getters["getUser"];
    },
    userMail() {
      return this.user?.contatti?.email || null;
    },
    userPhone() {
      return this.user?.contatti?.sms || null;
    },
    prefillUserName() {
      if (this.getDevLogout) {
        return null;
      }
      if (this.currentPartialTicket) {
        return this.partialTickettBody?.customer?.firstname;
      }

      return this.user?.nome;
    },
    prefillUserCognome() {
      if (this.getDevLogout) {
        return null;
      }
      if (this.currentPartialTicket) {
        return this.partialTickettBody?.customer?.lastname;
      }
      return this.user?.cognome;
    },
    prefillUserCf() {
      if (this.getDevLogout) {
        return null;
      }
      if (this.currentPartialTicket) {
        return this.partialTickettBody?.customer?.codice_fiscale;
      }

      return this.user?.codFisc;
    },
   
    isTypologyListDisabled() {
      if (this.listaTipologie.length === 0) {
        return true;
      }
      return false;
    },
    servizioSelezionatoDesc() {
      return (
        this.listaServizi.find((a) => a.id === this.servizioSelezionato)
          ?.name ?? null
      );
    },
  },

  async created() {

  },
  methods: {
    async goToLogin() {
      let path = encodeURIComponent(this.$route.path)
      login(path);
    },
    goBack() {
      this.$router.go(-1);
    },
    getDefaultService() {
      let id = this.$route.query?.servizio;
      const service = this.listaServizi?.find((s) => s.id.toString() === id);
      if (service) {
        this.servizioSelezionato = service.id;
      }
    },
    async createNotification(title, message) {
      await this.$store.dispatch("setNotificationInfo", {
        title: title,
        body: message,
      });
    },
    goToFeedback(type) {
      let query = { feedbackType: type };
      if (type === TICKET_NO_AUTH) {
        query.email = this.email;
      }

      this.$router.push({ name: "feedback", query: query });
    },
    async buildTicketPayload() {
      
      let title = this.listaServizi.find(
        (a) => a.id === parseInt(this.servizioSelezionato)
      )?.name;
    let mobilePhone = null
    if(this.telefono?.length>0){
      mobilePhone = "0039" + this.telefono
    }
      let payload = {
        article: {
          body: this.dettagli ?? null,
          from: this.email ?? null,

         
          type_id: 13,
         
          content_type: "text/html",
        },
        title: title ?? "",
        customer: {
          email: this.email ?? null,
          mobile:  mobilePhone,
          firstname: this.nome ?? null,
          lastname: this.cognome ?? null,
          codice_fiscale: this.cf?.toUpperCase() ?? null,
        },
        
        group_id: 2,
    
        priority_id: 4,
        
        utente_riconosciuto: this.isUserAuth ? 1 : 0,
        
        type_id: 1,
      };

      if (this.servizioSelezionato) {
        payload.service_catalog_item_id = parseInt(this.servizioSelezionato);
      }
      if (this.tipologiaSelezionata) {
        payload.service_catalog_sub_item_id = parseInt(
          this.tipologiaSelezionata
        );
      }
      if (this.file) {
        payload.article.attachments = [this.file];
      }

      return payload;
    },
    async createPartialTicket() {
      let isValidate = false;
      await this.validation.revalidateField("#inputSelect1").then((isValid) => {
        isValidate = isValid;
      });
      if (!isValidate) {
        this.validation.showErrors({ "#inputSelect1": "Camèpo obbligatorio" });
        return;
      }
      this.isLoading = true;
      scroll(0, 0);
      try {
    
        let idAsset = this.assetsList.find(
          (a) => a.codice === ASSETS_MAP.ASSISTANCE
        )?.id;

        let payload = await this.buildTicketPayload();

        let payloadString = {
          body: JSON.stringify(payload),
          idAsset: idAsset,
        };

        //Se si tratta di una bozza precedentemente creata l'aggiorno e non creo una nuova
        if (this.isPartialTicket) {
          let idBozza = this.$route.query.idBozza;
          let promise = await updatePartialTicket(idBozza, payloadString);
        } else {
          let promise = await createPartialTicket(payloadString);
        }

        this.goToFeedback(PARTIAL_TICKET);
      } catch (e) {
        let notificationTitle = "Attenzione";
        let notificationMessage = "Non è stato possibile salvare la bozza";
        this.createNotification(notificationTitle, notificationMessage);
      }
      this.isLoading = false;
    },

    createAriaLabel(label, value, small) {
      if (value) {
        return label + " " + value + " " + small;
      }
      return label + " " + small;
    },

    //PER GENERARE UN NUOVO OTP
    restartOtp() {
      this.createOtp();
    },

    //CREA IL PRIMO OTP
    createOtp() {
     
      this.isConfirmOtpModalVisible = true;

     
      let myModal = new bootstrap.Modal(
        document.getElementById("otpModal"),
        {}
      );

      myModal.show();
    },

    //VISUALIZZA LA MODALE DI CONFERMA CREAZIONE OTP
    openOtpDialog() {
      let myModal = new bootstrap.Modal(
        document.getElementById("startOtpModal"),
        {}
      );
      myModal.show();
    },

    //VISUALIZZA LA MODALE DI PRIVACY
    async openPrivacyDialog() {
      let myModal = new bootstrap.Modal(
        document.getElementById("privacyModal"),
        {}
      );
      myModal.show();
      try {
        this.isLoadingPolicy = true;
        let promise = await getPrivacyText();
        this.privacyText = promise.data;
      } catch (e) {}
      this.isLoadingPolicy = false;
    },

    //AGGIORNA IL VALORE DEL INPUT FILE
    async updateFile(file) {
      this.file = null;

      let type = null;
      let fileName = null;
      let base64 = null;
      
      
      if (file) {
        
        if (!file.data) {
          type = file?.type;
          fileName = file?.name;
          base64 = await toBase64(file);
        } else {
          
          fileName = file?.filename;
          type = file?.mimetype;
          base64 = file?.data;
        }

    
        base64 = base64.substring(base64.indexOf(",") + 1);
      }

      this.file = {
        filename: fileName,
        data: base64,
        "mime-type": type,
      };
    },

   
    async sendTicket(otp, interval) {
      scroll(0, 0);

      let params = {};
      if (otp) {
        (params.otp = otp), (params.cf = this.cf.toUpperCase());
        params.richiesta = "EMAIL";
      }
      let payload = await this.buildTicketPayload();

      try {
        this.isLoading = true;

       
        if (this.isUserAuth) {
          let promise = await createTicket(payload, params);
        } else {
          let promise = await createTicketPublic(payload, params);
        }

      
        if (interval) {
          await clearInterval(interval);
        }

      
        if (this.isPartialTicket) {
          let idBozza = this.$route.query.idBozza;
          let promise = await deletePartialTicket(idBozza);
        }

   
        if (this.isUserAuth) {
          this.goToFeedback(TICKET);
        } else {
          this.goToFeedback(TICKET_NO_AUTH);
        }
      } catch (e) {
        let notificationTitle = "Attenzione";
        let notificationMessage = "";
        if (otp) {
          notificationMessage =
            "Non è stato possibile inviare la richiesta, si prega di generare un nuovo codice Otp e riprovare";
        } else {
          notificationMessage =
            "Non è stato possibile inviare la richiesta, si prega di riprovare";
        }
        this.createNotification(notificationTitle, notificationMessage);
      }
      this.isLoading = false;
    },

   
    setValidazione() {
   
      let validation = this.validation;
      validation.addField("#inputText1", [
        {
          rule: "required",
          errorMessage: "Campo obbligatorio",
        },
      ]);
      validation.addField("#inputText2", [
        {
          rule: "required",
          errorMessage: "Campo obbligatorio",
        },
      ]);
      validation.addField("#inputText3", [
        {
          rule: "required",
          errorMessage: "Campo obbligatorio",
        },
        {
          validator: (value) => isValidCF(value),
          errorMessage: " Inserire un codice fiscale valido",
        },
      ]);
      validation.addField("#inputText4", [
        {
          rule: "required",
          errorMessage: "Campo obbligatorio",
        },
        {
          rule: "email",
          errorMessage: "Inserire mail valida",
        },
      ]);
      validation.addField("#inputText5", [
       
        {
          rule: "number",
          errorMessage: "Inserire un numero valido",
        },
        {
          rule: "maxLength",
          value: 10,
          errorMessage: "Inserire un numero valido",
        },
        {
          rule: "minLength",
          value: 9,
          errorMessage: "Inserire un numero valido",
        },
      ]);
      validation.addField("#inputSelect1", [
        {
          rule: "required",
          errorMessage: "Campo obbligatorio",
        },
      ]);
      validation.addField("#inputSelect2", [
        {
          rule: "required",
          errorMessage: "Campo obbligatorio",
        },
      ]);
      validation.addField("#inputTextArea1", [
        {
          rule: "required",
          errorMessage: "Campo obbligatorio",
        },
      ]);
      validation.addField("#inputCheckbox1", [
        {
          rule: "required",
          errorMessage: "Dichiara di aver letto l'informativa",
        },
      ]);

      validation.addField("#inputFile1", [
        {
          validator: (value) => !this.fileSizeError,
          errorMessage: "Dimensione file non valida",
        },
      ]);
      //SE LA VALIDAZIONE HA SUCCESSO
      validation.onSuccess((event) => {
        if (!this.isUserAuth) {
          this.openOtpDialog();
        } else {
          this.sendTicket();
        }
      });
    },

 
    updateDettagli(value) {
      this.dettagli = value;
    },

  
    async updateServizioSelezionato(opz) {
    
     
      this.listaTipologie = [];
      
        setTimeout(() => {
          if (this.servizioSelezionato) {
          this.updateTypoligyList();
        }
        }, 500);
     
    },

      async updateTypoligyList() {
      try {
        let listaTipologiePromise = await getListaTipologieServizio(
          this.servizioSelezionato
        );
        this.listaTipologie = listaTipologiePromise.data;
      } catch (e) {
        let notificationTitle = "Attenzione";
        let notificationMessage = "Problema nel caricamento liste";

        this.createNotification(notificationTitle, notificationMessage);
      }
    },


 
    updateNome(value) {
      this.nome = value;
    },

   
    updateCognome(value) {
      this.cognome = value;
    },


    updateCf(value) {
      this.cf = value;
    },

    //AGGIORNO IL TELEFONO
    updateTelefono(value) {
      this.telefono = value;
    },

    //AGGIORNO LA EMAIL
    updateEmail(value) {
      this.email = value;
    },

    async onValidFileSize(val) {
      this.fileSizeError = !val;

      let isValidSize = false;
      await this.validation.revalidateField("#inputFile1").then((isValid) => {
        isValidSize = isValid;
      });

      if (!isValidSize) {
        this.validation.showErrors({
          "#inputFile1": "Dimensione file non valida",
        });
        return;
      }
    },
  },

  async mounted() {
    scroll(0, 0);

    this.isLoading = true;
    //TRY PER POPOLARE LE LISTE
    try {
      let listaerviziPromise = await getListaServizi();
      this.listaServizi = listaerviziPromise.data;
    } catch (e) {
      let notificationTitle = "attenzione";
      let notificationMessage = "problema nel caricamento liste";

      this.createNotification(notificationTitle, notificationMessage);
    }

    if (this.isPartialTicket) {
      try {
      
        let idAsset = this.assetsList.find(
          (a) => a.codice === ASSETS_MAP.ASSISTANCE
        )?.id;
        let currentPartialTicketId = this.$route.query.idBozza;
        let params = { asset: idAsset };
        let partialTicketListPromise = await getPartialTicketlist(params);
        let partialTicketList = partialTicketListPromise.data;

        this.currentPartialTicket = partialTicketList.find(
          (a) => a.id === parseInt(currentPartialTicketId)
        );

        if (this.currentPartialTicket) {
          this.partialTickettBody = JSON.parse(this.currentPartialTicket?.body);
        }
      } catch (e) {}
    }
  
    if (
      (this.$store.getters["getUser"] || this.currentPartialTicket) &&
      !this.getDevLogout
    ) {
      this.cf = this.prefillUserCf;
      this.cognome = this.prefillUserCognome;
      this.nome = this.prefillUserName;
      this.email = this.prefillEmail;
      this.telefono = this.prefillTelephoneLabel;
      this.dettagli = this.prefillDetails;
      this.file = this.prefillFile;

      //CONTROLLO CONTATTI
    }
    this.defaultServiceId = this.$route.query?.servizio;
    this.servizioSelezionato = this.prefillService
   await  this.updateServizioSelezionato();
   this.tipologiaSelezionata = this.prefillType;
  
    this.isLoading = false;

    //DOPO CHE IL FORM è RENDERIZZATO, SETTO LA VALIDAZIONE
    await this.$nextTick(() => {
      const validation = new JustValidate("#form");
      this.validation = validation;
      this.setValidazione();
    });
  },
};
</script>

<template>
  <main id="main-container" class="container mb-5">
    <!--    COMPONENTE BREADCRUMBS-->
    <breadcrumbs :links="breadcrumbsLinks"></breadcrumbs>

    <!--    COMPONENTE TITOLO-->
    <title-section title="Richiesta assistenza"></title-section>

    <cf-page-section class="my-4 mx-1">
      <form
        :style="[
          !isLoading
            ? { visibility: 'visible' }
            : { visibility: 'hidden', height: '0px' },
        ]"
        v-if="!isLoading"
        action="#"
        id="form"
        enctype="multipart/form-data"
      >
        <div class="row py-lg-2">
          <div class="col-12">
            I campi contraddistinti dal simbolo (*) sono obbligatori
          </div>
          <template v-if="!isUserAuth">
            <div class="col-12 h6">
              Hai già un'identità digitale SPID o CIE?
              <a
                class="back-button"
                :href="LOGIN_URL"
                @click.prevent="goToLogin"
                >Accedi</a
              >
            </div>
          </template>
        </div>
        <form-bg title="Richiedente">
          <div class="row g-0">
            <div class="col-12">
              <!--              INPUT DEL NOME-->
              <input-testuale
                :aria-label="
                  createAriaLabel('Nome*', nome, 'Inserisci il tuo nome')
                "
                id="1"
                @updateText="updateNome"
                label-text="Nome*"
                more-info="Inserisci il tuo nome"
                :prefill="prefillUserName"
                :readonly="isUserAuth"
              ></input-testuale>
            </div>
            <div class="col-12">
              <!--              INPUT DEL COGNOME-->
              <input-testuale
                :aria-label="
                  createAriaLabel(
                    'Cognome*',
                    cognome,
                    'Inserisci il tuo cognome'
                  )
                "
                id="2"
                @updateText="updateCognome"
                label-text="Cognome*"
                more-info="Inserisci il tuo cognome"
                :prefill="prefillUserCognome"
                :readonly="isUserAuth"
              ></input-testuale>
            </div>
            <div class="col-12">
              <!--              INPUT DEL cf-->

              <input-testuale
                :aria-label="
                  createAriaLabel(
                    'Codice fiscale*',
                    cf,
                    'Inserisci il tuo codice fiscale'
                  )
                "
                id="3"
                @updateText="updateCf"
                label-text="Codice fiscale*"
                more-info="Inserisci il tuo codice fiscale"
                :is-uppercase="true"
                :prefill="prefillUserCf"
                :max-lenght="16"
                :readonly="isUserAuth"
              ></input-testuale>
            </div>
            <div class="col-12">
              <!--              INPUT DELLA MAIL-->
              <input-testuale
                :aria-label="
                  createAriaLabel('Email*', email, 'Inserisci la tua email')
                "
                id="4"
                @updateText="updateEmail"
                label-text="Email*"
                more-info="Inserisci la tua email"
                :prefill="prefillEmail"
              ></input-testuale>
            </div>
            <div class="col-12">
              <!--              INPUT DEL TELEFONO-->
              <input-testuale
                :aria-label="
                  createAriaLabel(
                    'Telefono',
                    telefono,
                    'Inserisci il tuo telefono'
                  )
                "
                id="5"
                @updateText="updateTelefono"
                label-text="Telefono"
                more-info="Inserisci il tuo telefono"
                :prefill="prefillTelephoneLabel"
                :prepend="true"
              >
                <template #prepend>+39</template>
              </input-testuale>
            </div>
          </div>
        </form-bg>

        <form-bg title="Richiesta">
          <div class="row">
            <div class="col-12">
              <!--              SELECT DEI SERVIZI-->

              <cf-select
                id="1"
                v-model="servizioSelezionato"
                :option-list="listaServizi"
                label-text="Servizio"
                required
                option-value="id"
                option-label="name"
                more-info="Seleziona il servizio per cui vuoi richiedere assistenza"
                @on-select="updateServizioSelezionato"
              />

            </div>

            <div class="col-12">
              <!--              INPUT DELLA TIPOLOGIA DISATTIVA SE NON è SELEZIONATO UN SERVIZIO-->

              <cf-select
                id="2"
                v-model="tipologiaSelezionata"
                :option-list="listaTipologie"
                :disabled="isTypologyListDisabled"
                label-text="Tipologia problematica"
                required
                option-value="id"
                option-label="name"
                more-info="Seleziona la categoria del servizio per cui vuoi richiedere assistenza"
              />

             
            </div>
            <div class="col-12">
              <!--              IMPUT LIBERO-->
              <input-testuale-area
                id="1"
                :aria-label="
                  createAriaLabel(
                    'Dettagli*',
                    dettagli,
                    'Inserire al massimo 600 caratteri'
                  )
                "
                @updateTextArea="updateDettagli"
                label-text="Dettagli*"
                more-info="Inserire al massimo 600 caratteri"
                :prefill="prefillDetails"
              >
              </input-testuale-area>
            </div>
            <div class="col-12">
              <!--              CARICAMENTO FILE FACOLTATIVO-->

              <input-file
                id="1"
                label-text="Upload allegato"
                @update-file="updateFile"
                :prefill="file"
                max-size="2048"
                @valid-size="onValidFileSize"
                downloadable
              >
              </input-file>
            </div>
          </div>
        </form-bg>

        <!--        LINK E CHECKBOX PER PRIVACY-->
        <div class="row py-lg-2">
          <div class="col-12 pb-2">
            Per i dettagli sul trattamento dei dati personali consulta

            <a class="link" @click="openPrivacyDialog"
              >l'informativa sulla privacy</a
            >
          </div>
          <div class="col-12">
            <input-checkbox
              id="1"
              labelText="Ho letto e compreso l'informativa sulla privacy"
            ></input-checkbox>
          </div>
        </div>

        <!--      SEZIONE TASTI-->
        <section class="container px-5">
          <div
            class="row full-width justify-content-between g-3 pb-2 my-2 mx-md-5"
          >
            <div class="col-md-auto col-12 text-center">
              <svg class="icon ms-1">
                <use
                :xlink:href="$svgs.getSpriteSvgUrl('it-arrow-left-triangle')"
              
                ></use>
              </svg>
              <a class="back-button" @click="goBack">Indietro</a>
            </div>
            <template v-if="isUserAuth">
              <div class="col-md-auto col-12">
                <button
                  @click="createPartialTicket"
                  type="button"
                  class="w-100 btn btn-outline-primary"
                >
                  Salva richiesta
                </button>
              </div>
            </template>
            <div class="col-md-auto col-12">
              <button
                aria-label="invia richiesta"
                id="submit-btn"
                class="w-100 btn btn-primary"
              >
                Invia
              </button>
            </div>
          </div>
        </section>
      </form>

      <template v-if="isLoading">
        <div class="row py-2 justify-content-center full-width overlay">
          <div class="col-auto">
            <div class="progress-spinner progress-spinner-active">
              <span class="visually-hidden">Caricamento...</span>
            </div>
          </div>
        </div>
      </template>
    </cf-page-section>

    <notification-toast
      :notification-title="notificationTitle"
      :notification-message="notificationMessage"
      id="notification"
    ></notification-toast>
  

    <!--    SEZIONE FISSA DEI CONTATTI-->

    <!--    MODALE PRIVACY-->
    <cf-modal title="Privacy" no-action id="privacyModal" ref="privacyModal">
      <template v-if="!isLoadingPolicy">
        <div class="modal-body">{{ privacyText }}</div>
      </template>
      <template v-if="isLoadingPolicy">
        <div class="row py-2 justify-content-center full-width overlay">
          <div class="col-auto">
            <div class="progress-spinner progress-spinner-active">
              <span class="visually-hidden">Caricamento...</span>
            </div>
          </div>
        </div>
      </template>
      <div class="row justify-content-end gap-2 py-3">
        <div class="col-md-auto col-12">
          <button
            type="button"
            class="btn btn-outline-primary"
            data-bs-dismiss="modal"
          >
            Chiudi
          </button>
        </div>
      </div>
    </cf-modal>

    <!--    MODALE DI CONFERMA PER CREAZIONE OTP-->
    <cf-modal
      no-action
      id="startOtpModal"
      ref="startOtpModal"
      title="Nuova richiesta di assistenza"
    >
      <div>
        Per confermare la ricchiesta ti verrà inviato un codice di verifica
        all'indirizzo <strong>{{ email }}</strong>
      </div>

      <div class="row justify-content-end gap-2 py-3">
        <div class="col-md-auto col-12">
          <button
            type="button"
            class="btn btn-outline-primary w-100"
            data-bs-dismiss="modal"
          >
            Annulla
          </button>
        </div>
        <div class="col-md-auto col-12">
          <button
            @click="createOtp"
            type="button"
            class="btn btn-primary w-100"
            data-bs-dismiss="modal"
          >
            Conferma
          </button>
        </div>
      </div>
    </cf-modal>


    <!--    MODALE DELLA CREAZIONE E CONTROLLO OTP-->
    <check-otp
      class="mb-0"
      id="6"
      label-text="Codice di conferma*"
      :start-timer="startTimer"
      :modal-visible="isConfirmOtpModalVisible"
      :cf="cf"
      :email="email"
      @sendRequest="sendTicket"
    >
    </check-otp>
  </main>
  <contacts-section></contacts-section>
</template>


