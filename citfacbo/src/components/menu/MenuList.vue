<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="accordion menu-list">
    <csi-accordion class="" expand-default :id="'menuList-' + id">
      <template #header>{{ header }} </template>

      <template #body>
        <table class="menu-table table" v-if="menuList?.length > 0">
          <thead>
            <tr>
              <th scope="col">Nome menu</th>
              <th scope="col">Url</th>
              <th scope="col">Data element</th>
              <th scope="col"></th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(item, index) in menuList" :key="index">
              <td class="col-4">{{ item.title }}</td>
              <td class="col-4">{{ item.url }}</td>
              <td class="col">{{ getDataElement(item) || '-' }}</td>

              <td style="text-align: right" class="col-auto" v-if="!readOnly">
                <csi-button
                  type="button"
                  no-label
                  outline
                  class="text-right col-auto p-1 mx-2"
                  data-bs-toggle="tooltip"
                  data-bs-placement="top"
                  data-bs-title="This top tooltip is themed via CSS variables."
                  @click.prevent="onEditItem(item, index)"
                  aria-label="modifica"
                  title="modifica"
                  :icon="$svgs.getBootstrapIconsUrl('pencil')"
                  no-min-width
                >
                </csi-button>

                <csi-button
                  type="button"
                  no-label
                  color="danger"
                  outline
                  class="text-right col-auto p-1"
                  @click.prevent="onDeleteItem(item, index)"
                  aria-label="elimina"
                  title="elimina"
                  :icon="$svgs.getBootstrapIconsUrl('trash')"
                  no-min-width
                >
                </csi-button>
              </td>
            </tr>
          </tbody>
        </table>

        <csi-alert type="info" v-else class="mb-4">Nessuna voce nel menu</csi-alert>

        <div class="d-flex justify-content-end" v-if="!readOnly">
          <button class="btn btn-outline-primary" @click="onCreateItem">Aggiungi voce</button>
        </div>

        <menu-item-form
          v-model="isOpenMenuItemModal"
          :menu-item="selectedItem"
          @on-edit="editMenuItem"
          @on-create="createMenuItem"
        />

        <!-- CANCELLA ITEM -->

        <csi-modal
          id="deleteItemModal"
          v-model="isOpenDeleteItemModal"
          title="Cancella voce del menu"
          @onConfirm="deleteMenuItem"
        >
          <p>
            <strong>Attenzione</strong><br />
            Vuoi cancellare la voce del menu <strong>{{ selectedItem.title }}</strong> ?
          </p>
        </csi-modal>
      </template>
    </csi-accordion>
  </div>
</template>

<script>
import CsiAccordion from '../core/CsiAccordion.vue'
import CsiAlert from '../core/CsiAlert.vue'
import CsiButton from '../core/CsiButton.vue'
import CsiCard from '../core/CsiCard.vue'
import CsiModal from '../core/CsiModal.vue'
import MenuItemForm from './MenuItemForm.vue'
import MenuTable from './MenuTable.vue'
export default {
  components: { CsiButton, MenuItemForm, CsiAccordion, MenuTable, CsiAlert, CsiModal, CsiCard },
  name: 'MenuList',
  props: {
    header: { type: String, default: '' },
    menu: { type: Array, default: () => [] },
    id: { type: String, default: '' },
    readOnly: { type: Boolean, default: false }
  },
  emits: ['onUpdateMenu'],
  data() {
    return {
      isOpenMenuItemModal: false,
      isOpenDeleteItemModal: false,
      selectedItem: null,
      selectedIndex: null,
      menuList: []
    }
  },
  mounted() {
    this.menuList = this.menu
  },
  watch: {
    menuList: {
      deep: true,
      handler(val) {
        this.$emit('onUpdateMenu', val)
      }
    }
  },
  methods: {
    onEditItem(item, index) {
      this.selectedItem = item
      this.selectedIndex = index
      this.isOpenMenuItemModal = true
    },
    onDeleteItem(item, index) {
      this.selectedItem = item
      this.selectedIndex = index
      this.isOpenDeleteItemModal = true
    },
    editMenuItem(item) {
      this.menuList[this.selectedIndex] = item
    },
    deleteMenuItem() {
      this.menuList.splice(this.selectedIndex, 1)
      this.isOpenDeleteItemModal = false
    },
    createMenuItem(item) {
      this.menuList.push(item)
    },
    onCreateItem() {
      this.selectedItem = null
      this.selectedIndex = null
      this.isOpenMenuItemModal = true
    },
    getDataElement(item) {
      return item?.atributes ? item?.atributes['data-element'] : ''
    }
  }
}
</script>
