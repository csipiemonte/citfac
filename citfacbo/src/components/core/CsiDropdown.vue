<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="csi-dropdown dropdown" @blur.capture="handleBlur">
    <button
      class="csi-dropdown-button btn m-0"
      :id="id"
      :ref="id"
      :aria-expanded="isExpanded"
      :class="activeBtnClasses"
      aria-haspopup="listbox"
      data-toggle="dropdown"
      role="menu"
      tabindex="1"
      @click="toggleDropdown"
      data-bs-toggle="dropdown"
      @keyup.up.down.prevent="showOptions"
    >
      <slot name="buttonContent"></slot>
    </button>

    <Transition name="easy-out">
      <div
        v-show="isExpanded"
        :id="targetId"
        :aria-labelledby="id"
        :class="{ show: isExpanded }"
        class="dropdown-content"
      >
        <div class="link-list-wrapper">
          <ul
            :ref="optionsContainerRef"
            class="link-list"
            tabindex="-1"
            role="listbox"
            @keyup.up.prevent="selectPrevOption"
            @keyup.down.prevent="selectNextOption"
            @keydown.up.down.prevent
            @keydown.enter.esc.prevent="reset"
          >
            <slot name="dropdownContent"></slot>
          </ul>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script>
/* 
@type: tag usato per il bottone (può essere un "button", "a" (link) o "input")
@btnIcon: in caso di bottone con icona
@title: label del bottone dropdown

*/

export default {
  name: 'CsiDropdown',
  components: {},
  props: {
    id: String,
    btnClass: { type: String, default: '' }
  },
  data() {
    return {
      isExpanded: false,
      btnElement: null,
      selectedItem: 0,
      menuItems: []
    }
  },
  computed: {
    targetId() {
      return this.id + 'Target'
    },
    activeBtnClasses() {
      let classes = this.btnClass

      if (this.isExpanded) {
        classes += ` show`
      }
      return classes
    },
    btnWidth() {
      return this.btnElement?.clientWidth + 'px'
    },
    optionsContainerRef() {
      return 'dropdownBody' + this.targetId
    }
  },
  mounted() {
    this.btnElement = document.getElementById(this.id)
    this.menuItems = document.querySelectorAll('.menu-item')
  },
  methods: {
    async showOptions() {
      this.isExpanded = true

      await this.$nextTick()
      this.menuItems[0].focus()
    },
    hideOptions() {
      this.isExpanded = false
    },
    async reset() {
      this.hideOptions()
      await this.$nextTick()
      this.$refs[this.id].focus()
    },

    toggleDropdown(event) {
      if (event?.relatedTarget) return
      this.isExpanded ? this.hideOptions() : this.showOptions()
    },
    handleBlur(e) {
      if (this.$el.contains(e.relatedTarget)) return
      this.hideOptions()
    },

    selectPrevOption() {
      if (this.selectedItem > 0) {
        this.selectedItem--
        this.$refs['menu-item'][this.selectedItem].focus()
      }
    },
    selectNextOption() {
      this.selectedItem++
      this.$refs['menu-item'][this.selectedItem].focus()
    }
  }
}
</script>
