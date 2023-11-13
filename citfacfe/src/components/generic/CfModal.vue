<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<!--MODALE CUSTOM -->
<template>
  <div
    :id="id"
    :class="{ 'bd-example-modal-xl': maximized }"
    aria-labelledby="modalTitle"
    class="cf-modal modal fade"
    role="dialog"
    tabindex="-1"
    ref="cfModal"
  >
    <div
      style="margin: auto"
      :class="{ 'modal-xl': maximized, 'modal-lg': medium, 'modal-md': small }"
      class="modal-dialog modal-dialog-centered modal-fullscreen-sm-down"
      role="document"
    >
      <div class="modal-content">
        <!-- Modal Header -->
        <div class="modal-header">
          <h5 id="modalTitle" class="modal-title">{{ title }}</h5>
          <button
            :id="`${id}-modal-close__btn`"
            aria-label="Chiudi finestra modale"
            class="btn-close"
            data-bs-dismiss="modal"
            type="button"
          >
            <svg class="icon">
              <use
              :xlink:href="$svgs.getSpriteSvgUrl('it-close')"
             ></use>
            </svg>
          </button>
        </div>
        <div class="modal-body">
          <slot />
        </div>
        <template v-if="!noAction">
          <div class="modal-footer">
            <div class="w-100 pt-4">
              <div
                class="d-grid gap-2 d-md-block d-md-flex justify-content-md-between modal-footer-actions flex-md-row-reverse"
              >
                <slot name="actions"></slot>
              </div>
            </div>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<script>
// COMPONENTE FINESTRA MODALE CUSTOM
// PER UTILIZZARE IL COMPONENTE USARE I PROPS
//"title" TITOLO DELLA MODALE
//id" UN ID UNIVOCO PER IL COMPONENTE,
//maximized" FINESTRA A SCHERMO INTERO
//@onHide = PERMETTE DI CHIUDERE LA FINESTRA DALL'ESTERNO

let thisModalObj = null;

export default {
  name: "CfModal",
  emits: ["onHide"],
  props: {
    title: String,
    id: String,
    maximized: Boolean,
    medium: Boolean,
    small: Boolean,
    noAction: Boolean,
  },
  data() {
    return {
      myModalEl: null,
    };
  },

  mounted() {
    this.myModalEl = document.querySelector("#" + this.id);
    thisModalObj = bootstrap.Modal.getOrCreateInstance(this.myModalEl);


    this.triggerHidden()
  },

  methods: {
    triggerHidden() {
      let self = this;

      this.myModalEl.addEventListener("hide.bs.modal", function (event) {
        self.$emit("onHide");
      });
    },
    onHide() {
      
      let id = `#${this.id}-modal-close__btn`;
      let closeBtn = document.querySelector(id);
      closeBtn.click();
    },
    _show() {
      thisModalObj.show();
    },
  },
};
</script>


