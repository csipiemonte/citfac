<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="csi-toggle input-group" >
    
    <label class="toggle-label " :for="id" :class="{'active': modelValue}">
      {{ label }}
     
      <input
      type="checkbox"
      name="toggle"
      role="switch"
      :id="id"
      class="toggle-input"
      @input="onInput"
    />
      
        <span class="toggle-lever" ></span>
        <span class="toggle-focus"></span>
      
    </label>
    
    
  </div>
</template>

<script>
export default {
  name: 'CsiToggle',
  emits: ['update:modelValue', 'input'],
  props: {
    label: String,
    modelValue: Boolean,
    disabled: Boolean,
    id: String
  },
  data() {
    return {
    
    }
  },
  computed: {},
  methods: {
    onInput(event) {
     
      let checked = event?.target?.checked
      this.$emit('update:modelValue', checked)
      this.$emit('input', checked)
    },
   
   
  }
}
</script>

