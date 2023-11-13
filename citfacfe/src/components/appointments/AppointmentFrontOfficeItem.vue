<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>

  <div :class="[isSelected ? 'border-primary': 'border-100']"
       class="card p-3 appointment-front-office-item border border-2">
    <div class="card-header border-0 p-0 m-0">


      <div class="form-check m-0 ">
        <input :id="'radio'+id" v-model="selectedItem" :value="id" class="form-check-input" name="selectedFrontOffice"
               type="radio" @input="onSelected">
        <label :for="'radio'+ id" class="m-0 radio-input-label">
          <h3 class="h5 fw-bold mb-0 col ">{{ name }}</h3>
        </label>
      </div>
    </div>
    <div class="card-body p-0 my-2 it-list-wrapper">
      <ul class="it-list ">
        <li class="list-item py-3">
                            <span class="text">
                              <em>Indirizzo</em>
                             {{ address }}
                            </span>
        </li>
        <li class="list-item py-3">
                            <span class="text">
                              <em>Informazioni</em>
                             {{ information ?? '-' }}
                            </span>
        </li>
      </ul>
    </div>


  </div>

</template>

<script>
export default {
  name: "AppointmentFrontOfficeItem",
  emits: ["onSelect"],
  props: {
    location: Object,

    modelValue: [String, Number]
  },
  data() {
    return {
      selectedItem: false
    }
  },
  mounted() {
    if (this.modelValue) {
      this.selectedItem = this.modelValue
    }
  },
  computed: {
    id() {
      return this.location?.id
    },
    isSelected() {
      return this.modelValue === this.id
    },
    name() {
      return this.location?.name
    },
    address() {
      return this.location?.location
    },
    description() {
      return this.location?.description
    },
    information() {
      let info = this.location?.additionalInformations
      return info && info !== "" ? info : "-"
    }
  },
  methods: {
    onSelected(event) {
      this.selectedItem = this.id
      this.$emit('onSelect', this.id)
    }
  }
}
</script>


