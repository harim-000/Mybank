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
    <h1 class="text-deep-purple-lighten-1">Find Password</h1>


    <v-text-field
        color="deep-purple-lighten-1"
        v-model="userName"
        :maxlength="20"
        counter="20"
        type="text"
        prepend-icon="mdi-account"
        :rules="[value => checkID(value, 'UserName')]"
        label="User Nickname"
        variant="outlined"
        class="form-field"
        style="color: #7E57C2;"
    />
    <v-text-field
        v-model="email"
        color="deep-purple-lighten-1"
        prepend-icon="mdi-email"
        :maxlength="100"
        base-color="deep-purple-lighten-1"
        :rules="[value => checkEmail(value, 'UserEmail')]"
        variant="outlined"
        label="User Email"
        style="color: #7E57C2;"
    ></v-text-field>
    <v-btn
        color="deep-purple-lighten-1"
        :loading="loading"
        class="mt-1"
        text="Find Password"
        block
        @click="login"
    ></v-btn>
    <v-card-text>

    </v-card-text>

  </v-sheet>


</template>

<script>
import axios from "@/axios/JwtToken";

export default {

  data() {
    return {
      loading: false,
      userName: '',
      email: '',
      emailCheck: '',
      visible: false,
      loginError: false
    };
  },
  methods: {

    checkID(value, fieldName) {
      console.log('Value:', value);
      if (!value) {
        console.log(`${fieldName} 를 입력해주세요.`);
        return `${fieldName} 를 입력해주세요.`;
      } else {
        return axios.post("/user/checkIdDuplicate", null, {
          params: {
            user_id: this.userName,
          },
        }).then((response) => {
          if (response.data === true) {
            return true;
          } else {
            return '일치하는 회원정보가 없습니다.';
          }
        }).catch((error) => {
          console.error(error);
          return '일치하는 회원정보가 없습니다.';
        });
      }

    },

    checkEmail(value, fieldName) {
      console.log('Value:', value);
      if (!value) {
        console.log(`${fieldName} 를 입력해주세요.`);
        return `${fieldName} 를 입력해주세요.`;
      } else {
        return axios.post("/user/checkIdEmailDuplicate", null, {
          params: {
            user_id: this.userName,
          },
        }).then((response) => {
            this.emailCheck = response.data.user_email;
          if(this.emailCheck === this.email) {
              return true;
          } else {
            return '일치하는 회원정보가 없습니다.'
          }
        }).catch((error) => {
          console.error(error);
          return '일치하는 회원정보가 없습니다.';
        });
      }
    },
    async login() {
      const emailValidationMessage = await this.checkEmail(this.email, 'User Email');
      const idValidationMessage = await this.checkID(this.userName, 'User Nickname');

      if (emailValidationMessage !== true) {
        alert(emailValidationMessage);
        return;
      } else if (idValidationMessage !== true) {
        alert(idValidationMessage);
        return;
      }
      try {
        console.log(this.email);
        const formData = new FormData();
        formData.append("email", this.email.toString());
        // 이메일 발송을 위한 API 엔드포인트로 요청을 보냅니다.
        const response = await axios.post("/user/findPwd", formData, {
          headers: {
            "Content-Type": "multipart/form-data", // 파일 업로드를 위한 Content-Type 설정
          },
        });
        if (response.status === 200 && response.data === "이메일 발송에 성공했습니다.") {
          alert("이메일 발송이 완료되었습니다.");
        } else {
          // 이메일 발송에 실패한 경우 오류 처리
          alert("이메일 발송 중 오류가 발생했습니다.");
        }
      } catch (error) {
        // 이메일 발송에 실패한 경우 오류 처리
        alert("이메일 발송 중 오류가 발생했습니다.");
        console.error(error);
      }
    }
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