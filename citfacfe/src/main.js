/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 */

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import store from './store'
import App from './App.vue'
import router from './router'


import "./css/overrides.scss"

import globalFilters from "./services/filters";
import globalSvgs from "./services/svgs"

const devMode = import.meta.env.MODE == 'development';
console.log("MODE:",  import.meta.env.MODE);

const app = createApp(App)

app.config.globalProperties.$filters = globalFilters
app.config.globalProperties.$svgs = globalSvgs

//injected property "utente" is a ref and will be auto-unwrapped and no longer needs `.value` in the next minor release.
//To opt-in to the new behavior now, set `app.config.unwrapInjectedRef = true`
//(this config is temporary and will not be needed in the future.)
app.config.unwrapInjectedRef = true;

app.use(createPinia())
app.use(router)
app.use(store)

const { fetch: originalFetch } = window;

window.fetch = async (...args) => {
    let [resource, config ] = args;

    if(!config){
        config = {};
    }
    if(!config.headers){
        config.headers = {};
    }



    const response = await originalFetch(resource, config);

    // response interceptor here
    return response;
};


app.mount('#app')
