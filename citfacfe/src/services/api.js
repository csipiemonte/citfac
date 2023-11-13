/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 */

import { http,  httpPublic, httpAppsched } from "../boot/axios";
import { getUri } from "./utils";


export const postMaintenanceCredentials = ( httpConfig = {}) => {
  const url = `/maintenance`;
  return httpPublic.post(url, httpConfig);
};

export const getListaServizi = (httpConfig = {}) => {
  const url = `/crm/services`;
  return httpPublic.get(url, httpConfig);
};



export const getPrivacyText = (httpConfig = {}) => {
  const url = `/policy`;
  return httpPublic.get(url, httpConfig);
};


export const getListaTipologieServizio = (id, httpConfig = {}) => {
  const url = `/crm/services/${id}`;
  return httpPublic.get(url, httpConfig);
};

export const createTicket = (payload, params, httpConfig = {}) => {
  const url = `/crm/tickets`;
  return http.post(url, payload, { params }, httpConfig);
};

export const createTicketPublic = (payload, params, httpConfig = {}) => {
  const url = `/crm/tickets`;
  return httpPublic.post(url, payload, { params }, httpConfig);
};

export const createOtp = (payload, httpConfig = {}) => {
  const url = `/otp`;
  return httpPublic.post(url, payload, httpConfig);
};

export const validateOtp = (params, httpConfig = {}) => {
  const url = `/otp/verify`;
  return httpPublic.get(url, { params }, httpConfig);
};

export const getAttemptsLeft = (httpConfig = {}) => {
  const url = `/otp/channels/sms/attempts-left`;
  return httpPublic.get(url, httpConfig);
};

export const getMe = (httpConfig = {}) => {
  const url = `/utenti/me`;
  return httpPublic.get(url, httpConfig);
};

//DATO HEADER SHIB IDENTITà MI DA I TKT DELL'UTENTE LOGGATO
export const getTicketList = (httpConfig = {}) => {
  const url = `/crm/tickets/search`;
  return http.get(url, httpConfig);
};

export const getFullTicketList = (httpConfig = {}) => {
  const url = `/crm/alltickets`;
  return http.get(url, httpConfig);
};


export const getTicketDetail = (idTicket, httpConfig = {}) => {
  const url = `/crm/tickets/${idTicket}/articles`;
  return http.get(url, httpConfig);
};


export const getTicketArticleFile = (
  idTicket,
  idArticle,
  idAttachment,
  httpConfig = {}
) => {
  const url = getUri({
    url: `${http.defaults.baseURL}/crm/tickets/${idTicket}/articles/${idArticle}/attachments/${idAttachment}`,
    ...httpConfig,
  });
  window.open(url);
};

//GET LISTA BOZZE
export const getPartialTicketlist = (params, httpConfig = {}) => {
  const url = `/drafts`;
  return http.get(url, { params }, httpConfig);
};

export const createPartialTicket = (payload, httpConfig = {}) => {
  const url = `/draft`;
  return http.post(url, payload, httpConfig);
};

//CANCELLA BOZZA
export const deletePartialTicket = (
  id,
  httpConfig = { responseType: "blob" }
) => {
  const url = `/draft/${id}`;
  return http.delete(url, httpConfig);
};

//MODIFICA BOZZA
export const updatePartialTicket = (
  id,
  payload,
  httpConfig = { responseType: "blob" }
) => {
  const url = `/draft/${id}`;
  return http.put(url, payload, httpConfig);
};


export const changeTicketStatus = (id, payload, httpConfig = {}) => {
  const url = `/crm/tickets/${id}`;
  return http.put(url, payload, httpConfig);
};


export const editTicket = (id, payload, httpConfig = {}) => {
  const url = `/crm/tickets/${id}/articles`;
  return http.post(url, payload, httpConfig);
};

export const getConfigComuni = (host, httpConfig = {}) => {
  const url = `/configComune/fromtenant`;
  return httpPublic.get(url, httpConfig);
};
export const getConfigServices = (host, httpConfig = {}) => {
  const url = `/configComune/servizi`;
  return httpPublic.get(url, httpConfig);
};

export const getConfigDetailServices = (httpConfig = {}) => {
  const url = `/configComune/dettaglioServizi`;
  return httpPublic.get(url, httpConfig);
};

export const getConfigMenu = (host, httpConfig = {}) => {
  const url = `/configComune/menu`;
  return httpPublic.get(url, httpConfig);
};

//NOTIFICATORE

export const getNotifyTerms = (httpConfig = {}) => {
  const url = `/notificatore/preferenze/terms`;
  return http.get(url, httpConfig);
};

export const updateNotifyTerms = (taxCode, httpConfig = {}) => {
  const url = `/notificatore/preferenze/users/${taxCode}/terms`;
  return http.put(url, httpConfig);
};

export const saveUserContacts = (taxCode, payload, httpConfig = {}) => {
  const url = `/notificatore/preferenze/users/${taxCode}/contacts`;
  return http.put(url, payload, httpConfig);
};

export const deleteUserContacts = (taxCode, httpConfig = {}) => {
  const url = `/notificatore/preferenze/users/${taxCode}`;
  return http.delete(url, httpConfig);
};

export const getNotifyServicesList = (httpConfig = {}) => {
  const url = `/notificatore/preferenze/services`;
  return http.get(url, httpConfig);
};
export const getNotifyPreferencesList = (taxCode, httpConfig = {}) => {
  const url = `/notificatore/preferenze/users/${taxCode}/preferences`;
  return http.get(url, httpConfig);
};

export const setNotifyPreferencesList = (taxCode, payload, httpConfig = {}) => {
  const url = `/notificatore/preferenze/users/${taxCode}/preferences`;
  return http.put(url, payload, httpConfig);
};

//FAQ
export const getFaqTypeList = (httpConfig = {}) => {
  const url = `/crm/knowledge_bases/1/categories`;
  return httpPublic.get(url, httpConfig);
};

export const searchFaq = (payload, httpConfig = {}) => {
  const url = `/crm/knowledge_bases/search`;
  return httpPublic.post(url, payload, httpConfig);
};

export const getFaqList = (typeId, httpConfig = {}) => {
  const url = `/crm/knowledge_bases/1/categories/${typeId}`;
  return httpPublic.get(url, httpConfig);
};

export const getFaqAnswer = (typeId, id, httpConfig = {}) => {
  const url = `/crm/knowledge_bases/1/categories/${typeId}/answers/${id}`;
  return httpPublic.get(url, httpConfig);
};

//MOON
export const getMoonToken = (httpConfig = {}) => {
  const url = `/moon/token`;
  return http.get(url, httpConfig);
};
export const getMoonIdentity = (httpConfig = {}) => {
  const url = `/moon/moon-identita`;
  let config = { params: {}, headers: httpConfig };
  return http.get(url, config);
};

export const getMoonInstances = (
  moonIdToken,
  params,
  httpConfig = { "Moon-Identita-JWT": moonIdToken }
) => {
  const url = `/moon/istanze-paginate`;
  let config = { params: params, headers: httpConfig };
  return http.get(url, config);
};

export const downloadInstance = (
  instanceCode,
  moonIdToken,
  httpConfig = { "Moon-Identita-JWT": moonIdToken }
) => {
  let config = { params: {}, headers: httpConfig, responseType: "blob" };
  const url = `/moon/istanze/${instanceCode}/pdf`;
  return http.get(url, config);
};

export const deleteMoonInstance = (
  instanceCode,
  moonIdToken,
  httpConfig = { "Moon-Identita-JWT": moonIdToken }
) => {
  const url = `/moon/istanze/${instanceCode}`;
  let config = { params: {}, headers: httpConfig };
  return http.delete(url, config);
};

//NOTIFICATORE
export const getNotifications = (cf, params, httpConfig = {}) => {
  const url = `/notificatore/messagestore/users/${cf}/messages`;
  return http.get(url, { params }, httpConfig);
};

export const deleteNotifications = (cf, id, httpConfig = {}) => {
  const url = `/notificatore/messagestore/users/${cf}/messages/${id}`;
  return http.delete(url, httpConfig);
};

export const setNotificationStatus = (cf, payload, httpConfig = {}) => {
  const url = `/notificatore/messagestore/users/${cf}/messages/status`;
  return http.put(url, payload, httpConfig);
};

//DOCUMENTI
export const getDocumentsList = (cf, payload, httpConfig = {}) => {
  const url = `/document/documenti/cittadino/${cf}`;
  return http.post(url, payload, httpConfig);
};

export const getDocumentScopeList = (cf, httpConfig = {}) => {
  const url = `/document/ambiti/cittadino/${cf}`;
  return http.get(url, httpConfig);
};

export const getDocumentDetail = (cf, uuid, httpConfig = {}) => {
  const url = `/document/documenti/${uuid}/cittadino/${cf}`;
  return http.get(url, httpConfig);
};

//PAGAMENTI

export const getpaymentPolicy = (httpConfig = {}) => {
  const url = `policy/ppay`;
  return httpPublic.get(url, httpConfig);
};

export const getDebtPositions = (cf, httpConfig = {}) => {
  const url = `/ppay/citizens/${cf}/debtpositions`;
  return http.get(url, httpConfig);
};
export const getPaymentTypes = (httpConfig = {}) => {
  const url = `/ppay/paymenttypes`;
  return httpPublic.get(url, httpConfig);
};
export const getPaymentReceipt = (cf, iuv, httpConfig = {}) => {
  const url = `/ppay/citizens/${cf}/debtpositions/${iuv}/paymentreceipt`;
  return http.get(url, httpConfig);
};

export const getPaymentNoticePdf = (cf, iuv, httpConfig = {}) => {
  const url = `/ppay/citizens/${cf}/debtpositions/${iuv}/paymentnotice`;
  return http.get(url, httpConfig);
};

export const getPagoPaUrl = (iuv, httpConfig = {}) => {
  const url = `/ppay/debtpositions/${iuv}/payment`;
  return http.get(url, httpConfig);
};

export const setPagoPaUrlNew = (paymentType, payload, httpConfig = {}) => {
  const url = `/ppay/paymenttypes/${paymentType}/payment`;
  return httpPublic.post(url, payload, httpConfig);
};

export const createDebtPositionNotice = (
  cf,
  paymentType,
  payload,
  httpConfig = {}
) => {
  const url = `/ppay/citizens/${cf}/paymenttypes/${paymentType}/debtpositions`;
  return httpPublic.post(url, payload, httpConfig);
};

export const getReceiptStorageStatus = (cf, httpConfig = {}) => {
  const url = `/ppay/citizens/${cf}`;
  return http.get(url, httpConfig);
};
export const setReceiptStorageStatus = (cf, httpConfig = {}) => {
  const url = `/ppay/citizens/${cf}`;
  return http.put(url, {}, httpConfig);
};

//APPUNTAMENTI
export const getAppointmentCategories = (httpConfig = {}) => {
  const url = `/appsched/categories`;
  return httpAppsched().get(url, httpConfig);
};

export const getCategoryServices = (id, httpConfig = {}) => {
  const url = `/appsched/categories/${id}/services`;
  return httpAppsched().get(url, httpConfig);
};

export const getAppointmentService = (id, httpConfig = {}) => {
  const url = `/appsched/services/${id}`;
  return httpAppsched().get(url, httpConfig);
};

export const getServiceProviders = (id, httpConfig = {}) => {
  const url = `/appsched/services/${id}/providers`;
  return httpAppsched().get(url, httpConfig);
};

export const getServiceUnavailableDates = (id, httpConfig = {}) => {
  const url = `/appsched/services/${id}/unavailable_dates`;
  return httpAppsched().get(url, httpConfig);
};

export const getServiceAvailableHours = (id, httpConfig = {}) => {
  const url = `/appsched/services/${id}/availabilities`;
  return httpAppsched().get(url, httpConfig);
};

export const createAppointment = (payload, httpConfig = {}) => {
  const url = `/appsched/appointments`;
  return httpAppsched().post(url, payload, httpConfig);
};

export const getAppointment = (id, httpConfig = {}) => {
  const url = `/appsched/appointments/${id}`;
  return httpAppsched().get(url, httpConfig);
};

export const getAppointmentList = (httpConfig = {}) => {
  const url = `/appsched/appointments`;
  return httpAppsched().get(url, httpConfig);
};

export const editAppointment = (id, payload, httpConfig = {}) => {
  const url = `/appsched/appointments/${id}`;
  return httpAppsched().put(url, payload, httpConfig);
};

export const deleteAppointment = (id, httpConfig = {}) => {
  const url = `/appsched/appointments/${id}`;
  return httpAppsched().delete(url, httpConfig);
};

//PREFERITI
export const setFavouriteService = (id, httpConfig = {}) => {
  const url = `/preferenze/${id}`;
  return http.post(url, httpConfig);
};

export const removeFavouriteService = (id, httpConfig = {}) => {
  const url = `/preferenze/${id}`;
  return http.delete(url, httpConfig);
};

export const getFavouriteService = (httpConfig = {}) => {
  const url = `/preferenze`;
  return http.get(url, httpConfig);
};

//VALUTAZIONE
export const setServiceFeedback = (payload, httpConfig = {}) => {
  const url = `/feedback`;
  return httpPublic.post(url, payload, httpConfig);
};
