/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright CSI Piemonte - 2023
 */

import {
  createRouter,
  createWebHashHistory,
  createWebHistory,
} from "vue-router";
import HomeView from "../views/HomeView.vue";
import routes from "./routes";
import { capitalizeFirst } from "../services/utils";
import { DEFAULT_META_TITLE } from "../services/config";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: routes,
});
router.beforeEach((to, from) => {
  document.title = to.meta?.title
    ? `${DEFAULT_META_TITLE} | ${to.meta.title}`
    : DEFAULT_META_TITLE;

  //In caso ci sia bisogno di aggiungere un meta title dinamico viene passato come param nella route
  if (to.params.metaTitle) {
    let title = to.params.metaTitle.replaceAll("-", " ");
    title = capitalizeFirst(title);
    document.title = `${document.title} - ${title}`;
  }
});



export default router;
