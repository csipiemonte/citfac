<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="preference-icon">
    <button @click="onClick"
            :aria-label="ariaLabel"
            :disabled="disabled"
            class="btn btn-flat text-primary px-0"
            :class="{'opacity-50' : disabled}"
            data-bs-toggle="button" autocomplete="off" aria-pressed="true"
    >
    
      <svg
                                aria-hidden="true"
                                class="icon icon-xs me-1"
                                :class="[disabled ? 'icon-secondary' : 'icon-primary' ]"
                              >
                                <use :href="icon"></use>
                              </svg>
    </button>
  </div>

</template>

<script>
import {PREFERENCES_TYPES_MAP} from "../../../services/config";

const ICONS_MAP ={
  [PREFERENCES_TYPES_MAP.PUSH] : 'bell',
  [PREFERENCES_TYPES_MAP.EMAIL] : 'envelope',
  [PREFERENCES_TYPES_MAP.SMS] : 'chat-left-text',
}


export default {


  name: "PreferenceIcon",
  props:{
    value: Boolean,
    disabled: Boolean,
    type: String
  },
  data(){
    return{
      PREFERENCES_TYPES_MAP,
    }
  },
  computed:{
    icon(){
      let icon = ICONS_MAP[this.type]
      if(!!this.value){
        icon = `${icon}-fill`
      }

      return  this.$svgs.getBootstrapIconsUrl(icon);
    },
    ariaLabel(){
      let text = this.type
      if(!!this.value){
        text = `${text} attivo`
      }else{
        text = `${text} disattivo`
      }

      return text
    }
  },
  methods:{
    onClick(){
      this.$emit('on-change', !this.value)
    }
  }
}
</script>

