<template>
  <br>
  <br>
  <br>
  <v-card
      class="mx-auto" style="width: 70%; height: 50%; max-width: none;"
      color="deep-purple-lighten-1"
  >
    <v-card-item>
      <v-card-title>
        <v-avatar  style="width: 100px; height: 100px;">
          <img
              v-if="userInfo.user_profile"
              :src="`/assets/profile/${this.userInfo.user_profile}`"
              alt="Profile Image"
              style="width: 100px; height: 100px;"
          />
          <img
              v-else
              :src="require(`@/assets/default_profile.png`)"
              alt="Default Profile Image"
              style="width: 100px; height: 100px;"
          />
        </v-avatar>
        &nbsp;&nbsp;&nbsp;
        <span class="text-h6">{{ userInfo.user_id }}</span>
      </v-card-title>

      <template v-slot:append>
        <v-defaults-provider
            :defaults="{
            VBtn: {
              variant: 'text',
              density: 'comfortable',
            }
          }"
        >

          <v-btn
              icon="mdi-pencil"
              to="/mod"
          ></v-btn>

        </v-defaults-provider>
      </template>
    </v-card-item>

    <v-list>
      <v-list-item
          prepend-icon="mdi-email"
          :title="userInfo.user_email"
          style="color: #5E35B1;"
      ></v-list-item>

      <v-divider inset></v-divider>

      <v-list-item
          to="/my-questions"
          prepend-icon="mdi-text-box-edit"
          title="나의 질문글"
          style="color: #5E35B1;"
      ></v-list-item>

      <v-divider inset></v-divider>

      <v-list-item
          to="/my-commented-posts"
          prepend-icon="mdi-message-text"
          title="내가 댓글을 단 게시글"
          style="color: #5E35B1;"
      ></v-list-item>
    </v-list>
    <v-btn
        color="deep-purple-lighten-5"
        variant="plain"
        value="sign_out"
        text="회원탈퇴"
        @click="showAlert = true"
    />
    <v-btn
        color="deep-purple-lighten-5"
        variant="plain"
        value="logout"
        @click="logout"
    >로그아웃</v-btn>
  </v-card>
  <v-dialog v-model="showAlert" max-width="500px">
    <v-card>
      <v-card-title class="headline" color="deep-purple-darken-4">회원탈퇴</v-card-title>
      <v-card-text color="deep-purple-darken-4">
        회원탈퇴를 하시면 지금까지 기록한 가계부 기록이 사라집니다! 정말 탈퇴하실 건가요?
      </v-card-text>
      <v-card-actions>
        <v-btn color="green-darken-1" text @click="showAlert = false">취소</v-btn>
        <v-btn color="pink-lighten-1" text @click="del">탈퇴</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
import axios from "axios";
import store from "@/store";
import router from "@/router/index";

export default {
  data() {
    return {
      userInfo: {
        user_profile: null,
      },
      user_id: '',
      showAlert: false
    };
  },
  async created() {
    try {
      this.user_id = store.state.login.member; // user_id를 가져옴
      const response = await axios.post(`/user/getUserInfo?user_id=${this.user_id}`);
      this.userInfo = response.data; // 가져온 사용자 정보를 저장
      console.log(this.userInfo.user_id);
      console.log(this.userInfo.user_profile, "마이페이지 프로필");
      console.log(this.user_id, "마이페이지 아이디");
    } catch (error) {
      console.error("Error fetching user info:", error);
    }
  },
  methods: {
    async del() {
      try {
        this.user_id = store.state.login.member; // user_id를 가져옴
        const result = await axios.post(`/user/del?user_id=${this.user_id}`);
        if (result.status === 200) {
          alert("회원 탈퇴가 완료되었습니다.");
          await this.logout();
          await router.push("/login");
        }
      } catch (err) {
        alert("회원 탈퇴 중에 오류가 발생했습니다.");
        console.error(err);
      }
    },
    async logout() {
      sessionStorage.clear();
      store.commit('login/logout', null); // 상태 변경
      router.push("/login");
    },
  },
};
</script>
<style>

</style>