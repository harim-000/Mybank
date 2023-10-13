<script>
  export default {
  data: () => ({
  links: [
  'Home',
  'About Us',
  'Team',
  'Services',
  'Blog',
  'Contact Us',
  ],
}),
}
</script>

<template>
  <v-card class="custom-card" >
    <v-col variant="plain" class="text-center mt-4" color="deep-purple-accent-4">
      <v-btn variant="plain" to="/" color="deep-purple-accent-4">
        Home
      </v-btn>
        myb64233@gmail.com
      </v-col>
      <v-col class="text-center mt-4" cols="12">
          <p style="color: #7E57C2">&copy; Harim Seok</p>
      </v-col>
  </v-card>
</template>

<style scoped>
.custom-card {
  min-height: 10vh; /* Set a minimum height for the card */
}
</style>