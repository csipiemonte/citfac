/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 */

import { createStore } from 'vuex'
import {
  getCrmServicesList,
  getServicesDetails,
  getMoonModulesList,
  getNotifyServicesList,
  getServiceCards,
  getAppointmentOffices
} from '../services/api'
import { onShowNotificationToast } from '../services/business-logic'
export default createStore({
  state: {
    user: null,
    notificationToasts: [],
    tenant: null,
    tenantId: null,
    assets: [],
    servicesList: [],
    serviceCardList: [],
    assistanceServiceList: [],
    moonModulesList: [],
    notifyServiceList: [],
    faqCategoriesList: [],
    tenantCms: null,
    serviceCategoriesList: [],
    appointmentOfficesList: []
  },
  getters: {
    getUser: (state) => state.user,
    getTenant: (state) => state.tenant,
    getAssets: (state) => state.assets,
    getNotificationToasts: (state) => state.notificationToasts,
    getTenantId: (state) => state.tenantId,
    getServicesList: (state) => state.servicesList,
    getServiceCardList: (state) => state.serviceCardList,
    getAssistanceServiceList: (state) => state.assistanceServiceList,
    getMoonModulesList: (state) => state.moonModulesList,
    getNotifyServiceList: (state) => state.notifyServiceList,
    getFaqCategoriesList: (state) => state.faqCategoriesList,
    getAppointmentOfficesList: (state) => state.appointmentOfficesList,
    getServiceCategoriesList: (state) => state.serviceCategoriesList,
    getTenantCms: (state) => state.tenantCms
  },
  actions: {
    setUser(context, { user }) {
      context.commit('setUser', { user })
    },
    addNotificationToast(context, toast) {
      context.commit('addNotificationToast', toast)
    },
    clearNotificationToast(context) {
      context.commit('clearNotificationToast')
    },
    setTenantId(context, id) {
      context.commit('setTenantId', id)
    },
    setTenant(context, { tenant }) {
      context.commit('setTenant', { tenant })
    },
    setAssets(context, { assets }) {
      context.commit('setAssets', { assets })
    },
    setTenantCms(context, { cms }) {
      context.commit('setTenantCms', { cms })
    },
    setServicesList(context, { list }) {
      context.commit('setServicesList', { list })
    },
    setServiceCardList(context, { list }) {
      context.commit('setServiceCardList', { list })
    },
    setAssistanceServiceList(context, { list }) {
      context.commit('setAssistanceServiceList', { list })
    },
    setMoonModulesList(context, { list }) {
      context.commit('setMoonModulesList', { list })
    },
    setNotifyServiceList(context, { list }) {
      context.commit('setNotifyServiceList', { list })
    },
    setFaqCategoriesList(context, { list }) {
      context.commit('setFaqCategoriesList', { list })
    },

    setServiceCategoriesList(context, { list }) {
      context.commit('setServiceCategoriesList', { list })
    },
    setAppointmentOfficesList(context, { list }) {
      context.commit('setAppointmentOfficesList', { list })
    },
    async updateServiceList(context) {
      let tenantId = context.getters['getTenantId']
      //Recuperiamo la lista dei dettagli servizi
      let servicesListPromise = getServicesDetails(tenantId)

      //Recuperiamo la lista delle schede servizio
      let serviceCardsPromise = getServiceCards(tenantId)

      try {
        let { data: list } = await servicesListPromise
        context.commit('setServicesList', { list })
      } catch (error) {
        await onShowNotificationToast({
          body: 'Non è stato possibile recuperare la lista dei servizi.',
          type: 'error'
        })
      }

      try {
        let { data: list } = await serviceCardsPromise
        context.commit('setServiceCardList', { list })
      } catch (error) {
        await onShowNotificationToast({
          body: 'Non è stato possibile recuperare la lista dei servizi Cms.',
          type: 'error'
        })
      }
    },
    async setDefaultServiceParams(context) {
      let tenantId = context.getters['getTenantId']
      let servicesList = context.getters['getServicesList']
      let assistanceServices = context.getters['getAssistanceServiceList']
      let moonModules = context.getters['getMoonModulesList']
      let notifyServices = context.getters['getNotifyServiceList']
      let appointmentOffices = context.getters['getAppointmentOfficesList']

      let assistanceServicesPromise = null
      let moonModulesPromise = null
      let notifyServicesPromise = null
      let appointmentOfficesPromise = null
      //Recuperiamo la lista delle servizi CRM
      if (assistanceServices?.length === 0) {
        assistanceServicesPromise = getCrmServicesList(tenantId)
      }

      //Recuperiamo la lista delle servizi moon
      if (moonModules?.length === 0) {
        moonModulesPromise = getMoonModulesList()
      }

      //Recuperiamo la lista degli sportelli appuntamenti
      if (appointmentOffices?.length === 0) {
        appointmentOfficesPromise = getAppointmentOffices()
      }

      //Recuperiamo la lista delle servizi del notificatore
      if (notifyServices?.length === 0) {
        notifyServicesPromise = getNotifyServicesList()
      }
      //Recuperiamo la lista dei dettagli servizi e servizi
      if (servicesList?.length === 0) {
        await context.dispatch('updateServiceList')
      }

      if (assistanceServicesPromise) {
        try {
          let { data: list } = await assistanceServicesPromise

          context.commit('setAssistanceServiceList', { list })
        } catch (error) {
          await onShowNotificationToast({
            body: 'Non è stato possibile recuperare la lista dei servizi assistenza.',
            type: 'error'
          })
        }
      }
      if (moonModulesPromise) {
        try {
          let { data: list } = await moonModulesPromise
          context.commit('setMoonModulesList', { list })
        } catch (error) {
          await onShowNotificationToast({
            body: 'Non è stato possibile recuperare la lista dei moduli moon.',
            type: 'error'
          })
        }
      }
      if (appointmentOfficesPromise) {
        try {
          let { data: list } = await appointmentOfficesPromise
          context.commit('setAppointmentOfficesList', { list })
        } catch (error) {
          await onShowNotificationToast({
            body: 'Non è stato possibile recuperare la lista degli sportelli.',
            type: 'error'
          })
        }
      }

      if (notifyServicesPromise) {
        try {
          let { data: list } = await notifyServicesPromise
          context.commit('setNotifyServiceList', { list })
        } catch (error) {
          await onShowNotificationToast({
            body: 'Non è stato possibile recuperare la lista dei servizi di notifica.',
            type: 'error'
          })
        }
      }
    }
  },

  mutations: {
    setTenant: (state, { tenant }) => {
      state.tenant = tenant
    },
    addNotificationToast: (state, toast) => {
      toast['index'] = state.notificationToasts.length
      state.notificationToasts.push(toast)
    },
    clearNotificationToast: (state, index) => {
      state.notificationToasts.splice(index, 1)
    },
    setTenantId: (state, id) => {
      state.tenantId = id
    },
    setTenantCms: (state, { cms }) => {
      state.tenantCms = cms
    },
    setAssets: (state, { assets }) => {
      state.assets = assets
    },
    setUser: (state, { user }) => {
      state.user = user
    },
    setServicesList: (state, { list }) => {
      state.servicesList = list ?? []
    },
    setServiceCardList: (state, { list }) => {
      state.serviceCardList = list ?? []
    },
    setAssistanceServiceList: (state, { list }) => {
      state.assistanceServiceList = list ?? []
    },
    setMoonModulesList: (state, { list }) => {
      state.moonModulesList = list ?? []
    },
    setNotifyServiceList: (state, { list }) => {
      state.notifyServiceList = list ?? []
    },
    setFaqCategoriesList: (state, { list }) => {
      state.faqCategoriesList = list ?? []
    },

    setServiceCategoriesList: (state, { list }) => {
      state.serviceCategoriesList = list ?? []
    },
    setAppointmentOfficesList: (state, { list }) => {
      state.appointmentOfficesList = list ?? []
    }
  }
})
