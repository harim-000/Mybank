import axios from "axios";
import store from "@/store";
import router from "@/router/index";

// Add a request interceptor
axios.interceptors.request.use(
    function (config) {

        console.log(store.state.login, "로그인 객체 확인");
        const token = store.state.login.token;
        const member = store.state.login.member;
        config.headers.token = encodeURIComponent(token);
        config.headers.member = encodeURIComponent(member);

        if (token) {
            // ISO-8859-1로 변환하여 설정
            config.headers.token = token;
            config.headers.member = encodeURIComponent(member);

        }
        return config;
    },
    function (error) {
        return Promise.reject(error);


    }
);

axios.interceptors.response.use(
    (response) => {
        //console.log("악시오스 인터셉터 시작", response.headers);

        if(response.headers.token && response.headers.user) {
            // 토큰 저장
            store.commit("login/setToken", decodeURIComponent(response.headers.token));
            console.log(response.headers.token, "토큰저장")
            // 사용자 아이디 저장
            store.commit("login/setMember", decodeURIComponent(response.headers.user));
            console.log(response.headers.user, "사용자저장")

        }

        return response;
    },
    (error) => {
        console.log(error);
        // 토큰 없음 에러 : 토큰 만료
        if (error.response.status === 401) {
            if(store.state.login.token!=null) {
                sessionStorage.removeItem("token");
                sessionStorage.removeItem("member");
                store.commit("login/logout"); // Vuex 상태를 초기화하고 로그아웃 처리
                alert("다시 로그인해주세요.");
                router.push({path: '/login'});
            }
            return;
        }
        else if (error.response.status === 405) {
            if(store.state.login.token!=null) {
                sessionStorage.removeItem("token");
                sessionStorage.removeItem("member");
                store.commit("login/logout"); // Vuex 상태를 초기화하고 로그아웃 처리
                alert("다시 로그인해주세요.");
                router.push({path: '/login'});
            }
            return;

        }
        // 권한 없음 에러
        else if(error.response.status === 403) {
            if(store.state.login.token==null) {
                router.push({path: '/login'});
            }
            else if(store.state.login.token!=null) {
                router.push({path: '/error'});
            }
        }
    }
);
export default axios;
