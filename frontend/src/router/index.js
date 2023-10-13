import {createRouter, createWebHashHistory} from 'vue-router';
import axios from "axios";


const routes = [
        {
            path: '/',
            component: () => import('@/components/home.vue'),
        },
        {
            path: '/login',
            component: () => import('@/components/login.vue'),
        },
        {
            path: '/find_id',
            component: () => import('@/components/find_id.vue'),
        },
        {
            path: '/find_pwd',
            component: () => import('@/components/find_pwd.vue'),
        },
        {
            path: '/footer',
            component: () => import('@/components/footer.vue')
        },
        {
            path: '/header',
            component: () => import('@/components/header.vue')
        },
        {
            path: '/join',
            component: () => import('@/components/join.vue')
        },
        {
            path: '/notice',
            component: () => import('@/components/notice.vue')
        },
        {
            path: '/noticeCont',
            component: () => import('@/components/notice_content.vue')
        },
        {
            path: '/faq',
            component: () => import('@/components/faq.vue')
        },
        {
            path: '/mypage',
            component: () => import('@/components/mypage.vue')
        },
        {
            path: '/mod',
            component: () => import('@/components/modUser.vue')
        },
        {
            path: '/write',
            component: () => import('@/components/faq_write.vue')
        },
        {
            path: '/faqCont',
            component: () => import('@/components/faq_content.vue')
        },
        {
            path: '/my-commented-posts',
            component: () => import('@/components/myCommentedPosts.vue')
        },
        {
            path: '/my-questions',
            component: () => import('@/components/myQuestions.vue')
        },
        {
            path: '/error',
            component: () => import('@/components/error.vue')
        },
        {
            path: '/:pathMatch(.*)*',
            redirect: '/error',
        }


    ];


// 화면이동 시 위로 스크롤
const router = createRouter({
    scrollBehavior() {
        return new Promise((resolve) => {
            setTimeout(() => {
                resolve({ x: 0, y: 0 })
            }, 500)
        })
    },
    history: createWebHashHistory(),
    routes,
});

// 전역 네비게이션 가드 설정
router.beforeEach(async (to, from, next) => {

    axios.get("/").then(response=>{
        console.log(response);
    });

    await next();
});

export default router;