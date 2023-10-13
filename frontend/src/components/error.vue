<script setup>

</script>

<template>
  <br>
  <br>
  <br>
  <br>
  <v-card
      class="mx-auto"
      width="900"
  >
    <v-card-text>
      <div>error page</div>
      <p class="text-h4 text--primary">
        404 error
      </p>
      <p>wrong approach</p>
      <div class="text--primary">
        존재하지 <br>
        않는 페이지입니다.
      </div>
    </v-card-text>
    <v-card-actions>
      <v-btn
          variant="text"
          color="deep-purple-accent-4"
          to="/"
      >
        Home
      </v-btn>
    </v-card-actions>
  </v-card>
</template>

<style scoped>
.mx-auto {
  width: 900px;
}
</style>