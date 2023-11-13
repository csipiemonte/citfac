/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 */

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import Vuelidate from '@vuelidate/core'

import App from './App.vue'
import router from './router'
import store from './store'


console.log('MODE:', import.meta.env.MODE)

 import './assets/scss/app.scss'


import globalSvgs from "./services/svgs"

const app = createApp(App)
app.config.globalProperties.$svgs = globalSvgs
app.use(createPinia())
app.use(router)
app.use(store)
app.use(Vuelidate);

window.fetch = async (...args) => {
  let [resource, config] = args

  if (!config) {
    config = {}
  }
  if (!config.headers) {
    config.headers = {}
  }



  const response = await originalFetch(resource, config)

  // response interceptor here
  return response
}
app.mount('#app')
