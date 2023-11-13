/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 */

export const MAINTENANCE_HASH_KEY = "CITFAC_MAINTENANCE"


export const PREFERENCES_TYPES_MAP={
    SMS: 'sms',
    EMAIL: 'email',
    PUSH: 'push'
}

export const MOBILE_PHONE_PREFIX_ITALY = "0039";

export const DATE_REGEX = /^(19[0-9]{2}|2[0-9]{3})-(0[1-9]|1[012])-([123]0|[012][1-9]|31)$/;

export const LIMIT_ACTVITIES = 5


export const DEFAULT_META_TITLE = "Città Facile";
export const ASSETS_MAP = {
    PAYMENTS: "PPAY",
    APPOINTMENTS: "APPSCHED",
    ASSISTANCE: "ASSISTENZA_CRM",
    INSTANCES: "MOON",
    DOCUMENTS: "MY_DOCS",
    NOTIFICATIONS: "NOTIFICATIONS"
}


   


export const PAYMENT_STATUS_MAP = {
    PAYED: "0",
    NOT_PAYED: "1", 
    CANCELLED: "2"
}

export const  LINK_TYPES_MAP={
    LINK: "a", 
    ROUTER: "router-link"
  }