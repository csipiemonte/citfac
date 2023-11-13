<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <button :class="classes" class=" btn appointment-free-slot-calendar__slot__time" type="button"
          @click="onSlotSelect">
    {{ slot }}
  </button>
</template>

<script>


export default {
  name: "AppointFreeSlotTimeButton",
  props: {
    slot: String,
    active: Boolean
  },
  computed: {
    classes(){
      let result = [];
      if (this.active){
        result.push(
            "appointment-free-slot-calendar__slot--selected btn-primary"
        );
      }else{
        result.push(
            "btn-outline-primary"
        );
      }


      return result;
    }
  },
  methods:{
    onSlotSelect(){
      this.$emit('on-select', this.slot)
    }
  }
}
</script>

