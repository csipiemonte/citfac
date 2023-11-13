<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div>
    <div class="csi-tabs">
      <div class="nav-container" :style="{ width: maxContainerWidth }">
        <ul
          :id="id"
          class="nav justify-content-center"
          role="tablist"
          :aria-labelledby="AriaLabelledby"
        >
          <li
            class="nav-item"
            v-for="(tab) in tabs"
            :key="tab.target"
            :class="[{ active: tab.target === selectedTab }, { 'text-uppercase': !noCaps }]"
            :style="{ width: tabWidth }"
          >
            <router-link
              :id="`tab-${tab.target}`"
              role="tab"
              class="nav-link"
              :to="tab.target"
              exact-path-class="active"
              exact
              :alt="`visualizza tab ${tab.nome}`"
              >{{ tab.nome }}</router-link
            >
          </li>
        </ul>
        <div
          aria-hidden="true"
          :id="`focus-border-${id}`"
          class="focus-border"
          :style="{ width: tabWidth }"
        ></div>
      </div>
    </div>

    <div :id="`tab-panel-${id}`" class="tab-content" tabindex="0">
      <slot></slot>
    </div>
  </div>
</template>

<script>
/*
TABS
@tabs : La lista delle  tab. Deve avere questa forma:
{target: '', nome: '', } 
 */
export default {
  name: 'CsiTabs',
  emits: ['update:modelValue'],
  props: {
    tabs: { type: Array, default: () => [], required: true },
    id: String,
    AriaLabelledby: String,
    noCaps: Boolean,
    modelValue: String
  },
  data() {
    return {
      selectedTab: null,
      selectedTabIndex: 0,

      focusLine: {
        left: 0,
        width: 0
      },
      focusLineTemp: null,
      tabPanels: [],
      selectedTabPanel: null,
      selectedTabPanelIndex: null
    }
  },
  computed: {
    maxContainerWidth() {
      return `${200 * this.tabsLength}px`
    },
    tabsLength() {
      return this.tabs.length
    },
    tabWidth() {
      return `calc(100% / ${this.tabsLength})`
    }
  },
  mounted() {
    this.selectedTab = this.modelValue
    this.selectedTabIndex = 0
  },
  watch: {
    $route: {
      immediate: true,
      handler(val, oldVal) {
        this.$nextTick(() => {
          this.selectedTab = this.tabs.find((tab) => tab.target.name === val.name)

          this.selectedTabIndex = this.tabs.indexOf(this.selectedTab)

          const tab = document.getElementById(`focus-border-${this.id}`)
          if (!tab) return
          tab.style.left = `calc(${this.tabWidth} * ${this.selectedTabIndex})`
        })
      }
    }
  },
  methods: {
    onSelectTab(tab, index) {
  
      this.selectedTab = tab.path
      this.selectedTabIndex = index

     
    }
  }
}
</script>
