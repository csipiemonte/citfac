<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  
  <main class="main container page-dashboard mt-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1 class="csi-h3 m-0" id="dashboard-feedback">{{ this.$route.meta?.title ?? '-' }}</h1>
    </div>

    <div class="page-dashboard">
      <CsiRouterTabs :tabs="routerTabs" id="rtabs"></CsiRouterTabs>

      <router-view v-slot="{ Component }" >
        <keep-alive>
          <component :is="Component"  />
          
        </keep-alive>
      </router-view>
    </div>
  </main>
</template>

<script>
import CsiInnerLoading from '../../components/core/CsiInnerLoading.vue'
import CsiTabs from '../../components/core/CsiTabs.vue'
import CsiTabPanel from '../../components/core/CsiTabPanel.vue'
import CsiRouterTabs from '../../components/core/CsiRouterTabs.vue'
import {
  DASHBOARD_HOME,
  DASHBOARD_PAGES,
  DASHBOARD_PAGE_DETAILS,
  DASHBOARD_SERVICES
} from '../../router/routes'

const TABS_MAP = {
  PAGES: 'pagine',
  SERVICES: 'servizi'
}
const TABS = [
  { target: TABS_MAP.PAGES, nome: 'Pagine portale' },
  { target: TABS_MAP.SERVICES, nome: 'Servizi' }
]

export default {
  components: {
    CsiInnerLoading,
    CsiTabs,
    CsiTabPanel,
    CsiRouterTabs
  },
  name: 'PageFeedbackHome',
  data() {
    return {
      TABS,
      TABS_MAP,
      routerTabs: [
        { target: DASHBOARD_PAGES, nome: 'Pagine portale' },
        { target: DASHBOARD_SERVICES, nome: 'Servizi' }
      ],
      isLoading: false,
      isOpen: true,
      progressValue: 0,
      selectedTab: TABS_MAP.PAGES,
      isLoading: false,
      pagesList: [],
      servicesList: [],
      filteredPagesList: [],
      filteredServicesList: [],
      period: null,
      selectedItem: null
    }
  },
  created() {
    // // this.handleNavigation(this.$router)
  },

  methods: {
    onSelectPage(page) {
      let route = {
        name: DASHBOARD_PAGE_DETAILS.name,
        params: {
          url: encodeURI(page.pagePath)
        }
      }
      this.$router.push(route)
    },
    async handleNavigation(to, from, next) {
      // Quando la route target è proprio la home reindirizziamo verso le pagine
      if (to.name === DASHBOARD_HOME.name) {
        next ? next(route) : this.$router.push(DASHBOARD_PAGES)
      } else if (next) {
        next()
      }
    }
  }
}
</script>
