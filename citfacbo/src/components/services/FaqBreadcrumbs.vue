<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
 
      <ol class="breadcrumb col m-0" style="--bs-breadcrumb-divider: '|'">
        <li class="breadcrumb-item" v-for="(faq, index) in faqBreadcrumb" :key="faq.id">
          <template v-if="index === faqBreadcrumb.length - 1">
            <strong class="text-secondary">
              {{ faq.name }}
            </strong>
          </template>
          <template v-else>
            {{ faq.name }}
          </template>
        </li>
      </ol>
   
</template>

<script>
export default {
  name: 'CsiSeparator',
  props: {
    faqBreadcrumb: { type: Array, default: () => [] }
  },

}
</script>
