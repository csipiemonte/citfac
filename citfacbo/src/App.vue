<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div id="app">
    <template v-if="isLoading">
      <csi-inner-loading showing></csi-inner-loading>
    </template>

    <template v-else>
      <CfHeader></CfHeader>
      <div class="error-tenant d-grid container text-center" v-if="tenantError">
        <CsiAlert type="error" class="my-4"> Errore caricamento tenant </CsiAlert>
      </div>
      <template v-else>
        <div class="container" v-if="!isHomepage">
          <csi-breadcrumbs class="mt-4" :links="breadcrumbsLinks"></csi-breadcrumbs>
        </div>
        <assets-missing-alert />
        <RouterView />
      </template>
      <CfFooter />
    </template>

    <csi-toast-list :toasts="notificationToasts"></csi-toast-list>
  </div>
</template>

<script>
import { getAssets, getTenant, getUser } from './services/api'
import CfHeader from './components/CfHeader.vue'
import CfFooter from './components/CfFooter.vue'

import CsiInnerLoading from './components/core/CsiInnerLoading.vue'
import CsiAlert from './components/core/CsiAlert.vue'
import CsiBreadcrumbs from './components/core/CsiBreadcrumbs.vue'
import { ERROR_404, HOMEPAGE } from './router/routes'
import AssetsMissingAlert from './components/AssetsMissingAlert.vue'
import CsiToastList from './components/core/CsiToastList.vue'

export default {
  name: 'App',
  components: {
    CfHeader,
    CfFooter,

    CsiInnerLoading,
    CsiAlert,
    CsiBreadcrumbs,
    AssetsMissingAlert,
    CsiToastList
  },
  data() {
    return {
      isLoading: false,
      tenantError: false
    }
  },
  async mounted() {
    this.isLoading = true
    //Chiamata get user

    //Tenant temporaneo in dev
    let id = 0
    await this.$store.dispatch('setTenantId', id)

    try {
      let { data: user } = await getUser()
      await this.$store.dispatch('setUser', { user })
    } catch (error) {}

    if (!this.user) return
    let tenantPromise = getTenant(this.tenantId)
    let assetsPromise = getAssets()
    try {
      let { data: tenant } = await tenantPromise
      await this.$store.dispatch('setTenant', { tenant })
    } catch (error) {
      if (error.request?.status !== 404) {
        //    this.tenantError = true
      }
    }

    try {
      let { data: assets } = await assetsPromise

      await this.$store.dispatch('setAssets', { assets })
    } catch (error) {
      console.error(error)
    }

    if (this.tenant) {
      let cms = this.tenant.cms
      await this.$store.dispatch('setTenantCms', { cms })
      await this.$store.dispatch('updateServiceList')
    }
    this.isLoading = false
  },
  computed: {
    user() {
      return this.$store.getters['getUser']
    },
    notificationToasts() {
      return this.$store.getters['getNotificationToasts']
    },
    tenantId() {
      return this.$store.getters['getTenantId']
    },
    tenant() {
      return this.$store.getters['getTenant']
    },
    breadcrumbsLinks() {
      let breadcrumbs = this.$route.meta?.breadcrumbs ?? []

      return breadcrumbs.map((item) => {
        return { url: item, name: item.meta.title }
      })
    },
    isHomepage() {
      return this.$route.name === HOMEPAGE.name || this.$route.name === ERROR_404.name
    }
  }
}
</script>
