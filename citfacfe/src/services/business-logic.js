/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 */

import { PREFERENCES_TYPES_MAP } from "./config";
import { getNotifyServicesList } from "./api";
import store from "../store/index";

export const LOGIN_URL = "api/utenti/login";
export const LOGOUT_URL = "api/utenti/logout";
export const setNotifyPreferences = async () => {
  let { data: list } = await getNotifyServicesList();
  await store.dispatch("setNotifyServicesList", { list });
};

export const isPreferenceChannelActive = (channelOptions, channel) => {
  return !!channelOptions[channel];
};

export const mapPreferencesList = (preferences) => {
  let preferencesList = preferences?.split(",") ?? [];
  return {
    [PREFERENCES_TYPES_MAP.EMAIL]: preferencesList.includes(
      PREFERENCES_TYPES_MAP.EMAIL
    ),
    [PREFERENCES_TYPES_MAP.SMS]: preferencesList.includes(
      PREFERENCES_TYPES_MAP.SMS
    ),
    [PREFERENCES_TYPES_MAP.PUSH]: preferencesList.includes(
      PREFERENCES_TYPES_MAP.PUSH
    ),
  };
};
export const convertToBlobUrl = (binary) => {
  const blob = new Blob([binary], { type: "application/pdf" });
  return URL.createObjectURL(blob);
};

export const base64ToBlob = (base64, type = "application/pdf") => {
  const binStr = window.atob(base64);
  const len = binStr.length;
  const arr = new Uint8Array(len);
  for (let i = 0; i < len; i++) {
    arr[i] = binStr.charCodeAt(i);
  }
  return arr;
};

export const isAssetPublic = (assetCode) => {
  let assetList = store.getters["getAssetsList"];
  const asset = assetList?.find((a) => a.codice === assetCode);
  return asset?.showPublic;
};



//STATI TICKET
export const NEW = 1;
export const OPENED = 2;
export const CLOSED = 4;
export const RESOLVED = 8;
export const MORE_INFO = 9;

//STATI ISTANZE
export const SENT = { CODICE: "INVIATA", DESC: "In attesa" };
export const PUBLISHED = { CODICE: "PUB", DESC: "In corso" };
export const COMPLETED = { CODICE: "COMPLETATA", DESC: "Conclusa" };
export const PARTIAL = { CODICE: "BOZZA", DESC: "In bozza" };
export const APPROVED = { CODICE: "APPROVATA", DESC: "Approvata" };
export const UNDER_PROCESSING = {
  CODICE: "IN_ISTRUTTORIA",
  DESC: "in lavorazione",
};

//FEEDBACK TYPE
export const TICKET = "ticket";
export const TICKET_NO_AUTH = "ticketNoAuth";
export const PARTIAL_TICKET = "partialTicket";


//ID RELATIVO AGLI UTENTI NEGLI ARTICLES
export const CUSTOMER_COD = "Customer";

//CODICE FAQ
export const FAQ_COD = 1;
