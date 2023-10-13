// store/index.js
import { createStore } from "vuex";
import login from "@/store/modules/login";
import createPersistedState from 'vuex-persistedstate';
import home from "@/store/modules/home";

const store = createStore({
    modules: {
        login, home
    },
    plugins: [
        createPersistedState({
            paths: ['login'],
            storage: window.sessionStorage,
        }),
    ]
});

export default store;