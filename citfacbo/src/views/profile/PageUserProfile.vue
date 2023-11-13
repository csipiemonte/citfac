<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->



<template>
  <main class="main container mt-4">
    <profile-section-title></profile-section-title>


    <div>
      <strong><small>Ruolo</small></strong>
      <p>{{ userRole }}</p>
    </div>
  </main>
</template>
<script>
import ProfileSectionTitle from '../../components/profile/ProfileSectionTitle.vue'
export default {
  components: { ProfileSectionTitle },
  name: 'PageUserProfile',
  computed:{
    user() {
      return this.$store.getters['getUser']
    },
    userRole(){
      
      return this.user?.ruolo
    }
  }
}
</script>
