<template>
  <div id="app">
    <v-app>
      <div class="head">
        <div class="logo-container">
          <router-link to="/">
            <img src="@/assets/logo.png"  alt="logo" class="logo"/>
          </router-link>
        </div>
        <div class="header-container">
      <Header />
        </div>
      </div>
    <router-view />

    </v-app>
      <Footer />
  </div>
</template>

<script>
import Header from './components/header.vue'
import Footer from './components/footer.vue'
export default {
  name: 'App',
  components: {
    Header,
    Footer
  }
}
</script>

<style>
a {
  text-decoration: none;
}
#app {
  backface-visibility: hidden;
  min-width: 100%;
  min-height: 100%;
  position: relative;

}
.head{
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
}
.header-container {
  display: flex;
  align-items: center;
}
.logo-container {
  display: flex;
  align-items: center;
}

.logo {
  height: 36px;
  width: 50px;
  margin-outside: 10px;
}
</style>
