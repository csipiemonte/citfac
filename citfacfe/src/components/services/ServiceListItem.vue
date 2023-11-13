<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div id=""
       class="cf-card cf-card-latest-messages mb-4">
    <div class="card shadow-sm px-4 pt-4 pb-4 rounded">
      <div class="card-header border-0 p-0">
        <a class="text-decoration-none  mb-2 category text-uppercase" data-element="service-category-link"
           href="#">{{ category }}</a>
      </div>
      <div class="card-body p-0 my-2">
        <h3 class="green-title-big text-primary">
          <RouterLink :to="url" class="text-decoration-none"
                      data-element="service-link">{{ title }}
          </RouterLink>
        </h3>
        <p class="text-paragraph">
          <slot/>
        </p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "ServiceListItem",
  props: {
    category: String,
    title: String,
    url: [String,Object],

  },

}
</script>



