<template>
  <v-sheet min-width="300" class="mx-auto" >
    <br>
    <br>
    <br>
    <h1 class="text-deep-purple-lighten-1">Find Id</h1>


    <v-text-field
        color="deep-purple-lighten-1"
        v-model="email"
        :maxlength="50"
        type="text"
        prepend-icon="mdi-account"
        :rules="[value => checkEmail(value, 'UserEmail')]"
        label="User Email"
        variant="outlined"
        class="form-field"
        style="color: #7E57C2;"
    />
    <v-btn
        color="deep-purple-lighten-1"
        :loading="loading"
        class="mt-1"
        text="Find Id"
        block
        @click="sendId"
    ></v-btn>
    <br>
    <br>
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
      email: '',
    };
  },
  methods: {

    checkEmail(value, fieldName) {
      console.log('Value:', value);
      if (!value) {
        console.log(`${fieldName} 를 입력해주세요.`);
        return `${fieldName} 를 입력해주세요.`;
      } else {
        return axios.post("/user/checkEmailDuplicate", null, {
          params: {
            user_email: this.email,
          },
        }).then((response) => {
          if (response.data === true) {
            return true;
          } else {
            return '이메일 정보가 없습니다.'
          }
        }).catch((error) => {
          console.error(error);
          return '일치하는 회원정보가 없습니다.';
        });
      }
    },
    async sendId() {
      const validationMessage = await this.checkEmail(this.email, 'User Email');
      if (validationMessage !== true) {
        alert(validationMessage);
        return; // 유효하지 않은 이메일이면 이후 코드 실행을 막기 위해 return 사용
      }
      try {
        console.log(this.email);
        const formData = new FormData();
        formData.append("email", this.email.toString());
        // 이메일 발송을 위한 API 엔드포인트로 요청을 보냅니다.
        const response = await axios.post("/user/findId", formData, {
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