/*
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 */

import axios from "axios";

import { isAssetPublic } from "../services/business-logic";
import { ASSETS_MAP, MAINTENANCE_HASH_KEY } from "../services/config";
import { BASE_URL } from "../configuration";

const IS_DEV = import.meta.env.VITE_IS_DEV;
const IS_TEST = import.meta.env.VITE_IS_TEST;
const IS_PROD = import.meta.env.VITE_IS_PROD;

export const http = axios.create({
  baseURL: IS_DEV ? BASE_URL + import.meta.env.VITE_APP_API_BASE_URL : import.meta.env.VITE_APP_API_BASE_URL,
});
export const httpPublic = axios.create({
  baseURL: IS_DEV ? BASE_URL + import.meta.env.VITE_APP_API_BASE_PUBLIC_URL : import.meta.env.VITE_APP_API_BASE_PUBLIC_URL,

});

// verifichiamo se appsched è pubblico o no
export const httpAppsched = () => {
  const isAppschedPublic = isAssetPublic(ASSETS_MAP.APPOINTMENTS);
  return isAppschedPublic ? httpPublic : http;
};

addHeaders(http);
addPublicHeaders(httpPublic);

function addHeaders(http) {
  http.interceptors.request.use((cfg) => {
    cfg.headers = cfg.headers || {};

    const HASH = JSON.parse( sessionStorage.getItem(MAINTENANCE_HASH_KEY));
    cfg["auth"] = HASH;

    return cfg;
  });
}

function addPublicHeaders(http) {
  http.interceptors.request.use((cfg) => {
    cfg.headers = cfg.headers || {};

    const HASH = JSON.parse( sessionStorage.getItem(MAINTENANCE_HASH_KEY));
    cfg["auth"] = HASH;

    return cfg;
  });
}
