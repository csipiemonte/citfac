/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 */

import store from "../store";

const consumer = "cittafacile";

export function useMoonUrl(codiceModulo, service, codiceIstanza, backUrl) {
  const codiceEnteMoon = store.getters["getComuniConfig"]?.codiceEnte;
  const defaultBackUrl = window.location.origin
  let url = `/moonfobl/accesso/gasprp_salute?consumer=${consumer}&service=${service}&back_url=${
    backUrl || defaultBackUrl //"%2F"
  }&codice_ente=${codiceEnteMoon}`;
  if (codiceModulo) {
    url = url + "&codice_modulo=" + codiceModulo;
  }
  if (codiceIstanza) {
    url = url + "&codice_istanza=" + codiceIstanza;
  }

  return url;

}


export function useServiceMoonUrl(serviceiD, codiceModulo, service, codiceIstanza, backUrl) {
  const codiceEnteMoon = store.getters["getComuniConfig"]?.codiceEnte;
  const defaultBackUrl = window.location.origin
  let url = `/moonfobl/accesso/gasprp_salute?consumer=${consumer}&service=${service}&back_url=${
    backUrl || defaultBackUrl //"%2F"
  }&codice_ente=${codiceEnteMoon}`;
  if (codiceModulo) {
    url = url + "&codice_modulo=" + codiceModulo;
  }
  if (codiceIstanza) {
    url = url + "&codice_istanza=" + codiceIstanza;
  }

  return url;

}