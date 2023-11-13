<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <main class="main container mt-4">
    <profile-section-title profile-btn class="px-2"></profile-section-title>

    <div class="row align-items-stretch">
      <div class="p-3 col-12 col-md-6" v-for="menu in menuList" :key="menu.urlBackoffice">
        <template v-if="menu.type === 'link'">
          <a class="card home-menu-card" :href="menu.url" target="_blank">
            <csi-icon :src="$svgs.getBootstrapIconsUrl(menu.icon)"></csi-icon>

            <span class="ps-3">{{ menu.nome }}</span>
          </a>
        </template>
        <template v-else-if="menu.type === 'route'">
          <router-link class="card home-menu-card" :to="menu.url">
            <csi-icon :src="$svgs.getBootstrapIconsUrl(menu.icon)"></csi-icon>
            <span class="ps-3">{{ menu.nome }}</span>
          </router-link>
        </template>
      </div>
    </div>
  </main>
</template>

<script>
import CsiCard from '../components/core/CsiCard.vue'
import CsiIcon from '../components/core/CsiIcon.vue'
import ProfileSectionTitle from '../components/profile/ProfileSectionTitle.vue'
import { TENANT, DASHBOARD_HOME, SERVICES, MENU } from '../router/routes'
export default {
  components: { CsiCard, ProfileSectionTitle, CsiIcon },
  name: 'HomePage',

  data() {
    return {
      TENANT,
      DASHBOARD_HOME
    }
  },
  computed: {
    tenantCms() {
      return this.tenant?.cms
    },
    tenant() {
      return this.$store.getters['getTenant']
    },
    assets() {
      return this.tenant?.assets ?? []
    },
    menuList() {
      let cms = []

      const configuration = [
        { nome: 'Configurazione servizi', url: SERVICES, type: 'route', icon: 'boxes' },
        { nome: "Soddisfazione d'uso", url: DASHBOARD_HOME, type: 'route', icon: 'emoji-smile' },
       
      ]
      if (this.tenantCms) {

        configuration.push( {
            nome: 'Sito web',
            url: this.tenantCms?.backOffice,
            type: 'link',
            icon: 'layout-text-window-reverse'
          })
       
      }else{
          configuration.push( { nome: 'Menu', url: MENU, type: 'route', icon: 'list' })
      }

      

      let assets = this.assets.map((a) => {
        return {
          nome: a.nome,
          url: a.urlBackoffice,
          type: 'link',
          icon: a.icon
        }
      })

      let menuList = [...configuration, ...assets]

      return menuList.filter((m) => !!m.url)
    }
  },

  methods: {}
}
</script>
