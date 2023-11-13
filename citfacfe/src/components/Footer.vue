<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<script>
import { NEW_REQUEST, SERVICE_APPOINTMENT_BOOKING, FAQ_HOME } from "../router/routes";

export default {
  name: "Footer",
  data() {
    return {
      SERVICE_APPOINTMENT_BOOKING,
      NEW_REQUEST,
      FAQ_HOME
    };
  },
  props: {
    msg: {
      type: String,
      required: false,
    },
  },
  computed: {
    getComuneConfig() {
      return this.$store.getters["getComuniConfig"];
    },
    getComuneUrl() {
      return this.getComuneConfig?.homePage ?? "";
    },
    getComuneName() {
      return this.getComuneConfig?.nome;
    },
    logoSrc() {
      return new URL("/statics/img/logo-96.png", import.meta.url);
    },
  },

  
};
</script>

<template>
  <footer class="it-footer" id="footer">
    <div class="it-footer-main">
      <div class="container">
        <div class="row">
          <div class="col-12 footer-items-wrapper logo-wrapper">
            <img
              class="ue-logo"
              style="min-width: 300px"
              src="../assets/images/logo-eu-inverted.svg"
              alt="logo Unione Europea"
            />
            <div class="it-brand-wrapper">
              <a
                :href="getComuneUrl"
                title="Vai alla homepage"
                :aria-label="`homepage del comune di ${getComuneName}`"
                class="logo-hamburger"
              >
              <img
                      :src="logoSrc"
                      height="80"
                      
                      width="80"
                    />
                <div class="it-brand-text">
                  <h2 class="it-brand-title">{{ getComuneName }}</h2>
                </div>
              </a>
            </div>
          </div>
        </div>
        <div class="row">

          <div class="col footer-items-wrapper">
        
            <h3 class="footer-heading-title p-0"></h3>
            <div class="row justify-content-between">
         
             
              <div class="col-auto row">
                <div class="col-md-auto col-12">
                  <router-link :to="FAQ_HOME" data-element="faq"
                    >Leggi le FAQ</router-link
                  >
                </div>
                <div
                  class="col-md-auto col-12"
                 
                >
                  <router-link :to="SERVICE_APPOINTMENT_BOOKING"   data-element="appointment-booking" >Prenota un appuntamento</router-link>
                </div>

                <div class="col-md-auto col-12">
                  <router-link :to="NEW_REQUEST"
                    >Richiedi assistenza</router-link
                  >
                </div>
              </div>
          
              <div class="col-auto row pb-3">
                <div class="col-md-auto col-12">
                  <a href="#" data-element="privacy-policy-link"
                    >Informativa privacy</a
                  >
                </div>
                <div class="col-md-auto col-12">
                  <a href="#" data-element="legal-notes">Note legali</a>
                </div>
                <div class="col-md-auto col-12">
                  <a href="#" data-element="accessibility-link"
                    >Dichiarazione di accessibilità</a
                  >
                </div>
              </div>
            
            </div>
          </div>
          
        </div>

      </div>
    </div>
  </footer>
</template>
