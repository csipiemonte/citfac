/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 */

import { createRouter, createWebHistory, createWebHashHistory } from 'vue-router'
import routes from './routes'
import { slugToTitle } from '../services/business-logic'
const DEFAULT_TITLE = 'Configuratore Città Facile'
const router = createRouter({
  history: createWebHashHistory(import.meta.env.BASE_URL),
  routes: routes
})
router.beforeEach((to, from) => {

  //In caso ci sia bisogno di aggiungere un meta title dinamico viene passato come param nella route
  if (to.params.slug) {
    let slug = slugToTitle(to.params.slug)
    to.meta.title = slug
  }
  document.title = to.meta?.title ? `${DEFAULT_TITLE} | ${to.meta.title}` : DEFAULT_TITLE

  let breadcrumbs = to.meta?.parents ? [...to.meta?.parents] : []
  breadcrumbs.push(to)

  to.meta.breadcrumbs = breadcrumbs
 
})
export default router
