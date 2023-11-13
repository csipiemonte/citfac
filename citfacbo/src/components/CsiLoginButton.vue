<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <csi-dropdown btn-class="btn-primary btn-icon btn-full bg-transparent" id="loginButton">
    <template #buttonContent>
      <span aria-hidden="true" class="rounded-icon">
        <csi-icon color="primary" :src="$svgs.getSpriteSvgUrl('it-user')"></csi-icon>
      </span>
      <span class="d-none d-md-block">{{ userFullname }}</span>
    </template>
    <!-- CONTENUTO -->
    <template #dropdownContent>
      <li>
        <router-link
          role="option"
          ref="menu-item"
          @click="onCLick"
          class="dropdown-item list-item text-primary"
          :to="USER_PROFILE"
          >Profilo</router-link
        >
      </li>
      <li>
        <a
          role="option"
          ref="menu-item"
          class="list-item left-icon text-primary"
          :href="logoutUri"
          @click.prevent="goToLogout"
        >
          <span> Esci</span>
        </a>
      </li>
    </template>
  </csi-dropdown>
</template>

<script>
import { USER_PROFILE } from '../router/routes'
import { capitalize } from '../services/business-logic'
import CsiIcon from './core/CsiIcon.vue'

import CsiDropdown from './core/CsiDropdown.vue'
/* 
@type: tag usato per il bottone (può essere un "button", "a" (link) o "input")
@btnIcon: in caso di bottone con icona
@title: label del bottone dropdown

*/

export default {
  name: 'CsiLoginButton',
  components: { CsiIcon, CsiDropdown },
  props: {
    user: Object
  },
  data() {
    return {
      USER_PROFILE,
      isExpanded: false,
      positionTop: 0,
      positionLeft: 0
    }
  },
  computed: {
    userFullname() {
      let fullName = this.user ? `${this.user.nome} ${this.user.cognome}` : ''
      return capitalize(fullName)
    },
    id() {
      return 'loginButton'
    },
    targetId() {
      return this.id + 'Target'
    },
    activeBtnClasses() {
      let classes = []
      if (this.isExpanded) {
        classes.push('show')
      }
      return classes
    },
    positionStyle() {
      return { top: `${this.positionTop}px !important`, left: `${this.positionLeft}px` }
    },
    logoutUri() {
      let baseUrl = window.location.origin

      return `${baseUrl}/api-admin/logout`
    }
  },
  methods: {
    async goToLogout() {
      try {
        location.assign(this.logoutUri)
      } catch (e) {
        window.open(this.logoutUri, '_self')
      }
    },
    getMenuPosition() {
      let dropdownButton = document.getElementById(this.id)
      let distanceTop = 0
      let distanceLeft = 0
      if (dropdownButton) {
        // Cerchiamo la posizione sotto il bottone:
        let computedStyle = window.getComputedStyle(dropdownButton)
        let height = dropdownButton.clientHeight
        let paddingBottom = parseFloat(computedStyle.paddingBottom)
        distanceTop = height - paddingBottom + 14
        // Cerchiamo la posizione in centro:
        let width = dropdownButton.clientWidth
        let paddingLeft = parseFloat(computedStyle.paddingLeft)
        let paddingRight = parseFloat(computedStyle.paddingRight)
        distanceLeft = (width - paddingLeft - paddingRight) / 2
      }
      this.positionTop = distanceTop
      this.positionLeft = distanceLeft
    },
    toggleDropdown(event) {
      if (event?.relatedTarget) return
      this.isExpanded = !this.isExpanded
      this.getMenuPosition()
    },
    closeDropdown(event) {
      if (event?.relatedTarget) return
      this.isExpanded = false
    },
    onCLick() {
      this.isExpanded = false
    }
  }
}
</script>
