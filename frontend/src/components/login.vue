<template>
  <v-sheet min-width="300" class="mx-auto" >
    <v-alert
        v-if="loginError"
        color="warning"
        icon="$error"
        variant="outlined"
        border="top"
        title=""
        text="회원정보를 다시 입력해 주세요"
    ></v-alert>
    <br>
    <br>
    <br>
    <h1 class="text-deep-purple-lighten-1">Log In</h1>


    <v-text-field
        color="deep-purple-lighten-1"
        v-model="userName"
        :maxlength="20"
        counter="20"
        type="text"
        prepend-icon="mdi-account"
        :rules="[value => checkNotNull(value, 'UserName')]"
        label="User Nickname"
        variant="outlined"
        class="form-field"
        @keyup.enter="login"
        style="color: #7E57C2;"
    />
    <v-text-field
        color="deep-purple-lighten-1"
        :maxlength="12"
        counter="12"
        :append-inner-icon="visible ? 'mdi-eye-off' : 'mdi-eye'"
        base-color="deep-purple-lighten-1"
        :type="visible ? 'text' : 'password'"
        prepend-icon="mdi-lock"
        v-model="password"
        :rules="[value => checkNotNull(value, 'Password')]"
        variant="outlined"
        label="Password"
        @click:append-inner="visible = !visible"
        @keyup.enter="login"
        style="color: #7E57C2;"
    ></v-text-field>
    <v-btn
        color="deep-purple-lighten-1"
        :loading="loading"
        class="mt-1"
        text="Log In"
        block
        @click="login"
        @keyup.enter="login"
    ></v-btn>
    <br>
    <v-btn
        color="deep-purple-lighten-1"
        value="join"
        class="mt-1"
        block
        text="회원가입"
        to="/join"
    />
    <br>
    <h4 class="text-deep-purple-lighten-1">회원 정보를 잊어버리셨나요?</h4>
    <v-btn
        color="deep-purple-accent-4"
        variant="plain"
        prepend-icon="mdi-account"
        value="find_id"
        text="아이디 찾기"
        to="/find_id"
    />
    &nbsp;&nbsp;&nbsp;&nbsp;
    <v-btn
        color="deep-purple-accent-4"
        variant="plain"
        prepend-icon="mdi-lock"
        value="find_pwd"
        text="비밀번호 찾기"
        to="/find_pwd"
    />
    <v-card-text>

    </v-card-text>

  </v-sheet>


</template>

<script>
import axios from "@/axios/JwtToken";
import router from "@/router";

export default {

  data() {
    return {
      loading: false,
      userName: '',
      password: '',
      visible: false,
      loginError: false
    };
  },
  methods: {

    checkNotNull(value, fieldName) {
      console.log('Value:', value);
      if (!value) {
        console.log(`${fieldName} 를 입력해주세요.`);
        return `${fieldName} 를 입력해주세요.`;
      }
      return true;
    },
    async login() {
      try {
        const result = await axios.post('/login', {
          user_id: this.userName,
          user_pass: this.password
        });
        if (result.status === 200) {
          this.loginError = false;
          await router.push("/");
        }
        this.loginError = false;
      } catch (err) {
        this.loginError = true;
        console.log(err);
      }
    },
  },
};
</script>

<style>
a {
  text-decoration: none;
}
v-sheet {
  max-width: 300px;
  margin: 0 auto;
  padding-top: 50%;
}

.form-field {
  width: 100%;
  margin: 8px 0;
}
</style>