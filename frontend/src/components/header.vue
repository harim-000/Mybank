<script>
import { computed } from 'vue';
import axios from "@/axios/JwtToken";

export default {
  name: 'Header',
  data() {
    return {
      tab: 1,
      defaultProfileImageSrc: require('@/assets/default_profile.png'),
      isAuthenticated: computed(() => !!this.$store.state.login.token),
      userInfo: {
        user_profile: require('@/assets/default_profile.png'), // 기본 프로필 이미지 경로 설정
        handler: true
      },
      userProfileImage: '',
    };
  },
  computed: {
    profileImageSrc() {
      console.log(this.userInfo.user_profile, "헤더 프로필 표시직전")
      console.log(this.userInfo.user_profile
          ? require(`@/assets/default_profile.png`)
          : this.defaultProfileImageSrc);
      return this.userInfo.user_profile
          ? `/assets/profile/${this.userInfo.user_profile}`
          : this.defaultProfileImageSrc;
    }
  },
  watch: {
    '$store.state.login.member': {
      handler(newUserId, oldUserId) {
        if (newUserId !== oldUserId) {
          console.log("프로필 바뀜?")
          this.fetchUserInfo();
          this.updateProfileImage();
        }
      },
      immediate: true, // 이벤트 핸들러를 초기화 시점에 실행
    },
  },
  mounted() {
    this.fetchUserInfo();
    this.updateProfileImage();
    console.log(this.profileImageSrc, "프로필 이미지 경로");
  },
  methods: {
    updateProfileImage() {
      if (this.userInfo.user_profile) {
        this.userProfileImage = `/assets/profile/${this.userInfo.user_profile}`;
      } else {
        this.userProfileImage = this.defaultProfileImageSrc;
      }
    },
    async fetchUserInfo() {
      try {
        const user_id = this.$store.state.login.member;
        const response = await axios.post(`/user/getUserInfo?user_id=${user_id}`);
        this.userInfo = response.data;
        this.updateProfileImage(); // 이 부분을 추가하여 프로필 이미지 업데이트
        console.log(this.userInfo.user_id);
        console.log(this.userInfo.user_profile, "헤더 프로필");
        console.log(user_id, "헤더 아이디");
      } catch (error) {
        console.error("Error fetcdhing user info:", error);
      }
    },
  }
};
</script>

<template>
  <v-tabs v-model="tab" color="deep-purple-accent-4" align-tabs="end">
    <v-tab :value="1" :to="{ path: '/' }">Home</v-tab>
    <v-tab v-if="isAuthenticated" :value="2" :to="{ path: '/notice' }">NOTICE</v-tab>
    <v-tab v-if="isAuthenticated" :value="3" :to="{ path: '/faq' }">Q&A / REVIEW</v-tab>
    <v-tab v-if="isAuthenticated" :value="4" :to="{ path: '/mypage' }">
      <v-avatar  style="width: 50px; height: 50px;">
        <img
            v-if="userInfo.user_profile"
            :src="profileImageSrc"
            alt="Profile Image"
            :key="userInfo.user_profile"
            style="width: 50px; height: 50px;"
        />
        <img
            v-else
            :src="defaultProfileImageSrc"
            alt="Default Profile Image"
            style="width: 50px; height: 50px;"
        />
      </v-avatar>
    </v-tab>
    <!-- 로그인되지 않았을 때만 login 탭 보이도록 설정 -->
    <v-tab v-if="!isAuthenticated" :value="4" :to="{ path: '/login' }">login</v-tab>
  </v-tabs>
</template>

<style scoped>
a{
  text-decoration: none;
}
</style>