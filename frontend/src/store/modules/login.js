import router from '@/router';
import axios from '@/axios/JwtToken'


const login = {
    namespaced: true,
    state: {
        member: null,
        token: null,
        userInfo: null, // 사용자 정보 추가
    },
    getters: {
        isAuthenticated: (state) => !!state.token && !!state.member,

    },
    mutations: {
        setMember(state, payload) {
            state.member = payload;
            console.log(payload, "페이로드");
            console.log(state.member, "멤버");
        },
        setToken(state, payload) {
            state.token = payload;
            state.member = payload;
        },
        logout(state) {
            state.member = null;
            state.token = null;
            // 추가: 로그아웃 시 세션 스토리지의 토큰과 멤버 정보 삭제
            sessionStorage.removeItem('token');
            sessionStorage.removeItem('member');
        }
    },
    actions: {
        login(context, { id, pw }) {
            axios.post("/login", { id, pw })
                .then((response) => {
                    console.log("로그인 성공", response);

                    // 토큰 저장
                    context.commit("setToken", response.headers.token);

                    // 사용자 정보 저장
                    context.commit("setMember", response.headers.user);

                    // 페이지 이동 - 이전 페이지 정보가 있을 시 이전페이지, 없을 시 메인페이지
                    // router.push({ path: store.state.local.location==null ? '/' : store.state.local.location });
                    router.push({ path: '/' });
                })
                .catch(() => {
                    alert("로그인에 실패했습니다.");
                });
        },
    },
};

export default login;