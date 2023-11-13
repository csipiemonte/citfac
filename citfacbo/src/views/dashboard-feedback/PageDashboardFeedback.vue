<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  
 <!-- COMPONENTE CONTENITORE -->
    <router-view v-slot="{ Component }" >
      <keep-alive>
        <component :is="Component" :key="$route.path" />
      </keep-alive>
    </router-view>

</template>

<script>

export default {
  components: {  },
  name: 'PageDashboardFeedback',

  data() {
    return {
     
    }
  },
  computed: {
  
  },
  methods: {
    
  }
}
</script>
