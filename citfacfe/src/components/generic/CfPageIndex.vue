<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div :aria-labelledby="titleId" class="cf-navscroll sticky-top">
    <nav
      class="navbar it-navscroll-wrapper navbar-expand-lg "
      
      :class="{ 'inline-menu': inline }"
      data-bs-navscroll
    >
      <div :id="navBarId" class="navbar-custom">
        <div class="menu-wrapper">
          <div class="link-list-wrapper">
            <div class="accordion">
              <div class="accordion-item">
                <div :id="titleId" class="accordion-header">
                  <button
                    :aria-controls="collapseId"
                    aria-expanded="true"
                    class="title pb-2 px-3"
                    :data-bs-target="'#' + collapseId"
                    data-bs-toggle="collapse"
                    type="button"
                  >
                    INDICE DI PAGINA
                     <svg class="icon right">
                      <use
                      :xlink:href="$svgs.getSpriteSvgUrl('it-expand')"
                     
                      ></use>
                    </svg> 
                  </button>
                </div>

                <div class="progress">
                  <div
                    aria-valuemax="100"
                    aria-valuemin="0"
                    aria-valuenow="0"
                    class="progress-bar it-navscroll-progressbar"
                    role="progressbar"
                  ></div>
                </div>
                <div
                  :id="collapseId"
                  :aria-labelledby="titleId"
                  class="accordion-collapse collapse show"
                  role="region"
                >
                  <div class="accordion-body">
                    <ul class="link-list" data-element="page-index">
                      <li
                        v-for="(item, index) in menuItems"
                        :key="index"
                        class="nav-item"
                      >
                        <a :href="'#' + item.target" class="nav-link">
                          <span >{{ item.nome }}</span>
                        </a>
                        <ul
                          v-if="item.subMenu && item.subMenu.length > 0"
                          class="link-list ps-3"
                        >
                          <li v-for="(subItem, index) in item.subMenu" :key="index" class="nav-link">
                            <a class="nav-link" :href="'#' + subItem.target"
                              ><span>{{ subItem.nome }}</span></a
                            >
                          </li>
                        </ul>
                      </li>
                    </ul>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </nav>
  </div>
</template>

<script>
// COMPONENTE PER NAVIGAZIONE INDICE DI PAGINA
// PER UTILIZZARE IL COMPONENTE USARE I PROPS
//"menuItems" LISTA DEGLI ELEMENTI DEL MENU {target: '', nome: ''}

export default {
  name: "CfPageIndex",
  props: {
    menuItems: { type: Array, default: () => [] }, //{target: '', nome: '', subMenu:{target:"", nome: ""}}
    id: String,
    inline: Boolean,
  },
  computed: {
    navBarId() {
      return `navbar-progress-${this.id}`;
    },
    titleId() {
      return `accordion-title-${this.id}`;
    },
    collapseId() {
      return `collapse-${this.id}`;
    },
  },
};
</script>


