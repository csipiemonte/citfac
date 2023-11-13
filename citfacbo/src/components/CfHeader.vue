<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <csi-header class="bg-primary">
    <template #toolbar>
      <csi-toolbar class="bg-secondary">
        <div class="csi-navbar-brand">Città Facile - Pannello di controllo</div>

        <div role="navigation" v-if="user">
          <csi-dropdown btn-class="btn-primary btn-icon btn-full bg-transparent" id="helpButton">
    <template #buttonContent>
      <span aria-hidden="true" class="rounded-icon">
        <csi-icon color="primary" :src="$svgs.getBootstrapIconsUrl('question-lg')"></csi-icon>
      </span>
      <span class="d-none d-md-block">Aiuto</span>
    </template>
    <!-- CONTENUTO -->
    <template #dropdownContent>
       <li  class="menu-item">
        <router-link
         
          class="dropdown-item list-item text-primary"
          :to="ASSISTANCE_FORM"
          role="option"
          >Assistenza</router-link
        >
      </li>
      <li  class="menu-item">
        <a
         
          class="list-item left-icon text-primary"
          href="#"
          role="option"
        >
          <span> Manuale</span>
        </a>
      </li> 
    </template>
  </csi-dropdown>

          <router-link
            class="btn btn-primary btn-icon btn-full bg-transparent"
            id="tenantConfigure"
            ref="tenantConfigure"
            no-min-width
            :to="TENANT"
            title="Configurazione ente"
          >
            <span aria-hidden="true" class="rounded-icon">
               <csi-icon color="primary" :src="$svgs.getBootstrapIconsUrl('gear')"></csi-icon>
            </span>
            <span class="d-none d-md-block">Impostazioni</span>
          </router-link>

          <csi-login-button :user="user"></csi-login-button>
        </div>
      </csi-toolbar>
    </template>

    <router-link title="Vai alla homepage" to="/" class="py-2">
      <img :src="logoSrc" height="80"  width="80" />

      <h2 class="brand-title">{{ tenantName }}</h2>
    </router-link>
  </csi-header>
</template>

<script>
import CsiHeader from './core/CsiHeader.vue'
import CsiToolbar from './core/CsiToolbar.vue'
import CsiDropdown from './core/CsiDropdown.vue'
import CsiLoginButton from './CsiLoginButton.vue'
import { TENANT , ASSISTANCE_FORM} from '../router/routes'
import CsiIcon from './core/CsiIcon.vue'


export default {
  name: 'CfHeader',
  components: { CsiHeader, CsiToolbar, CsiDropdown, CsiLoginButton, CsiIcon },

  props: {},
  data() {
    return {
      TENANT,
      ASSISTANCE_FORM
    }
  },
  computed: {
    tenant() {
      return this.$store.getters['getTenant']
    },
    user() {
       return this.$store.getters['getUser']
    },
    tenantLogo() {
      return new URL(`/src/assets/images/it-pa.svg`, import.meta.url).href
    },
    tenantName() {
      if (this.tenant) {
        return `${this.tenant.nome ?? '-'}`
      } else {
        return 'Nuovo ente'
      }
    },
    logoSrc() {
      return "/admin/statics/img/logo-96.png";
    },
  }
}
</script>
