/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 */

const globalSvgs = {
  /*Funzione che consente le chiamate a risorse statiche esterne al progetto*/
  getSpriteSvgUrl(code) {
    const imageUrl = new URL("/statics/svg/sprites.svg", import.meta.url).href;


    return `/statics/svg/sprites.svg#${code}`
  },
  getBootstrapIconsUrl(code) {

    return `/statics/svg/bootstrap-icons.svg#${code}`
  },
};

export default globalSvgs;
