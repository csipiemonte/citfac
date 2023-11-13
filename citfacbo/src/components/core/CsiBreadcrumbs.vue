<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <nav aria-label="breadcrumb">
    <ol class="breadcrumb">
      <li class="breadcrumb-item">
        <router-link :to="HOMEPAGE">Home</router-link>
      </li>
      <li
        class="breadcrumb-item"
        v-for="(link, index) in links"
        :key="index"
        :class="{ active: isActive(index) }"
        :aria-current="isActive(index) ? 'page' : null"
      >
        <router-link :to="link.url">{{ link.name }}</router-link>
      </li>
    </ol>
  </nav>
</template>

<script>
import { HOMEPAGE } from '../../router/routes'
//PER COMPORRE IL BREADCRUMBS BASTA UN ARRAY DI OGGETTI {url: "/url/del/link", name: "nome del link"}  DA PASSARE Al
// PROPS "link"
export default {
  name: 'CsiBreadcrumbs',
  props: {
    links: { type: Array, default: () => [] }
  },
  data() {
    return {
      HOMEPAGE
    }
  },
 
  computed: {},
  methods: {
    isActive(index) {
      return index === this.links.length - 1
    }
  },
  async mounted() {}
}
</script>

