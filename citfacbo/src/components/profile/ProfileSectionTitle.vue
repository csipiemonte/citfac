<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <section id="profile-section-title" class="align-items-star mb-4">
    <div class="row justify-content-start align-items-center gap-2 pb-2">
      <div class="col-12 col-md-auto">
        <h1 class="mb-0" data-element="page-name">{{ userFullname }}</h1>
      </div>

      <div class="col" v-if="profileBtn">
        <router-link :to="USER_PROFILE" class="btn btn-icon btn-link " id="profile-btn">
          <span class="me-2">
            <csi-icon color="primary" :src="$svgs.getBootstrapIconsUrl('person-circle')"></csi-icon>
          </span>
          <span>Profilo</span>
        </router-link>
      </div>
    </div>
    <div class="profile-section-title__taxcode">CF: {{ user.codFisc }}</div>
  </section>
</template>

<script>
import { capitalize } from '../../services/business-logic'
import { USER_PROFILE } from '../../router/routes'
import CsiIcon from '../core/CsiIcon.vue'


export default {
  name: 'ProfileSectionTitle',
  components: {CsiIcon},
  props:{
    profileBtn:Boolean
  },
  data() {
    return {
      USER_PROFILE
    }
  },
  computed: {
    user() {
      return this.$store.getters['getUser']
    },
    userFullname(){
      let fullname =  this.user ? `${this.user.nome} ${this.user.cognome}` : ''
      return  capitalize(fullname)
    }
  }
}
</script>
