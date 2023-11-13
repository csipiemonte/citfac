/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 */

export const ERROR_404 = {
  path: '/:pathMatch(.*)*',
  name: 'error404',
  component: () => import('../views/PageError404.vue')
}

export const TENANT = {
  path: '/impostazioni-ente',
  name: 'tenant',
  component: () => import('../views/tenant/PageTenant.vue'),
  meta: {
    title: 'Impostazioni ente',
    showAssetMissingAlert: true
  }
}

export const ASSISTANCE_FORM = {
  path: '/assistenza/nuovo',
  name: 'assistanceForm',
  component: () => import('../views/assistance/PageAssistanceForm.vue'),
  meta: {
    title: 'Assistenza',

    showAssetMissingAlert: true
  }
}

export const SERVICES = {
  path: '/configurazione-servizi',
  name: 'services',
  component: () => import('../views/services/PageServices.vue'),
  meta: {
    title: 'Configurazione servizi',
    showAssetMissingAlert: true
  }
}

export const MENU = {
  path: '/configurazione-menu',
  name: 'menu',
  component: () => import('../views/menu/PageMenu.vue'),
  meta: {
    title: 'Configurazione menu',
    showAssetMissingAlert: true
  }
}
export const DASHBOARD_PAGES = {
  path: 'pagine',
  name: 'dashboard-pages',
  component: () => import('../views/dashboard-feedback/PageFeedbackPages.vue'),
  meta: {
    title: "Soddisfazione d'uso"
  }
}

export const DASHBOARD_SERVICES = {
  path: 'servizi',
  name: 'dashboard-services',
  component: () => import('../views/dashboard-feedback/PageFeedbackServices.vue'),
  meta: {
    title: "Soddisfazione d'uso"
  }
}

export const DASHBOARD_HOME = {
  path: '',
  name: 'dashboard-home',
  component: () => import('../views/dashboard-feedback/PageFeedbackHome.vue'),
  redirect: DASHBOARD_PAGES,
  meta: {
    title: "Soddisfazione d'uso"
  },
  children: [DASHBOARD_PAGES, DASHBOARD_SERVICES]
}

export const DASHBOARD_PAGE_DETAILS = {
  path: 'pagine/:url/:slug',
  name: 'dashboard-page-detail',
  component: () => import('../views/dashboard-feedback/PageFeedbackPageDetail.vue'),
  meta: {
    title: "Soddisfazione d'uso - Pagine",
    parents: [DASHBOARD_PAGES]
  }
}

export const DASHBOARD_SERVICES_DETAILS = {
  path: 'servizi/:id/:slug',
  name: 'dashboard-service-detail',
  component: () => import('../views/dashboard-feedback/PageFeedbackServiceDetail.vue'),
  meta: {
    title: "Soddisfazione d'uso - Servizi",
    parents: [DASHBOARD_SERVICES]
  }
}

export const DASHBOARD = {
  path: '/dashboard-feedback/',
  name: 'dashboard-feedback',
  component: () => import('../views/dashboard-feedback/PageDashboardFeedback.vue'),
  redirect: DASHBOARD_HOME,
  meta: {
    title: "Soddisfazione d'uso"
  },
  children: [DASHBOARD_HOME, DASHBOARD_PAGE_DETAILS, DASHBOARD_SERVICES_DETAILS]
}

export const HOMEPAGE = {
  path: '/',
  name: 'home',
  component: () => import('../views/HomePage.vue'),
  meta: {
    showAssetMissingAlert: true
  }
}

export const USER_PROFILE = {
  path: '/profilo-utente',
  name: 'user-profile',
  component: () => import('../views/profile/PageUserProfile.vue'),
  meta: {
    title: 'Profilo utente'
  }
}

export const SERVICE_NEW = {
  path: '/configurazione-servizi/nuovo',
  name: 'serviceNew',
  component: () => import('../views/services/PageServiceNew.vue'),
  meta: {
    title: 'Nuovo Servizio',
    parents: [SERVICES],
    showAssetMissingAlert: true
  }
}

export const SERVICE_EDIT = {
  path: '/configurazione-servizi/:id/:slug',
  name: 'serviceEdit',
  component: () => import('../views/services/PageServiceEdit.vue'),
  meta: {
    title: '',
    parents: [SERVICES],
    showAssetMissingAlert: true
  }
}

const routes = [
  ERROR_404,
  HOMEPAGE,
  TENANT,
  SERVICES,
  MENU,
  DASHBOARD,
  SERVICE_NEW,
  SERVICE_EDIT,
  USER_PROFILE,
  ASSISTANCE_FORM
]

export default routes
