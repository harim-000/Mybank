import {createApp} from 'vue'
import router from '@/router/index';
import App from './App.vue'
import { library } from '@fortawesome/fontawesome-svg-core'
import { fas } from '@fortawesome/free-solid-svg-icons'
import { far } from '@fortawesome/free-regular-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
// Vuetify
import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'

import 'vuetify/dist/vuetify.css'
import '@mdi/font/css/materialdesignicons.css' // Ensure you are using css-loader
import SimpleVueValidation from 'simple-vue-validator';
import axios from "axios";
import store from "@/store";
import VueApexCharts from 'vue3-apexcharts';
import * as labsComponents from 'vuetify/labs/components'
import {VDatePicker} from "vuetify/lib/labs/VDatePicker";

const vuetify = createVuetify({
    components: {
        ...components,
        ...labsComponents,
        VDatePicker,

    },
    directives,
    icons: {
        defaultSet: 'mdi',
        theme: { defaultTheme: 'light' },
    },
})

library.add(fas, far)


const app = createApp(App);
app.config.globalProperties.axios = axios;
axios.defaults.baseURL = "http://localhost:8080";

app.component('font-awesome-icon', FontAwesomeIcon);
app.use(VueApexCharts);
app.use(SimpleVueValidation);
app.use(router).use(vuetify);
app.use(store);
app.mount('#app');
