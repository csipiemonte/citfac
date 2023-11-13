/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 */

import { http, httpPublic } from '../boot/axios'

export const getUser = (httpConfig = {}) => {
  const url = `/user/me`
  return http.get(url, httpConfig)
}

// TENANT
// -------------------------------------------------------
export const getTenantList = (httpConfig = {}) => {
  const url = `/tenants`
  return http.get(url, httpConfig)
}
export const getTenant = (id, httpConfig = {}) => {
  const url = `/tenant/${id}`
  return http.get(url, httpConfig)
}

export const createTenant = (payload, httpConfig = {}) => {
  const url = `/tenant`
  return http.post(url, payload, httpConfig)
}

export const updateTenant = (id, payload, httpConfig = {}) => {
  const url = `/tenant/${id}`
  return http.put(url, payload, httpConfig)
}

export const patchTenant = (id, payload, httpConfig = {}) => {
  const url = `/tenant/${id}`
  return http.patch(url, payload, httpConfig)
}

//ASSETS
//-------------------------------------------------------
export const getAssets = (httpConfig = {}) => {
  const url = `/assets`
  return http.get(url, httpConfig)
}

//REGIONI
//-------------------------------------------------------
export const getRegionList = (httpConfig = {}) => {
  const url = `/regions`
  return http.get(url, httpConfig)
}

//MANUTENZIONE
//-------------------------------------------------------
export const updateMaintenance = (tenantId, payload, httpConfig = {}) => {
  const url = `/tenant/${tenantId}/maintenance`
  return http.post(url, payload, httpConfig)
}

//CREDENZIALI
//-------------------------------------------------------
export const getCredentials = (tenantId, httpConfig = {}) => {
  const url = `/tenant/${tenantId}/credentials`
  return http.get(url, httpConfig)
}

export const getCredential = (tenantId, credentialApi, httpConfig = {}) => {
  const url = `/tenant/${tenantId}/credential/${credentialApi}`
  return http.get(url, httpConfig)
}

export const createCredential = (tenantId, assetId, payload, httpConfig = {}) => {
  const url = `/tenant/${tenantId}/credential/${assetId}`
  return http.post(url, payload, httpConfig)
}

export const updateCredential = (tenantId, credentialApi, payload, httpConfig = {}) => {
  const url = `/tenant/${tenantId}/credential/${credentialApi}`
  return http.put(url, payload, httpConfig)
}

export const deleteCredential = (tenantId, credentialApi, httpConfig = {}) => {
  const url = `/tenant/${tenantId}/credential/${credentialApi}`
  return http.delete(url, httpConfig)
}

//SERVIZI
//-------------------------------------------------------

export const getServiceCards = (tenantId, httpConfig = {}) => {
  const url = `/tenant/${tenantId}/services`
  return http.get(url, httpConfig)
}

export const getServiceCard = (tenantId, serviceId, httpConfig = {}) => {
  const url = `/tenant/${tenantId}/service/${serviceId}`
  return http.get(url, httpConfig)
}

export const createServiceCard = (tenantId, payload, httpConfig = {}) => {
  const url = `/tenant/${tenantId}/service`
  return http.post(url, payload, httpConfig)
}

export const updateServiceCard = (tenantId, serviceId, payload, httpConfig = {}) => {
  const url = `/tenant/${tenantId}/service/${serviceId}`
  return http.put(url, payload, httpConfig)
}

export const deleteServiceCard = (tenantId, serviceId, httpConfig = {}) => {
  const url = `/tenant/${tenantId}/service/${serviceId}`
  return http.delete(url, httpConfig)
}

export const getServiceCategories = (httpConfig = {}) => {
  const url = `/services/categories`
  return http.get(url, httpConfig)
}

// DETTAGLIO SERVIZI
//-------------------------------------------------------

export const getServicesDetails = (tenantId, httpConfig = {}) => {
  const url = `/tenant/${tenantId}/details`
  return http.get(url, httpConfig)
}

export const getServicesDetail = (tenantId, detailId, httpConfig = {}) => {
  const url = `/tenant/${tenantId}/detail/${detailId}`
  return http.get(url, httpConfig)
}
export const newServicesDetail = (tenantId, payload, httpConfig = {}) => {
  const url = `/tenant/${tenantId}/detail`
  return http.post(url, payload, httpConfig)
}

export const updateServicesDetail = (tenantId, detailId, payload, httpConfig = {}) => {
  const url = `/tenant/${tenantId}/detail/${detailId}`
  return http.put(url, payload, httpConfig)
}

export const deleteServicesDetail = (tenantId, detailId, httpConfig = {}) => {
  const url = `/tenant/${tenantId}/detail/${detailId}`
  return http.delete(url, httpConfig)
}

// MENU
//-------------------------------------------------------

export const getMenuList = (tenantId, httpConfig = {}) => {
  const url = `/tenant/${tenantId}/menu`
  return http.get(url, httpConfig)
}

export const createMenu = (tenantId, payload, httpConfig = {}) => {
  const url = `/tenant/${tenantId}/menu`
  return http.post(url, payload, httpConfig)
}

export const updateMenu = (tenantId, payload, httpConfig = {}) => {
  const url = `/tenant/${tenantId}/menu`
  return http.put(url, payload, httpConfig)
}

export const deleteMenu = (tenantId, httpConfig = {}) => {
  const url = `/tenant/${tenantId}/menu`
  return http.delete(url, httpConfig)
}

// CMS
//-------------------------------------------------------

export const getTenantCms = (tenantId, httpConfig = {}) => {
  const url = `/tenant/${tenantId}/cms`
  return http.get(url, httpConfig)
}

export const createTenantCms = (tenantId, payload, httpConfig = {}) => {
  const url = `/tenant/${tenantId}/cms`
  return http.post(url, payload, httpConfig)
}

export const updateTenantCms = (tenantId, payload, httpConfig = {}) => {
  const url = `/tenant/${tenantId}/cms`
  return http.put(url, payload, httpConfig)
}

export const deleteTenantCms = (tenantId, httpConfig = {}) => {
  const url = `/tenant/${tenantId}/cms`
  return http.delete(url, httpConfig)
}

// API-PUBLIC
//-------------------------------------------------------
// SERVIZI CRM
export const getCrmServicesList = (httpConfig = {}) => {
  const url = `/crm/services`
  return httpPublic.get(url, httpConfig)
}

// SERVIZI NOTIFICATORE
export const getNotifyServicesList = (httpConfig = {}) => {
  const url = `/notification/services`
  return http.get(url, httpConfig)
}

// MODULI MOON
export const getMoonModulesList = (httpConfig = {}) => {
  const url = `/moon/moduli`
  return http.get(url, httpConfig)
}

//FAQS
export const getFaqTypeList = (httpConfig = {}) => {
  const url = `/crm/knowledge_bases/1/categories`
  return httpPublic.get(url, httpConfig)
}

export const getFaqDetail = (typeId, httpConfig = {}) => {
  const url = `/crm/knowledge_bases/1/categories/${typeId}`
  return httpPublic.get(url, httpConfig)
}


export const getAppointmentOffices = (httpConfig = {}) => {
  const url = `/appsched/categories`;
  return httpPublic.get(url, httpConfig);
};

// FIREBASE
//-------------------------------------------------------

export const getFirebaseConfig = (tenantId, httpConfig = {}) => {
  const url = `/tenant/${tenantId}/firebase`
  return http.get(url, httpConfig)
}

export const createFirebaseConfig = (tenantId, payload, httpConfig = {}) => {
  const url = `/tenant/${tenantId}/firebase`
  return http.post(url, payload, httpConfig)
}

export const updateFirebaseConfig = (tenantId, payload, httpConfig = {}) => {
  const url = `/tenant/${tenantId}/firebase`
  return http.put(url, payload, httpConfig)
}

// FEEDBACK
//-------------------------------------------------------

export const getFeedbackStats = (tenantId, httpConfig = {}) => {
  const url = `/tenant/${tenantId}/feedbacks/stats`
  return http.get(url, httpConfig)
}

export const getFeedbackDetail = (tenantId, httpConfig = {}) => {
  const url = `/tenant/${tenantId}/feedback`
  return http.get(url, httpConfig)
}

export const getFeedbackComments = (tenantId, httpConfig = {}) => {
  const url = `/tenant/${tenantId}/feedback/messages`
  return http.get(url, httpConfig)
}
