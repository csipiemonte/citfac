<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="dropdown cf-dropdown position-relative">
    <component
      :is="type"
      :id="id"
      ref="dropdownButton"
      :aria-expanded="isExpanded"
      :class="[{ show: isExpanded }, buttonClasses]"
      :title="title"
      aria-haspopup="true"
      class="btn"
      data-toggle="dropdown"
      role="menu"
      tabindex="0"
      @click="toggleDropdown"
      @keyup.enter="toggleDropdown"
      @focusout="closeDropdown"
    >
      <slot name="button"></slot>
      <svg class="icon icon-xs right dropdownIcon" v-if="!hideIcon">
        <use 
        :xlink:href="$svgs.getSpriteSvgUrl('it-expand')"
       ></use>
      </svg>
    </component>
    <Transition name="easy-out">
      <div
        v-show="isExpanded"
        :id="targetId"
        ref="dropdownBody"
        :aria-labelledby="id"
        :class="{ show: isExpanded }"
        class="dropdown-content"
      >
        <div class="link-list-wrapper">
          <ul class="link-list">
            <slot name="list" />
          </ul>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script>
const TYPES = {
  LINK: "a",
  BUTTON: "button",
};
export default {
  name: "CfDropdown",
  props: {
    type: String, // "a" or "button"
    id: String,
    title: String,
    buttonClasses: String,
    hideIcon: Boolean,
  },
  data() {
    return {
      isExpanded: false,
      positionTop: 0,
      positionLeft: 0,
    };
  },
  computed: {
    targetId() {
      return this.id + "Target";
    },

    btnClasses() {
      let classes = [this.buttonClasses];

      return classes;
    },
    positionStyle() {
      return {
        top: `${this.positionTop}px !important`,
        left: `${this.positionLeft}px`,
      };
    },
  },

  mounted() {
    this.$nextTick(() => {});
  },
  methods: {
    getMenuPosition() {
      let dropdownButton = document.getElementById(this.id);
      let distanceTop = 0;
      let distanceLeft = 0;

      if (dropdownButton) {
        // Cerchiamo la posizione sotto:
        let computedStyle = window.getComputedStyle(dropdownButton);
        let height = dropdownButton.clientHeight;
        let paddingBottom = parseFloat(computedStyle.paddingBottom);
        distanceTop = height - paddingBottom + 14;

        // Cerchiamo la posizione in centro:
        let width = dropdownButton.clientWidth;
        let paddingLeft = parseFloat(computedStyle.paddingLeft);
        let paddingRight = parseFloat(computedStyle.paddingRight);
        distanceLeft = (width - paddingLeft - paddingRight) / 2;
      }

      this.positionTop = distanceTop;
      this.positionLeft = distanceLeft;
    },
    toggleDropdown() {
      this.isExpanded = !this.isExpanded;
      this.getMenuPosition();
    },

    closeDropdown() {
      this.isExpanded = false;
    },
  },
};
</script>


