/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 */

import axios from 'axios'
import { BASE_URL } from '../configuration'

const IS_DEV = import.meta.env.VITE_IS_DEV;
const IS_TEST = import.meta.env.VITE_IS_TEST;
const IS_PROD = import.meta.env.VITE_IS_PROD;

export const http = axios.create({
  baseURL: IS_DEV ? BASE_URL + import.meta.env.VITE_APP_API_BASE_URL : import.meta.env.VITE_APP_API_BASE_URL,
});
export const httpPublic = axios.create({
  baseURL: IS_DEV ? BASE_URL + import.meta.env.VITE_APP_API_BASE_PUBLIC_URL : import.meta.env.VITE_APP_API_BASE_PUBLIC_URL,

});



addHeaders(http)
addHeaders(httpPublic)

function addHeaders(http) {
  http.interceptors.request.use((cfg) => {
    cfg.headers = cfg.headers || {}
    return cfg
  })
}
