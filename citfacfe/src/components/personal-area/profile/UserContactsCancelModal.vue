<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <cf-modal id="modal-user-notify-cancel" ref="modalUserNotifyCancel" title="Conferma eliminazione contatti">
    <cf-alert title="Attenzione!">
      <p class="mb-0">Eliminando i tuoi contatti non potrai ricevere notifiche via e-mail e via SMS.</p>
    </cf-alert>

    <template #actions>
      <button class="btn btn-primary btn-modal-action" type="button" @click="onConfirm" data-bs-dismiss="modal">Elimina tutti i contatti
      </button>
      <button class="btn btn-outline-primary btn-modal-action " data-bs-dismiss="modal" type="button">Annulla</button>

      <cf-inner-loading :showing="isRemovingContacts"/>
    </template>


  </cf-modal>
</template>

<script>
import CfModal from "../../generic/CfModal.vue";
import CfAlert from "../../generic/CfAlert.vue";
import {deleteUserContacts} from "../../../services/api";
import CfInnerLoading from "../../generic/CfInnerLoading.vue";


export default {
  name: "UserContactsCancelModal",
  components: {CfInnerLoading, CfAlert, CfModal},
  data() {
    return {
      isRemovingContacts: false
    }
  },
  computed: {
    userTaxCode() {
      let user = this.$store.getters['getUser']
      return user?.codFisc ?? null
    },
  },
  methods: {
    async onConfirm() {
      this.isRemovingContacts = true

      try {
        let response = await deleteUserContacts(this.userTaxCode)
        this.$store.dispatch('setUserContacts', {contacts: null})

        await this.$store.dispatch("setNotificationInfo", {body:"Contatti eliminati con successo", positive:true})

      } catch (e) {
        await this.$store.dispatch("setNotificationInfo", {body:"Non è stato possibile eliminare i contatti, si prega di riprovare"})
      }finally{
        this.isRemovingContacts = false
      }


    },

  }
}
</script>


