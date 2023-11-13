/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 */

const globalSvgs = {
  /*Funzione che consente le chiamate a risorse state esterne al progetto*/
  getSpriteSvgUrl(code) {
    return `/admin/statics/svg/sprites.svg#${code}`
  },
  getBootstrapIconsUrl(code) {
    return `/admin/statics/svg/bootstrap-icons.svg#${code}`
  },
  getAlertSvg(type) {
    let path = ''
    switch (type) {
      case 'success':
        path =
          'M12 2a10 10 0 1 0 10 10A10 10 0 0 0 12 2zm0 19a9 9 0 1 1 9-9 9 9 0 0 1-9 9zm-.5-6.8V5.7h1.2v8.5zm-.1 2.3h1.2v1.8h-1.2z'
        break
      case 'info':
        path =
          'M12 2a10 10 0 1 0 10 10A10 10 0 0 0 12 2zm0 19a9 9 0 1 1 9-9 9 9 0 0 1-9 9zm-.7-15h1.5v2h-1.5zm0 3h1.5v9h-1.5z'
        break
      case 'warning':
        path =
          'M12 2a10 10 0 1 0 10 10A10 10 0 0 0 12 2zm0 19a9 9 0 1 1 9-9 9 9 0 0 1-9 9zm-.5-6.8V5.7h1.2v8.5zm-.1 2.3h1.2v1.8h-1.2z'
        break
      case 'error':
        path =
          'M11.5 14.2V5.7h1.2v8.5zm-.1 4.1h1.2v-1.8h-1.2zM22 7.9v8.3L16.1 22H7.9L2 16.2V7.9L7.9 2h8.2zm-1 .4L15.7 3H8.3L3 8.3v7.5L8.3 21h7.4l5.3-5.2z'
        break
      default:
        path = ''
      
    }
    return path
  }
}

export default globalSvgs


