<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <section :class="[first ? 'justify-content-end' : 'justify-content-between']" class="row stepper-navigation mt-3">
    <div class="col-12 ">
      <nav class="steppers-nav">
        <div>
          <button v-show="!first" class="btn btn-outline-primary btn-sm  btn-icon" type="button" @click="$emit('prev')">
            <svg class="icon icon-primary">
              <use 
              :xlink:href="$svgs.getSpriteSvgUrl('it-chevron-left')"
             ></use>
            </svg>
            Indietro
          </button>
        </div>
        <div>

          <button v-if="submit" :disabled="disabledNext" :form="formId" class="btn btn-primary btn-sm btn-icon "
                  type="submit">{{ nextLabelDescription }}
            <svg class="icon icon-primary">
              <use 
              :xlink:href="$svgs.getSpriteSvgUrl('it-chevron-right')"
              ></use>
            </svg>

            <span v-if="loading" class="ms-2">
                         <cf-inner-loading showing small/>
                      </span>
          </button>

          <button v-else :disabled="disabledNext" class="btn btn-primary btn-sm btn-icon " type="button"
                  @click="$emit('next')">{{ nextLabelDescription }}
            <svg class="icon icon-primary">
              <use 
              :xlink:href="$svgs.getSpriteSvgUrl('it-chevron-right')"
             ></use>
            </svg>
            <span v-if="loading" class="ms-2">
                         <cf-inner-loading showing small/>
                      </span></button>
        </div>


      </nav>
    </div>
  </section>


</template>

<script>
// COMPONENTE  CHE CONSENTE DI NAVIGARE SULLO STEPPER
// PER UTILIZZARE IL COMPONENTE USARE I PROPS
//"first" NASCONDE O MOSTRA IL BOTTONE "INDIETRO"
//"last" NASCONDE O MOSTRA IL BOTTONE "AVANTI"
import CfInnerLoading from "../generic/CfInnerLoading.vue";

export default {
  name: "StepperNavigation",
  components: {CfInnerLoading},
  props: {
    formId: String,
    first: Boolean,
    last: Boolean,
    disabledNext: Boolean,
    submit: Boolean,
    loading: Boolean,
    nextLabel:String
  },
  computed:{
    nextLabelDescription(){
      if(this.nextLabel){
        return this.nextLabel
      }else if(this.last){
        return "Fine"
      }else{
        return "Avanti"
      }
    }
  }

}
</script>

