<!--
 SPDX-License-Identifier: EUPL-1.2
 Copyright CSI Piemonte - 2023
-->

<template>
  <div class="dashboard-rating">
    <div
      class="stars"
      :style="[{ '--rating': rating, '--nStars': N_STARS, 'font-size': size }]"
      :aria-label="`Valutazione media ${rating} su ${N_STARS}`"
      aria-hidden="true"
    ></div>
   
  </div>
</template>

<script>
const N_STARS = 5
export default {
  components: {},

  name: 'DashboardRating',
  props: {
    rating: Number,
    size:{type:String, default: "40px"}
  },
  data() {
    return {
      N_STARS
    }
  },
  created() {},
  computed: {
    starRatingIcons() {
      let stars = []

      for (let i = 0; i <= Math.floor(this.rating); i++) {
        stars.push('bi-star-fill')
      }

      if (this.rating % 1 !== 0) {
        stars.push('bi-star-half')
      }

      for (let i = 0; i < Math.floor(N_STARS - this.rating); i++) {
        stars.push('bi-star')
      }

      return stars
    },
    ratingPercent() {
      return (this.rating / N_STARS) * 100
    }
  },
  methods: {
    startIcon(i) {
      return this.starRatingIcons[i]
    }
  }
}
</script>

<style lang="scss">
.dashboard-rating {
  font-size: 0.88rem;
 
  .stars {
    --percent: calc(var(--rating) / var(--nStars) * 100%);
    width: 100%;
    position: relative;
    
    font-family: Times; // make sure ★ appears correctly
    line-height: 1;

    &::before {
      content: '★★★★★';
     
      background-image: linear-gradient(
        90deg,
        var(--bs-primary) var(--percent),
        #c3cfdb var(--percent)
      );
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
    }
  }
}
</style>
