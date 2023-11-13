<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<script>
import JustValidate from "just-validate";
import { setServiceFeedback } from "../../services/api";
import CfInnerLoading from "./CfInnerLoading.vue";
import { DEFAULT_META_TITLE } from "../../services/config";

export default {
  components: { CfInnerLoading },
  name: "ValutationSection",
  props: {
    faq: Boolean,
    page: Boolean,
    asset: String,
  },
  data() {
    return {
      selectedRating: 0,
      selectedAnswer: null,
      step: 1,
      isSendingFeedback: false,
      isSended: false,
      description: "",
      formValidation: null,
      captcha: null,
    };
  },
  async created() {},
  watch: {
    selectedRating() {
      if (!this.formValidation) {
        this.$nextTick(() => {
          this.formValidation = new JustValidate("#rating");
        });
      }
    },
  },
  computed: {
    isNegativeRating() {
      let ratings = [1, 2, 3];
      let selectedRating = this.selectedRating
        ? parseInt(this.selectedRating)
        : 0;
      return ratings.includes(selectedRating);
    },
    isPositiveRating() {
      let ratings = [4, 5];
      let selectedRating = this.selectedRating
        ? parseInt(this.selectedRating)
        : 0;
      return ratings.includes(selectedRating);
    },
    servicesList() {
      let services = this.$store.getters["getComuniConfigServices"];
      return services.filter((s) => !!s.dettaglio);
    },
    activeService() {
      if (this.asset) {
        let service = this.servicesList.find((a) => {
          const serviceAsset = a.dettaglio?.asset;
          return this.asset === serviceAsset?.codice;
        });
        return service;
      } else {
        return this.$store.getters["getActiveService"];
      }
    },
    pageTitle() {
      let title = document.title;

      return title.replace(`${DEFAULT_META_TITLE} | `, "");
    },
  },
  methods: {
    onBack() {
      if (this.step === 1) {
        this.selectedRating = 0;
        this.selectedAnswer = null;
      } else {
        this.step = 1;
        this.selectedAnswer = null;
      }
    },
    async sendNewFeedback() {

      this.isSendingFeedback = true;
      let payload = {
        rating: this.selectedRating,
        option: this.selectedAnswer,
        details: this.description,
        bind: this.captcha,
      };
      if (this.page) {
        let path = location.href;
        const url = path.split("?")[0];
        payload.pagePath = url;
        payload.pageTitle = document.title.replace(
          `${DEFAULT_META_TITLE} | `,
          ""
        );
      } else {
        payload.serviceID = this.activeService?.dettaglio?.idServizio;
        payload.pageTitle = this.activeService?.nome
      }

     
      try {
        let { data } = await setServiceFeedback(payload);
        this.isSended = true;
      } catch (e) {
        await this.$store.dispatch("setNotificationInfo", {
          body: "Non è stato possibile inviare la valutazione.",
        });
      }
      this.isSendingFeedback = false;
    },
    onSubmit() {
      let validation = this.formValidation;
      if (this.isPositiveRating) {
        validation.addRequiredGroup(
          "#feedback-positive-answers",
          "Selezionare una risposta"
        );
      } else if (this.isNegativeRating) {
        validation.addRequiredGroup(
          "#feedback-negative-answers",
          "Selezionare una risposta"
        );
      }

      //SE LA VALIDAZIONE HA SUCCESSO
      validation.revalidate().then((isValid) => {
        if (isValid) {
          if (this.step === 1) {
            this.step = 2;
          } else {
            this.sendNewFeedback();
          }
        }
      });
    },
  },
  async mounted() {},
};
</script>

<template>
  <div class="bg-primary">
    <div class="container">
      <div class="row d-flex justify-content-center bg-primary">
        <div class="col-12 col-lg-6 p-lg-0 px-4">
          <div id="" class="cf-rating">
            <div class="card shadow card-wrapper" data-element="feedback">
              <template v-if="!isSended">
                <div class="cf-rating__card-first">
                  <div class="card-header border-0">
                    <h2 class="mb-0" data-element="feedback-title">
                      Quanto è stato facile usare questo servizio?
                    </h2>
                  </div>
                  <!--  BOTTONI RATING -->
                  <!-- ------------------------------------------------------------------------------------ -->
                  <div class="card-body">
                    <fieldset class="rating">
                      <legend class="visually-hidden">
                        Valuta da 1 a 5 stelle la pagina
                      </legend>
                      <input
                        id="star5a"
                        v-model="selectedRating"
                        name="ratingA"
                        type="radio"
                        value="5"
                      />
                      <label
                        class="full rating-star active"
                        data-element="feedback-rate-5"
                        for="star5a"
                      >
                        <svg
                          id="it-star-full"
                          aria-labelledby="first-star"
                          class="icon icon-sm"
                          role="img"
                          viewBox="0 0 24 24"
                          xmlns="http://www.w3.org/2000/svg"
                        >
                          <path
                            d="M12 1.7L9.5 9.2H1.6L8 13.9l-2.4 7.6 6.4-4.7 6.4 4.7-2.4-7.6 6.4-4.7h-7.9L12 1.7z"
                          />
                          <path d="M0 0h24v24H0z" fill="none" />
                        </svg>
                        <span id="first-star" class="visually-hidden"
                          >Valuta 5 stelle su 5</span
                        >
                      </label>
                      <input
                        id="star4a"
                        v-model="selectedRating"
                        name="ratingA"
                        type="radio"
                        value="4"
                      />
                      <label
                        class="full rating-star active"
                        data-element="feedback-rate-4"
                        for="star4a"
                      >
                        <svg
                          id="it-star-full"
                          aria-labelledby="second-star"
                          class="icon icon-sm"
                          role="img"
                          viewBox="0 0 24 24"
                          xmlns="http://www.w3.org/2000/svg"
                        >
                          <path
                            d="M12 1.7L9.5 9.2H1.6L8 13.9l-2.4 7.6 6.4-4.7 6.4 4.7-2.4-7.6 6.4-4.7h-7.9L12 1.7z"
                          />
                          <path d="M0 0h24v24H0z" fill="none" />
                        </svg>
                        <span id="second-star" class="visually-hidden"
                          >Valuta 4 stelle su 5</span
                        >
                      </label>
                      <input
                        id="star3a"
                        v-model="selectedRating"
                        name="ratingA"
                        type="radio"
                        value="3"
                      />
                      <label
                        class="full rating-star active"
                        data-element="feedback-rate-3"
                        for="star3a"
                      >
                        <svg
                          id="it-star-full"
                          aria-labelledby="third-star"
                          class="icon icon-sm"
                          role="img"
                          viewBox="0 0 24 24"
                          xmlns="http://www.w3.org/2000/svg"
                        >
                          <path
                            d="M12 1.7L9.5 9.2H1.6L8 13.9l-2.4 7.6 6.4-4.7 6.4 4.7-2.4-7.6 6.4-4.7h-7.9L12 1.7z"
                          />
                          <path d="M0 0h24v24H0z" fill="none" />
                        </svg>
                        <span id="third-star" class="visually-hidden"
                          >Valuta 3 stelle su 5</span
                        >
                      </label>
                      <input
                        id="star2a"
                        v-model="selectedRating"
                        name="ratingA"
                        type="radio"
                        value="2"
                      />
                      <label
                        class="full rating-star active"
                        data-element="feedback-rate-2"
                        for="star2a"
                      >
                        <svg
                          id="it-star-full"
                          aria-labelledby="fourth-star"
                          class="icon icon-sm"
                          role="img"
                          viewBox="0 0 24 24"
                          xmlns="http://www.w3.org/2000/svg"
                        >
                          <path
                            d="M12 1.7L9.5 9.2H1.6L8 13.9l-2.4 7.6 6.4-4.7 6.4 4.7-2.4-7.6 6.4-4.7h-7.9L12 1.7z"
                          />
                          <path d="M0 0h24v24H0z" fill="none" />
                        </svg>
                        <span id="fourth-star" class="visually-hidden"
                          >Valuta 2 stelle su 5</span
                        >
                      </label>
                      <input
                        id="star1a"
                        v-model="selectedRating"
                        name="ratingA"
                        type="radio"
                        value="1"
                      />
                      <label
                        class="full rating-star active"
                        data-element="feedback-rate-1"
                        for="star1a"
                      >
                        <svg
                          id="it-star-full"
                          aria-labelledby="fifth-star"
                          class="icon icon-sm"
                          role="img"
                          viewBox="0 0 24 24"
                          xmlns="http://www.w3.org/2000/svg"
                        >
                          <path
                            d="M12 1.7L9.5 9.2H1.6L8 13.9l-2.4 7.6 6.4-4.7 6.4 4.7-2.4-7.6 6.4-4.7h-7.9L12 1.7z"
                          />
                          <path d="M0 0h24v24H0z" fill="none" />
                        </svg>
                        <span id="fifth-star" class="visually-hidden"
                          >Valuta 1 stelle su 5</span
                        >
                      </label>
                    </fieldset>
                  </div>
                </div>
                <!--  DOMANDE -->
                <!-- ------------------------------------------------------------------------------------ -->
                <template v-if="selectedRating">
                  <form
                    v-if="!isSended"
                    id="rating"
                    class="form-rating mt-4"
                    @submit.prevent="onSubmit"
                  >
                    <div v-show="step === 1" data-step="1">
                      <div class="cf-steps-rating">
                        <!--  RISPOSTE POSITIVE -->
                        <!-- ------------------------------------------------------------------------------------ -->
                        <fieldset
                          v-show="isPositiveRating"
                          class="fieldset-rating-one"
                          data-element="feedback-rating-positive"
                        >
                          <div class="valutation-answer w-100">
                            <h3
                              class="step-title d-flex align-items-center justify-content-between drop-shadow"
                            >
                              <legend
                                class="d-block d-lg-inline"
                                data-element="feedback-rating-question"
                              >
                                Quali sono stati gli aspetti che hai preferito?
                              </legend>
                              <span class="step">1/2</span>
                            </h3>
                          </div>
                          <div class="cf-steps-rating__body">
                            <div class="cf-radio-list">
                              <div class="card card-teaser shadow-rating">
                                <div class="card-body">
                                  <div
                                    id="feedback-positive-answers"
                                    class="form-check m-0"
                                  >
                                    <div
                                      class="radio-body border-bottom border-light cf-radio-list__item"
                                    >
                                      <input
                                        id="p-radio-1"
                                        v-model="selectedAnswer"
                                        name="rating1"
                                        type="radio"
                                        value="positivo1"
                                      />
                                      <label
                                        class="active"
                                        data-element="feedback-rating-answer"
                                        for="p-radio-1"
                                        >Le indicazioni erano chiare</label
                                      >
                                    </div>
                                    <div
                                      class="radio-body border-bottom border-light cf-radio-list__item"
                                    >
                                      <input
                                        id="p-radio-2"
                                        v-model="selectedAnswer"
                                        name="rating1"
                                        type="radio"
                                        value="positivo2"
                                      />
                                      <label
                                        class="active"
                                        data-element="feedback-rating-answer"
                                        for="p-radio-2"
                                        >Le indicazioni erano complete</label
                                      >
                                    </div>
                                    <div
                                      class="radio-body border-bottom border-light cf-radio-list__item"
                                    >
                                      <input
                                        id="p-radio-3"
                                        v-model="selectedAnswer"
                                        name="rating1"
                                        type="radio"
                                        value="positivo3"
                                      />
                                      <label
                                        class="active"
                                        data-element="feedback-rating-answer"
                                        for="p-radio-3"
                                        >Capivo sempre che stavo procedendo
                                        correttamente</label
                                      >
                                    </div>
                                    <div
                                      class="radio-body border-bottom border-light cf-radio-list__item"
                                    >
                                      <input
                                        id="p-radio-4"
                                        v-model="selectedAnswer"
                                        name="rating1"
                                        type="radio"
                                        value="positivo4"
                                      />
                                      <label
                                        class="active"
                                        data-element="feedback-rating-answer"
                                        for="p-radio-4"
                                        >Non ho avuto problemi tecnici</label
                                      >
                                    </div>
                                    <div
                                      class="radio-body border-bottom border-light cf-radio-list__item"
                                    >
                                      <input
                                        id="p-radio-5"
                                        v-model="selectedAnswer"
                                        name="rating1"
                                        type="radio"
                                        value="altro"
                                      />
                                      <label
                                        class="active"
                                        data-element="feedback-rating-answer"
                                        for="p-radio-5"
                                        >Altro</label
                                      >
                                    </div>
                                  </div>
                                </div>
                              </div>
                            </div>
                          </div>
                        </fieldset>
                        <!--  RISPOSTE NEGATIVE -->
                        <!-- ------------------------------------------------------------------------------------ -->
                        <fieldset
                          v-show="isNegativeRating"
                          class="fieldset-rating-two"
                          data-element="feedback-rating-negative"
                        >
                          <div class="valutation-answer w-100">
                            <h3
                              class="step-title d-flex align-items-center justify-content-between drop-shadow"
                            >
                              <legend
                                class="d-block d-lg-inline"
                                data-element="feedback-rating-question"
                              >
                                Dove hai incontrato le maggiori difficoltà?
                              </legend>
                              <span class="step">1/2</span>
                            </h3>
                          </div>
                          <div class="cf-steps-rating__body">
                            <div class="cf-radio-list">
                              <div class="card card-teaser shadow-rating">
                                <div class="card-body">
                                  <div
                                    id="feedback-negative-answers"
                                    class="form-check m-0"
                                  >
                                    <div
                                      class="radio-body border-bottom border-light cf-radio-list__item"
                                    >
                                      <input
                                        id="n-radio-1"
                                        v-model="selectedAnswer"
                                        name="rating2"
                                        type="radio"
                                        value="negativo1"
                                      />
                                      <label
                                        class="active"
                                        data-element="feedback-rating-answer"
                                        for="n-radio-1"
                                        >A volte le indicazioni non erano
                                        chiare</label
                                      >
                                    </div>
                                    <div
                                      class="radio-body border-bottom border-light cf-radio-list__item"
                                    >
                                      <input
                                        id="n-radio-2"
                                        v-model="selectedAnswer"
                                        name="rating2"
                                        type="radio"
                                        value="negativo2"
                                      />
                                      <label
                                        class="active"
                                        data-element="feedback-rating-answer"
                                        for="n-radio-2"
                                        >A volte le indicazioni non erano
                                        complete</label
                                      >
                                    </div>
                                    <div
                                      class="radio-body border-bottom border-light cf-radio-list__item"
                                    >
                                      <input
                                        id="n-radio-3"
                                        v-model="selectedAnswer"
                                        name="rating2"
                                        type="radio"
                                        value="negativo3"
                                      />
                                      <label
                                        class="active"
                                        data-element="feedback-rating-answer"
                                        for="n-radio-3"
                                        >A volte non capivo se stavo procedendo
                                        correttamente</label
                                      >
                                    </div>
                                    <div
                                      class="radio-body border-bottom border-light cf-radio-list__item"
                                    >
                                      <input
                                        id="n-radio-4"
                                        v-model="selectedAnswer"
                                        name="rating2"
                                        type="radio"
                                        value="negativo4"
                                      />
                                      <label
                                        class="active"
                                        data-element="feedback-rating-answer"
                                        for="n-radio-4"
                                        >Ho avuto problemi tecnici</label
                                      >
                                    </div>
                                    <div
                                      class="radio-body border-bottom border-light cf-radio-list__item"
                                    >
                                      <input
                                        id="n-radio-5"
                                        v-model="selectedAnswer"
                                        name="rating2"
                                        type="radio"
                                        value="altro"
                                      />
                                      <label
                                        class="active"
                                        data-element="feedback-rating-answer"
                                        for="n-radio-5"
                                        >Altro</label
                                      >
                                    </div>
                                  </div>
                                </div>
                              </div>
                            </div>
                          </div>
                        </fieldset>
                      </div>
                    </div>
                    <!--  ALTRE INFORMAZIONI -->
                    <!-- ------------------------------------------------------------------------------------ -->
                    <div v-show="step === 2" data-step="2">
                      <div class="cf-steps-rating">
                        <fieldset>
                          <div class="valutation-answer w-100">
                            <h3
                              class="step-title d-flex align-items-center justify-content-between drop-shadow mb-3"
                            >
                              <legend class="d-block d-lg-inline">
                                Vuoi aggiungere altri dettagli?
                              </legend>
                              <span class="step">2/2</span>
                            </h3>
                          </div>
                          <div class="cf-steps-rating__body mt-5">
                            <div class="form-group">
                              <label
                                class=""
                                for="formGroupExampleInputWithHelp"
                                >Dettaglio</label
                              >
                              <input
                                id="formGroupExampleInputWithHelp"
                                v-model="description"
                                aria-describedby="formGroupExampleInputWithHelpDescription"
                                class="form-control"
                                data-element="feedback-input-text"
                                maxlength="200"
                                type="text"
                              />
                              <small
                                id="formGroupExampleInputWithHelpDescription"
                                class="form-text"
                              >
                                Inserire massimo 200 caratteri</small
                              >
                            </div>
                          </div>

                          <div class="visually-hidden" aria-hidden="true">
                            <div class="form-group">
                              <label class="" for="bindInput">Captcha</label>
                              <input
                                id="bindInput"
                                v-model="captcha"
                                class="form-control"
                                type="text"
                              />
                            </div>
                          </div>
                        </fieldset>
                      </div>
                    </div>
                    <div
                      class="d-flex flex-nowrap pt-4 w-100 justify-content-center"
                    >
                      <button
                        class="btn btn-outline-primary fw-bold me-4 btn-back"
                        type="button"
                        @click="onBack"
                      >
                        Indietro
                      </button>
                      <button
                        class="btn btn-primary fw-bold btn-next"
                        form="rating"
                        type="submit"
                      >
                        Avanti

                        <span v-if="isSendingFeedback" class="ms-2">
                          <cf-inner-loading showing small />
                        </span>
                      </button>
                    </div>
                  </form>
                </template>
              </template>

              <!--  FEEDBACK  -->
              <!-- ------------------------------------------------------------------------------------ -->
              <div v-else class="cf-rating__card-second" data-step="3">
                <div class="card-header border-0 mb-0">
                  <h2 id="rating-feedback" class="title-medium-2-bold mb-0">
                    Grazie, il tuo parere ci aiuterà a migliorare il servizio!
                  </h2>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>


