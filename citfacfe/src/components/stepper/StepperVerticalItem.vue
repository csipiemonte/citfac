<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div ref="vertical-item" class="d-flex mb-1 ">
    <div class="d-flex flex-column  align-items-stretch ">
      <div :class="classes" class=" stepper-vertical__dot d-flex flex-row  align-items-center justify-content-center">
        <div class="text-center col-12">
          <template v-if="confirmed">
            <svg class="icon steppers-success icon-md icon-white">
              <use 
              :xlink:href="$svgs.getSpriteSvgUrl('it-check')"
              ></use>
            </svg>
            <span class="visually-hidden">Confermato</span>
          </template>
          <template v-if="active">
            <svg class="icon steppers-success icon-sm icon-white">
              <use
              :xlink:href="$svgs.getSpriteSvgUrl('it-pencil')"
            ></use>
            </svg>
            <span class="visually-hidden">Attivo</span>
          </template>
        </div>


      </div>
      <div v-if="!last" class="vr h-100 align-self-center"></div>
    </div>
    <div class="stepper-vertical__body ">
      <p :class="titleClasses" class="mb-0"><strong>{{ title }}</strong></p>
      <p v-if="subtitle" class="text-paragraph-small text-muted pb-3">{{ subtitle }}</p>
      <Transition name="slide-fade">
        <div class="py-4" v-if="active">
          <slot/>
        </div>
      </Transition>

    </div>
  </div>
</template>

<script>

// COMPONENTE SINGOLO STEP VERTICALE . DA USARE CON "STEPPER VERTICALE"
// PER UTILIZZARE IL COMPONENTE USARE I PROPS
//"name" NOME O ID DELLO STEP
//"title" TITOLO DELLO STEP
//"subtitle" EVENTUALE SOTTOTITOLO DELLO STEP
//"last" INDICA L'ULTIMO STEP

export default {
  name: "StepperVerticalItem",
  props: {
    name: {type: Number, default: null},
    title: {type: String, default: 'Titolo'},
    subtitle: {type: String, default: ''},
    last:Boolean
  },
  data() {
    return {

    }
  },

  computed: {
    step(){
      return this.$parent.$props.step
    } ,
    classes() {
      let result = []

      if (this.confirmed) {
        result.push("bg-success")
      } else if (this.active) {
        result.push("bg-primary")
      } else {
        result.push("neutral-2-bg-b3")
      }

      return result
    },
    titleClasses() {
      let result = []
      if (this.active) {
        result.push("text-primary fw-bold")
      } else if(this.confirmed){
        result.push("text-success fw-bold")
      }else {
        result.push("text-dark")
      }

      return result
    },
    active(){
      return  this.step === this.name
    },
    confirmed(){
      return  this.step > this.name
    }


  }
}
</script>

<style lang="scss" scoped>


.slide-fade-enter-active {
  transition: all 0.4s ease-out;
}

.slide-fade-leave-active {
  transition: all 0.4s cubic-bezier(1, 0.5, 0.8, 1);
}

.slide-fade-enter-from,
.slide-fade-leave-to {
  transform: translateY(20px);
  opacity: 0;
}

</style>
