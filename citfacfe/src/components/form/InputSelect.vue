<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="form-group">
    <label
      :for="currentId"
      style="pointer-events: none"
      :class="{ active: labelActive }"
      >{{ labelText }}</label
    >
    <div class="select-wrapper">
      <select
        :disabled="disabled"
        @focusout="activateLabel(false)"
        @focusin="activateLabel(false)"
        class="form-control fw-bold"
        @input="onSelect"
        :id="currentId"
      >
        <template v-for="(option, index) in currentOptionList" :key="index">
          <option
            :selected="option.isStarting"
            :id="option.id"
            v-show="option.showable"
            :value="option.id"
          >
            {{ option.name }}
          </option>
        </template>
      </select>
      <small class="form-text text-200" :id="currentId">{{ moreInfo }}</small>
    </div>
  </div>
</template>

<script>
export default {
  // COMPONENTE PER SELECT SINGOLA
  // PER UTILIZZARE IL COMPONENTE USARE I PROPS
  //labelText" TESTO DELLA LABEL DEL CAMPO INSERIMENTO,
  //id" UN ID UNIVOCO PER IL COMPONENTE,
  //disabled" PER INDICARE SE DISABILITATA
  //moreInfo" EVENTUALE INFO AGGIUNTIVA DEL CAMPO,
  //optionList" ARRAY DI OPZIONI {name: nomeOpzione, id: valoreOpzione}
  //@selectedOption" EMETTE IL VALORE AGGIORNATO DEL CAMPO
  name: "InputSelect",
  props: {
    disabled: Boolean,
    optionList: Array,
    labelText: String,
    id: String,
    moreInfo: String,
    prefill: Number,
  },

  data() {
    return {
      labelActive: false,
      selectedOption: null,
      currentId: "inputSelect" + this.id,
      selectedOptionDesc: null,
    };
  },
  watch: {
    optionList() {
      this.currentOptionList.unshift({
        showable: false,
      });

      this.activateLabel(true);
    },
    prefill() {
      if (this.prefill) {
        this.$emit("selectedOption", this.prefill);
        this.activateLabel();
      }
    },
  },
  created() {

  },
  computed: {
    
    currentOptionList() {
      let array = [...this.optionList];
      array.forEach((a) => {
        a.showable = true;
        if (a.id === this.prefill) {
          a.isStarting = true;
        }
      });
      return array;
    },
  },
  mounted() {
    //SELEZIONE L'ELEMENTO GRAFICO
    this.formSelezionato = document.getElementById(this.currentId);
    this.formSelezionato.value = null;
    if (this.prefill) {
      this.$emit("selectedOption", this.prefill);
      this.activateLabel();
    }
  },

  methods: {
    //POSIZIONA LA LABEL PER PERMETTERE LA SCRITTURA
    activateLabel(isChangeOption) {
      if (this.prefill) {
        this.labelActive = true;
        return;
      }
      if (this.formSelezionato?.value) {
        this.labelActive = true;
      } else {
        this.labelActive = !this.labelActive;
      }
      if (isChangeOption) {
        this.labelActive = false;
      }
    },
    onSelect() {
      //AGGIORNO VALORE OPZIONE SELEZIONATA

      this.selectedOption = this.formSelezionato?.value;
      this.selectedOptionDesc = this.currentOptionList.find(
        (a) => a.id === parseInt(this.selectedOption)
      ).name;
      this.$emit(
        "selectedOption",
        this.selectedOption,
        this.selectedOptionDesc
      );
    },
  },
};
</script>
