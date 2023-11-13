<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div  class="preference-channel-checkbox" :id="id+'-container'" tabindex="0">
    <div class="form-check ">
      <input :id="id" :disabled="disabled" :name="channel" v-model="checkboxValue"
             type="checkbox" />
      <label :id="`${id}-label`" :aria-label="label" :class="{'disabled': disabled}" :for="id"></label>
    </div>
    <small aria-hidden="true">{{ label }}</small>
  </div>
</template>

<script>

// COMPONENTE CHE CONSENTE DI ATTIVARE/DISABILITARE PREFERENZE DI TUTTI I SERVIZI
// PER UTILIZZARE IL COMPONENTE USARE I PROPS
//"modelValue" = ABILITA/DISABILITA UN CANALE
//"channel" =CANALE DA GESTIRE
//"disabled" = CHECBOX DISABILITATA
//"id" : ID DEL COMPONENTE
//"label" LABEL DEL CHECKBOX

export default {
  name: "PreferenceChannelCheckbox",
  props: {
    modelValue: Boolean,
    channel: String,
    disabled: Boolean,
    id: String,
    label: String
  },
  computed:{
    checkboxValue:{
      get() {
        return this.modelValue
      },
      set() {
        this.$emit('update:modelValue', !this.modelValue)
      }


    }
  },
  mounted() {
    if (this.disabled) {
        let checkbox = document.getElementById(`${this.id}-container`)
        if (checkbox) {
          checkbox.setAttribute("data-bs-toggle", "tooltip");
          checkbox.setAttribute("title", `Nessun Servizio supporta le notifiche ${this.channel}`);
        }
    }
  },
  methods: {
    onInput(event){
      this.$emit('update:modelValue', !this.modelValue)
    }
  }
}
</script>


