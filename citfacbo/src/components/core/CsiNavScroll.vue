<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="col-12 col-md-4 col-lg-3">
    <div :aria-labelledby="titleId" class="csi-nav-scroll">
      <nav class="" data-bs-navscroll>
        <div :id="navBarId" class="navbar-custom">
          <!-- HEADER DEL MENU -->
          <div class="accordion accordion-flush">
            <div class="accordion-item csi-accordion">
              <h3 class="accordion-header">
                <button
                  class="accordion-button collapsed"
                  type="button"
                  id="csi-nav-scroll__btn"
                  aria-controls="csi-nav-scroll__menu"
                  :aria-expanded="isOpen"
                  @click="toggleAccordion"
                >
                  Indice di pagina
                </button>
              </h3>
            </div>
          </div>
          <!-- PROGRESS BAR SULLO SCORRIMENTO DELLA PAGINA-->
          <div
            class="progress"
            role="progressbar"
            aria-label="stato dell'indice"
            :aria-valuenow="progressValue"
            aria-valuemin="0"
            aria-valuemax="100"
          >
            <div class="progress-bar bg-secondary" :style="{ width: progressValue + '%' }"></div>
          </div>
        </div>
        <!--  CONTENUTO DEL MENU-->
        <div
          v-show="isOpen"
          id="csi-nav-scroll__menu"
          class="accordion-collapse collapse show"
          role="region"
        >
          <div class="accordion-body">
            <ul class="link-list" data-element="page-index">
              <li v-for="(item, index) in menuItems" :key="index" class="nav-item">
                
                <router-link
                  
                  :to="item.target"
                  class="nav-link"
                  path-active-class="active"
                  active-class="active"
                  :path="item.exact"
                >
                  <span class="title-medium">{{ item.name }}</span>
                </router-link>
                <!--  SOTTOMENU -->
                <ul v-if="item.subMenu && item.subMenu.length > 0" class="link-sublist ps-3">
                  <li v-for="(subItem, index) in item.subMenu" :key="index">
                    

                    <router-link
                     
                      class="nav-link"
                      exact-active-class="active"
                      exact
                    >
                      <span class="title-medium">{{ subItem.name }}</span>
                    </router-link>
                  </li>
                </ul>
              </li>
            </ul>
          </div>
        </div>
      </nav>
    </div>
  </div>
</template>

<script>
/*
MENU LATERALE 
@menuItems : La lista delle voci del menù. Deve avere questa forma:
{target: '', nome: '', type:'', subMenu:{target:"", nome: "", type:''}} 
 - target: la url o hash di destinazione
 - nome: il nome della voce del menu
 - type: "route" se si tratta di una pagina diversa (route) ; 

 */
const LINK_TYPES = {
  ROUTER_LINK: 'route',
  HASH: 'hash'
}
export default {
  components: {},
  name: 'CsiNavScroll',
  props: {
    menuItems: { type: Array, default: () => [], required: true },
    id: String
  },
  data() {
    return {
      LINK_TYPES,
      hash: null,
      progressValue: 0,
      isOpen: true
    }
  },
  computed: {
    navBarId() {
      return `navbar-progress-${this.id}`
    },
    titleId() {
      return `accordion-title-${this.id}`
    },
    collapseId() {
      return `collapse-${this.id}`
    }
  },
  watch: {
 
  },
  created() {
    window.addEventListener('scroll', () => {
      let currentHash = this.hash
      let navTargets = document.querySelectorAll('.csi-nav-target')
      let navTargetsContainer = document.querySelector('.csi-nav-scroll-content')
      let position = window.scrollY
      let containerHeight = navTargetsContainer?.clientHeight - 130
      this.progressValue = (position / containerHeight) * 100

      navTargets.forEach((target) => {
        let targetOffsetTop = target.getBoundingClientRect().top + position - 20
        let targetOffsetBottom = target.clientHeight + targetOffsetTop
        let hash = target.id

        if (
          position < targetOffsetBottom &&
          position > targetOffsetTop &&
          currentHash !== '#' + hash
        ) {
        
          this.hash = hash
        }
      })
    })
  },
  methods: {
    onClickItem(hash) {
      window.location.hash = hash
    },
    toggleAccordion() {
     
      const menu = document.getElementById('csi-nav-scroll__menu')
      const button = document.getElementById('csi-nav-scroll__btn')

      if (button.classList.contains('collapsed')) {
        button.classList.add('showed')
        button.classList.remove('collapsed')
      } else {
        button.classList.add('collapsed')
        button.classList.remove('showed')
      }
      if (menu.classList.contains('show')) {
        menu.classList.add('collapse')
        menu.classList.remove('show')
      } else {
        menu.classList.add('show')
        menu.classList.remove('collapse')
      }

      this.isOpen = !this.isOpen
    }
  }
}
</script>

