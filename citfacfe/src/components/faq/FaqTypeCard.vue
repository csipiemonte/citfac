<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<script>

export default {
  name: "FaqTypeCard",
  props: {
    type: Object,
  },
  data() {
    return {

    };
  },
  async created() {

  },
  computed: {},
  methods: {
      typeSelected(){
          this.$emit("typeSelected", this.type)

      }
  },
  async mounted() {},
};
</script>

<template>

<button   type="button" class="btn btn-white  p-5 w-100  h-100  " @click="typeSelected" >
    <h4 >{{type.name}}</h4>

</button>
</template>

