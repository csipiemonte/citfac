<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="main container mt-4 ">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h1 class="csi-h3 m-0">{{ this.$route.meta?.title ?? '-' }}</h1>
    </div>

    <template v-if="isLoading">
      <csi-inner-loading showing></csi-inner-loading>
    </template>

    <!-- MENU MAIN-->
    <!-- ------------------------------------------------------------------------------------------ -->

    <template v-else>
      <menu-list
        id="mainMenu"
        :menu="mainMenu"
        header="Menu principale"
        @on-update-menu="onUpdateMainMenu"
        :read-only="hasCms"
      ></menu-list>

      <menu-list
        id="footerMenu"
        :menu="footerMenu"
        header="Menu footer"
        @on-update-menu="onUpdateFooterMenu"
        :read-only="hasCms"
      ></menu-list>

      <menu-list
        id="topicsMenu"
        :menu="topicsMenu"
        header="Menu argomenti"
        @on-update-menu="onUpdateTopicsMenu"
        :read-only="hasCms"
      ></menu-list>
    </template>

    <div class="d-flex mt-4 justify-content-between" v-if="showSaveBtn">
      <div v-show="hasMenu">
        <csi-button outline color="danger" @click="onDelete">Elimina menu</csi-button>
      </div>
      <csi-button @click="onSave" :disabled="hasMenuItems"
        >Salva</csi-button
      >
    </div>
    <!-- SALVA MENU-->
    <csi-modal
      id="confirmModal"
      v-model="isOpenSaveModal"
      :title="titleModalMenu"
      @onConfirm="onConfirmMenu"
      :loading="isSavingSettings"
    >
      <p>
        <strong>Attenzione</strong><br />
        <span v-if="hasMenu"> Stai per modificare il menu.</span>
        <span v-else> Vuoi creare un nuovo menu.</span>
        Vuoi continuare?
      </p>
    </csi-modal>

    <!-- ELIMINAs MENU-->
    <csi-modal
      id="deleteModal"
      v-model="isOpenDeleteModal"
      title="Elimina menu"
      @onConfirm="onDeleteMenu"
      :loading="isDeleting"
      confirm-btn-class="btn-danger"
      confirm-label="Elimina menu"
    >
      <p>
        <strong>Attenzione</strong><br />
        Stai per eliminare il menu. Vuoi continuare?
      </p>
    </csi-modal>
  </div>
</template>

<script>
import CsiButton from '../../components/core/CsiButton.vue'
import CsiInnerLoading from '../../components/core/CsiInnerLoading.vue'

import CsiAlert from '../../components/core/CsiAlert.vue'
import { createMenu, deleteMenu, getMenuList, updateMenu } from '../../services/api'
import CsiAccordion from '../../components/core/CsiAccordion.vue'
import MenuTable from '../../components/menu/MenuTable.vue'
import CsiModal from '../../components/core/CsiModal.vue'
import MenuItemForm from '../../components/menu/MenuItemForm.vue'
import MenuList from '../../components/menu/MenuList.vue'
import { onShowNotificationToast } from '../../services/business-logic'

export default {
  components: {
    CsiInnerLoading,
    CsiButton,
    CsiAlert,
    CsiAccordion,
    MenuTable,
    CsiModal,
    MenuItemForm,
    MenuList
  },
  data() {
    return {
      isLoading: false,
      menu: null,
      mainMenu: [],
      footerMenu: [],
      topicsMenu: [],
      isSavingSettings: false,
      isDeleting: false,
      isOpenSaveModal: false,
      isOpenDeleteModal: false,
      hasMenu: false
    }
  },
  computed: {
    tenantId() {
      return this.$store.getters['getTenantId']
    },
    showSaveBtn() {
      return this.mainMenu.length > 0 || this.footerMenu.length > 0 || this.topicsMenu.length > 0 && !this.hasCms
    },
    titleModalMenu() {
      return this.hasMenu ? 'Modifica menu' : 'Crea menu'
    },
    hasCms() {
      return  this.$store.getters['getTenantCms']
    },
  },
  watch: {
    menu: {
      immediate: true,
      handler(val) {
   
        if (val) {
          this.hasMenu = true
          this.mainMenu = this.menu['main']
          this.footerMenu = this.menu['menu-footer']
          this.topicsMenu = this.menu['menu-argomenti']
        } else {
          this.hasMenu = false
          this.mainMenu = []
          this.footerMenu = []
          this.topicsMenu = []
        }
      }
    }
  },
  async mounted() {
    this.isLoading = true
    try {
      let { data: menu } = await getMenuList(this.tenantId)
      this.menu = menu
    } catch (e) {}
    this.isLoading = false
  },
  methods: {
    onUpdateMainMenu(menu) {
      this.mainMenu = menu
    },
    onUpdateFooterMenu(menu) {
      this.footerMenu = menu
    },
    onUpdateTopicsMenu(menu) {
      this.topicsMenu = menu
    },
    onSave() {
      this.isOpenSaveModal = true
    },
    onDelete() {
      this.isOpenDeleteModal = true
    },
    async onConfirmMenu() {
      this.isSavingSettings = true
      let menu = {
        main: this.mainMenu,
        'menu-footer': this.footerMenu,
        'menu-argomenti': this.topicsMenu
      }
      if (this.hasMenu) {
        await this.onEditMenu(menu)
      } else {
        await this.onCreateMenu(menu)
      }
      this.isSavingSettings = false
      this.isOpenSaveModal = false
    },
    async onCreateMenu(menu) {
      try {
        let { data } = await createMenu(this.tenantId, menu)
        this.menu = data
        await onShowNotificationToast({
          body: 'Menu creato con successo.',
          type: 'success'
        })
        this.hasMenu = true
      } catch (error) {
        await onShowNotificationToast({
          body: 'Non è stato possibile creare il menu.',
          type: 'error'
        })
      }
    },
    async onEditMenu(menu) {
      try {
        let { data } = await updateMenu(this.tenantId, menu)
        this.menu = data
        await onShowNotificationToast({
          body: 'Menu modificato con successo.',
          type: 'success'
        })
      } catch (error) {
        await onShowNotificationToast({
          body: 'Non è stato possibile modificare il menu.',
          type: 'error'
        })
      }
    },
    async onDeleteMenu() {
      this.isDeleting = true
      try {
        let { data } = await deleteMenu(this.tenantId)
        await onShowNotificationToast({
          body: 'Menu eliminato con successo.',
          type: 'success'
        })
        this.menu = null
      } catch (error) {
        await onShowNotificationToast({
          body: 'Non è stato possibile modificare il menu.',
          type: 'error'
        })
      }

      this.isDeleting = false
      this.isOpenDeleteModal =false
    }
  }
}
</script>

