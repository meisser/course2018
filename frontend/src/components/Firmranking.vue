<template>
  <div>
  <div v-if="loading">
    Loading firm ranking...
  </div>
  <div v-if="!loading">
    <p>Traditionally, firms optimize profits. However, in agent-based simulations, it is more sensible to rank them by total dividends paid to consumer-shareholders. In a world, in which firms are allowed to own each others shares, it would be trivial to create infinite profits just by sending dividends back and forth between two companies that own each other. Thus, we need a more elaborate metric.</p>
    <table class="agentlist">
      <tr>
        <td>Rank</td>
        <td>Firm</td>
        <td>Dividends</td>
        <td>Source</td>
        <td>Version</td>
      </tr>
      <tr v-for="(rank,index) in ranking">
        <td>{{index + 1}}</td>
        <td>{{`${rank.type}`}}</td>
        <td>{{`${rank.score}`}}</td>
        <td>
          <a :href="`${rank.url}`">source</a>
        </td>
        <td>{{`${rank.version}`}}</td>
      </tr>
    </table>
  </div>
  </div>
</template>

<script>
import config from '../config';

export default {
  name: 'firmranking',
  props: ['simulationid'],
  data() {
    return {
      loading: true,
      ranking: null,
    };
  },
  created() {
    // get simulation ranking
    fetch(
      `${config.apiURL}/firmranking?sim=${this.$route.query.sim}`,
      config.xhrConfig,
    )
      .then(config.handleFetchErrors)
      .then(response => response.json())
      .then(
      (response) => {
        this.ranking = response.list;
        this.loading = false;
      },
    )
      .catch(error => config.alertError(error));
  },
};
</script>

<style lang="sass">
@import '../assets/sass/vars'
@import '../assets/sass/mixins'

.agentlist
  padding: 0

  tr:nth-child(even)
    background-color: #f2f2f2

  th, td
    padding: 10px
    text-align: left

  li
    text-align: left

</style>

