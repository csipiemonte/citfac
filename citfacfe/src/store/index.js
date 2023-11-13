/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 */

import { createStore } from "vuex";
import {
  getAppointmentList,
  getDebtPositions,
  getMoonInstances,
  getTicketList,
  saveUserContacts,
} from "../services/api";
import { LIMIT_ACTVITIES, MAINTENANCE_HASH_KEY } from "../services/config";

export default createStore({
  state: {
    moonAuth: null,
    moonConfig: null,
    user: null,
    notifyServicesList: null,
    comuniConfig: null,
    comuniConfigServices: null,
    comuniConfigDetailServices: null,
    comuniConfigMenu: null,
    devLogout: false,
    notificationInfo: {
      title: "Attenzione",
      body: "",
      positive: false,
    },
    ticketList: {
      tickets: [],
      nPage: 0,
    },
    instanceList: {
      instances: [],
      total: 0,
    },
    messageList: {
      messages: [],
      unread: 0,
    },
    paymentTypeList: [],
    appointmentList: [],
    paymentList: { payments: [], total: 0 },
    activeService: null,
    fcmToken: null,
    registrationSW: null,
    maintenanceHash: null,
  },
  getters: {
    getUser: (state) => state.user,
    getMoonAuth: (state) => state.moonAuth,
    getMoonConfig: (state) => state.moonConfig,
    getMoonIdToken: (state) => state.moonConfig?.idMoonToken,
    getNotifyServicesList: (state) => state.notifyServicesList ?? [],
    getComuniConfig: (state) => state.comuniConfig,
    getComuniConfigServices: (state) => {
      let services = [...state.comuniConfigServices];
      const details = state.comuniConfigDetailServices;
      services.forEach((service) => {
        service.dettaglio = details.find(
          (detail) => detail.idServizio === service.id
        );
        return service;
      });

      return services;
    },
    getComuniConfigDetailServices: (state) => state.comuniConfigDetailServices,
    getComuniConfigMenu: (state) => state.comuniConfigMenu,
    getDevLogout: (state) => state.devLogout,
    getNotificationInfo: (state) => state.notificationInfo,
    getTicketList: (state) => state.ticketList,
    getInstanceList: (state) => state.instanceList,
    getMessageList: (state) => state.messageList,
    getPaymentTypeList: (state) => state.paymentTypeList,
    getActiveService: (state) => state.activeService,
    getAssetsList: (state) => state.comuniConfig?.assets ?? [],
    getAssetCodeList: (state) => {
      let assetList = state.comuniConfig?.assets ?? [];
      return assetList?.map((a) => a.codice);
    },
    getFcmToken: (state) => {
      return state.fcmToken;
    },
    getFcmVapidKey: (state) => {
      return state.comuniConfig?.firebase?.vapidKey;
    },
    getFcmConfig: (state) => {
      let firebase = { ...state.comuniConfig?.firebase };
      if (firebase) {
        delete firebase.vapidKey;
      }

      return firebase;
    },
    getRegistrationSW: (state) => {
      return state.registrationSW;
    },
    getAppointmentList: (state) => {
      return state.appointmentList ?? [];
    },
    getPaymentList: (state) => {
      return state.paymentList;
    },
    getMaintenanceHash: (state) => {
      return state.maintenanceHash;
    },
  },
  actions: {
    setUser(context, { user }) {
      context.commit("setUser", { user });
    },

    async setNotifyServicesList(context, { list }) {
      context.commit("setNotifyServicesList", { list });
    },
    setUserContacts(context, { contacts }) {
      context.commit("setUserContacts", { contacts });
    },
    async setNotificationInfo(context, { title, body, positive }) {
      await context.commit("setNotificationInfo", { title, body, positive });
      const myNotification = new bootstrap.Notification(
        document.getElementById("notification-toast"),
        { timeout: 10000 }
      );
      myNotification.show();
    },

    async resetNotificationInfo(context) {
      await context.commit("setNotificationInfo", {
        title: "Attenzione",
        body: "",
        positive: false,
      });
    },
    async setTicketList(context, params) {
      if (!params) {
        let user = context.getters["getUser"];
        let taxCode = user?.codFisc;
        params = {
          per_page: LIMIT_ACTVITIES,
          page: 1,
          filter: `{"customer.codice_fiscale":{"eq": "${taxCode}"}}`,
        };
      }

      try {
        let { data: tickets } = await getTicketList({ params });
        let nPage = params.per_page / LIMIT_ACTVITIES;
        context.commit("setTicketList", { tickets, nPage });
      } catch (e) {
        context.dispatch("setNotificationInfo", {
          body: "Non è stato possibile caricare l'elenco delle richieste di assistenza",
        });
      }
    },

    setMessagesUnread(context, total) {
      context.commit("setMessagesUnread", total);
    },
    setMessageList(context, { messages, messagesUnread }) {
      context.commit("setMessageList", { messages, messagesUnread });
    },
    onreadMessage(context, messageId) {
      context.commit("onreadMessage", messageId);
    },
    setPaymentTypeList(context, { types }) {
      context.commit("setPaymentTypeList", { types });
    },

    setActiveService(context, { service }) {
      context.commit("setActiveService", { service });
    },
    async setFcmToken(context, { fcmToken }) {
      context.commit("setFcmToken", { fcmToken });
      console.debug("fcmToken", fcmToken);

      try {
        let user = context.getters["getUser"];
        let taxCode = user?.codFisc;

        let notifyContacts = user?.contatti;

        notifyContacts = notifyContacts || {};
        let services = context.getters["getNotifyServicesList"];
      
        let servicesWithoutFcmToken = services.filter((s) => {
          let fcmTokenList = notifyContacts?.push?.[s.name] ?? [];
          return !fcmTokenList.includes(fcmToken);
        });

     
        if (!servicesWithoutFcmToken.length) return;

       
        let pushListNew = servicesWithoutFcmToken.reduce((out, s) => {
          let code = s.name;
          let fcmTokenListOld = notifyContacts?.push?.[code] ?? [];
          out[code] = [...fcmTokenListOld, fcmToken];
          return out;
        }, {});

        notifyContacts.push = { ...notifyContacts.push, ...pushListNew };

        let { data: contacts } = await saveUserContacts(
          taxCode,
          notifyContacts
        );

        context.commit("setUserContacts", { contacts });
      } catch (err) {
        console.error(err);
      }
    },

    setRegistrationSW(context, { registration }) {
      context.commit("setRegistrationSW", { registration });
    },
    async setAppointmentList(context) {
      let user = context.getters["getUser"];
      let taxCode = user?.codFisc;

      let params = {
        sort: "-start",
        aggregates: true,
        codice_fiscale: taxCode,
      };
      try {
        let { data: appointments } = await getAppointmentList({ params });
        context.commit("setAppointmentList", { appointments });
      } catch (e) {
        context.dispatch("setNotificationInfo", {
          body: "Non è stato possibile caricare l'elenco degli appuntamenti",
        });
      }
    },
    async setPaymentList(context) {
      let user = context.getters["getUser"];
      let taxCode = user?.codFisc;
      let params = {
        currentPage: 1,
        elements: LIMIT_ACTVITIES,
        sort: "-dataScadenza",
      };
      try {
        let { data } = await getDebtPositions(taxCode, { params });
        if (data) {
          context.commit("setPaymentList", { data });
        }
      } catch (e) {
        context.dispatch("setNotificationInfo", {
          body: "Non è stato possibile caricare l'elenco dei pagamenti",
        });
      }
    },
    async setInstanceList(context) {
      let params = {
        limit: LIMIT_ACTVITIES,
        offset: 0,
      };
      let idMoonToken = context.getters["getMoonConfig"]?.idMoonToken;
      try {
        let { data } = await getMoonInstances(idMoonToken, params);
        if (data) {
          context.commit("setInstanceList", { data });
        }
      } catch (e) {
        context.dispatch("setNotificationInfo", {
          body: "Non è stato possibile recuperare le pratiche",
        });
      }
    },
    setMaintenanceHash(context, { hash }) {
      context.commit("setMaintenanceHash", { hash });
    },
  },
  mutations: {
    setNotifyServicesList: (state, { list }) => {
      state.notifyServicesList = list;
    },
    setUser: (state, { user }) => {
      state.user = user;
    },
    setMoonAuth: (state, moonAuth) => {
      state.moonAuth = moonAuth;
    },
    setMoonConfig: (state, moonConfig) => {
      state.moonConfig = moonConfig;
    },
    setDevLogout: (state, devLogout) => {
      state.devLogout = devLogout;
    },
    setComuniConfig: (state, comuniConfig) => {
      state.comuniConfig = comuniConfig;
    },
    setComuniConfigServices: (state, comuniConfigServices) => {
      state.comuniConfigServices = comuniConfigServices;
    },
    setComuniConfigDetailServices: (state, comuniConfigDetailServices) => {
      state.comuniConfigDetailServices = comuniConfigDetailServices;
    },
    setComuniConfigMenu: (state, comuniConfigMenu) => {
      state.comuniConfigMenu = comuniConfigMenu;
    },

    setUserContacts: (state, { contacts }) => {
      state.user = Object.assign({}, state.user, { contatti: contacts });
    },
    setNotificationInfo: (state, { title, body, positive }) => {
      if (title)
        state.notificationInfo = Object.assign({}, state.notificationInfo, {
          title: title,
        });
      if (body)
        state.notificationInfo = Object.assign({}, state.notificationInfo, {
          body: body,
        });
      state.notificationInfo = Object.assign({}, state.notificationInfo, {
        positive: !!positive,
      });
    },
    setTicketList: (state, { tickets, nPage }) => {
      state.ticketList = Object.assign({}, state.ticketList, {
        tickets: tickets ?? [],
      });
      state.ticketList = Object.assign({}, state.ticketList, {
        nPage: nPage,
      });
    },

    setMessagesUnread: (state, total) => {
      state.messagesUnread = total;
    },
    setMessageList: (state, { messages, messagesUnread }) => {
      if (messages)
        state.messageList = Object.assign({}, state.messageList, {
          messages: messages,
        });
      state.messageList = Object.assign({}, state.messageList, {
        unread: messagesUnread,
      });
    },
    onreadMessage: (state, messageId) => {
      const total = state.messageList?.unread;
      state.messageList = Object.assign({}, state.messageList, {
        unread: total - 1,
      });
    },
    setPaymentTypeList: (state, { types }) => {
      state.paymentTypeList = types;
    },

    setActiveService: (state, { service }) => {
      state.activeService = service;
    },
    setFcmToken(state, { fcmToken }) {
      state.fcmToken = fcmToken;
    },
    setRegistrationSW(state, { registration }) {
      state.registrationSW = registration;
    },
    setAppointmentList(state, { appointments }) {
      state.appointmentList = appointments;
    },
    setPaymentList(state, { data }) {
      state.paymentList = Object.assign({}, state.paymentList, {
        payments: data.debtPositions ?? [],
      });
      state.paymentList = Object.assign({}, state.paymentList, {
        total: data.totalElements,
      });
    },
    setInstanceList(state, { data }) {
      state.instanceList = Object.assign({}, state.instanceList, {
        instances: data?.items ?? [],
      });
      state.instanceList = Object.assign({}, state.instanceList, {
        total: data?.totalElements,
      });
    },

    setMaintenanceHash: (state, { hash }) => {
      if (hash) {
        sessionStorage.setItem(MAINTENANCE_HASH_KEY, hash);
        state.maintenanceHash = hash;
      } else {
        sessionStorage.removeItem(MAINTENANCE_HASH_KEY);
        state.maintenanceHash = null;
      }
    },
  },
});
