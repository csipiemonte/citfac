<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <table class="menu-table table table-responsive">
    <thead>
      <tr>
        <th scope="col">Nome menu</th>
        <th scope="col">Url</th>
        <th scope="col">Data element</th>
        <th scope="col"></th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="(item, index) in menu" :key="index">
        <td class="col-4">{{ item.title }}</td>
        <td class="col-4">{{ item.url }}</td>
        <td class="col">{{ getDataElement(item) || '-' }}</td>

        <td style="text-align: right" class="col-auto">
          <button
            type="button"
            class="btn btn-outline-primary text-right  p-1 mx-3"
            @click.prevent="onChange(item, index)"
            aria-label="modifica"
            title="modifica"
          >
            <i class="bi bi-pencil"></i>
          </button>

          <button
            type="button"
            class="btn btn-outline-danger text-right  p-1"
            @click.prevent="onDelete(item, index)"
            aria-label="elimina"
            title="elimina"
          >
            <i class="bi bi-trash"></i>
          </button>
        </td>
      </tr>
    </tbody>
    <template v-if="isOpenChangeModal">
      <menu-item-form v-model="isOpenChangeModal" :menu-item="selectedItem" @on-confirm="editMenuItem" />
    </template>

    
  </table>
</template>

<script>
import CsiButton from '../core/CsiButton.vue'
import MenuItemForm from './MenuItemForm.vue'
export default {
  components: { CsiButton, MenuItemForm },
  name: 'MenuTable',
  props: {
   menu:{type:Array, default: ()=>[]}
  },
  emits:['onEdit', 'onDelete'],
  data(){
    return {
      isOpenChangeModal:false,
      selectedItem: null,
      selectedIndex: null
    }
   
  },
  created(){
   
  },
  methods:{ 
    onChange(item, index){
      this.selectedItem = item
      this.selectedIndex = index
      this.isOpenChangeModal = true
    },
    onDelete(item, index){

    },
    editMenuItem(item){
        this.$emit('onEdit', item, this.selectedIndex)
    },
    getDataElement(item){
      return item?.atributes ? item?.atributes['data-element'] : ''
    }
  }

}
</script>

