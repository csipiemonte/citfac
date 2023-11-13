<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="form-group">
    <div class="input-group">
    <div class="input-group-prepend">
      <div class="input-group-text"><svg class="icon icon-sm"><use 
        :xlink:href="$svgs.getSpriteSvgUrl('it-search')"
                        ></use></svg></div>
    </div>
    <label
      :for="currentId"
      style="pointer-events: none"
      :class="{ active: labelActive }"
      >{{ labelText }}</label
    >
    <input
      :class="{ 'text-uppercase': isUppercase }"
      :name="currentId"
      @focusout="activateLabel"
      @focusin="activateLabel"
      @input="input"
      type="text"
      class="form-control"
      :id="currentId"

    >



    <small class="form-text text-200" :id="currentId">{{ moreInfo }}</small>
  </div>
    <ul v-if="dataListOptions.length>0" class="list-group">
      <li @click="confirmSearch(option)"  @mouseover="addActive(index)" @mouseout="addActive(index)"  class="list-group-item " v-for="(option, index) in dataListOptions" :id="index" :key="index" >
        <template v-if="checkTypeCategory(option)"> <svg class="icon ms-1">
          <use   :xlink:href="$svgs.getSpriteSvgUrl('it-folder')"
                  
          ></use>
        </svg></template><template v-else>
        <svg class="icon ms-1">
          <use
          :xlink:href="$svgs.getSpriteSvgUrl('it-file')"
       
          ></use>
        </svg>
      </template>
        {{option.title}}
      </li>

    </ul>
    <ul v-else-if="this.currentValue && this.currentValue.length>0" class="list-group mt-2">
      <li >
        <i> Nessun risultato trovato</i>
     </li>
    </ul>
    
  </div>
</template>

<script>
// COMPONENTE PER INPUT TESTUALE LIBERO
// PER UTILIZZARE IL COMPONENTE USARE I PROPS
//labelText" TESTO DELLA LABEL DEL CAMPO INSERIMENTO,
//id" UN ID UNIVOCO PER IL COMPONENTE,
//moreInfo" EVENTUALE INFO AGGIUNTIVA DEL CAMPO,
//isUppercase" SE TRUE IL TESTO è MAIUSCOLO,
//prefill" SE PRESENTE PRCOMPILA IL CAMPO
//@updateText" EMETTE IL VALORE AGGIORNATO DEL CAMPO
export default {
  name: "InputSearch",
  props: {
    labelText: String,
    id: String,
    moreInfo: String,
    isUppercase: Boolean,
    searchFromList: [String, Boolean],
    dataListObject: Object,

  },
  data() {
    return {
      textForm: null,
      currentId: null,
      labelActive: false,
      currentValue: null,
    };
  },
  async created() {
    this.currentId = "inputSearch" + this.id;
  },
  computed: {
    dataListOptions() {
      this.dataListObject?.details.forEach(a=>{
        let cleanText = a.title.replace(/<\/?[^>]+(>|$)/g, "");
        a.title=cleanText
      })

      return this.dataListObject?.details ?? []
    }
  },
  mounted() {
    this.textForm = document.getElementById(this.currentId);

  },
  watch: {

  },
  methods: {
    confirmSearch(option){
      this.textForm.value=option.title
      this.activateLabel()
      this.$emit("confirmSearch", option);
    },
    checkTypeCategory(option){
      if(option.type.includes("Category")){
        return true
      } else{
        return false
      }
    },
    addActive(id){
      let element= document.getElementById(id)
      if(element.classList.contains("active")){
        element.classList.remove("active")
      }else {
        element.classList.add("active")
      }
    },
    //POSIZIONA LA LABEL PER PERMETTERE LA SCRITTURA
    activateLabel() {
      if (this.textForm?.value) {
        this.labelActive = true;
      } else {
        this.labelActive = !this.labelActive;
      }
    },
    input() {
      this.currentValue = this.textForm.value;
      this.$emit("updateSearch", this.currentValue);

    },
  },
};
</script>

