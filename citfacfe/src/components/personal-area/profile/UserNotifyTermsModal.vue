<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <cf-modal maximized title="Termini e condizioni d'uso"  id="modal-user-notify-terms" ref="modalUserNotifyTerms">
    <div class="mb-5">
      <cf-inner-loading :showing="isLoading" class="my-4"/>

      <div v-show="!isLoading">
        <div class="privacy-terms" v-html="privacyTermsHtml"></div>
      </div>
    </div>


  </cf-modal>
</template>

<script>
import CfModal from "../../generic/CfModal.vue";
import CfInnerLoading from "../../generic/CfInnerLoading.vue";
import {getNotifyTerms} from "../../../services/api";
export default {
name: "UserNotifyTermsModal",
  components: {CfInnerLoading, CfModal},
  data(){
  return{
    isLoading:false,
    privacyTermsHtml:null
  }
  },
  async created() {
    this.isLoading = true
    try {
      let {data: terms} = await getNotifyTerms()
      this.privacyTermsHtml = terms
    } catch (e) {

      await this.$store.dispatch("setNotificationInfo", {title:"Attenzione", body:"Non è stato possibile recuperare l'informativa, si prega di riprovare"})

    } finally {
      this.isLoading = false
    }
  }
}
</script>

