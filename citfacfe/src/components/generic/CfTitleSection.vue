<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div id="cf-title-section" class="container ">
    <div class="row justify-content-center" >
      <breadcrumbs :links="breadcrumbs"/>
      <div class="col-12 col-lg-10">
        <div class="cf-hero">
          <section class="it-hero-wrapper bg-white align-items-start">


            <div class="it-hero-text-wrapper p-0 ">
              <div class="row justify-content-start align-items-center">
                <div class="col-auto">
                  <h1 class="text-black hero-title  mb-0 pb-2" data-element="page-name">
                    <slot name="title"></slot>
                  </h1>
                </div>


                <div class="col" >
                  <slot name="extras"></slot>
                </div>
              </div>


              <template v-if="subtitle">
                <div class="it-category fw-bolder">
                  <slot name="subtitle"></slot>
                </div>
              </template>
            </div>
          </section>
        </div>
      </div>
    </div>

  </div>
</template>


<script>



// COMPONENTE "TITOLO DI PAGINA"
// PER UTILIZZARE IL COMPONENTE USARE I PROPS
//"breadcrumbs" EVENTUALE LISTA DI BREADCRUMBS {url: "/url/del/link", name: "nome del link"}
//"title" TITOLO DELLA PAGINA
//"subtitle" EVENTUALE SOTTOTITOLO DELLA PAGINA

import Breadcrumbs from "./Breadcrumbs.vue";
export default {
  name: "CfTitleSection",
  components: {Breadcrumbs},
  props:{
    breadcrumbs:{type:Array , default : ()=>[]},
    subtitle:Boolean
  },

}
</script>

