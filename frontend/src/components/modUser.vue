<template>
  <v-sheet width="300" class="mx-auto">
    <br>
    <br>
    <br>
    <h1  class="text-deep-purple-lighten-1">Edit profile</h1>
    <br>
      <v-avatar  style="width: 200px; height: 200px;">
        <v-img
            :src="preview ? preview : userProfileImage"
            alt="profile_img"
        ></v-img>
      </v-avatar>
      <br>
      <br>
      <v-file-input
          v-model="profile"
          accept="image/png, image/jpeg, image/bmp, image/jpg"
          @change="previewFile"
          ref="fileInput"
          label="Profile Image"
          prepend-icon="mdi-camera"
          hint=".png, .jpeg, .bmp, .jpg 형식"
          persistent-hint
          @keyup.enter="mod"
          style="color: #5E35B1;"
      >
        <template v-slot:label>
          <span v-if="profile"></span>
          <span v-else>Profile Image</span>
        </template>
        <template v-slot:hint>
          .png, .jpeg, .bmp, .jpg 형식
        </template>
        <template v-slot:append-outer>
          <v-icon :color="profile ? 'green' : 'grey'">mdi-check</v-icon>
        </template>
      </v-file-input>
      <br>
      <v-text-field
          v-model="userName"
          :placeholder="userInfo.user_id"
          @keyup.enter="mod"
          style="color: #311B92;"
          readonly="true"
          variant="outlined"
      />
      <v-text-field
          v-model="email"
          :rules="[value => checkEmail(value)]"
          :label="userInfo.user_email"
          class="form-field"
          variant="outlined"
          name="user_email"
          hint="예시) test@test.com"
          persistent-hint
          @keyup.enter="mod"
          style="color: #5E35B1;"
          :readonly="false"
          ref="emailField"
      />
    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <v-btn
        v-if="emailFlag && !emailFieldReadonly"
        color="deep-purple-lighten-1"
        :maxlength="6"
        @click="sendmail"
    >
      인증메일 발송
    </v-btn>
    <v-dialog v-model="showCodeModal" max-width="400" persistent>
      <v-card>
        <v-card-title>인증 코드 입력</v-card-title>
        <v-card-text>
          받은메일 혹은 스팸메일함을 확인해 주세요
          <v-text-field
              v-model="verificationCode"
              label="인증 코드"
          ></v-text-field>
          <div v-if="countdown > 0">
            <p>남은 시간: {{ formatCountdown(countdown) }} </p>
          </div>
        </v-card-text>
        <v-card-actions>
          <v-btn @click="verifyCode">인증 완료</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
      <v-text-field
          :append-inner-icon="visible ? 'mdi-eye-off' : 'mdi-eye'"
          :type="visible ? 'text' : 'password'"
          class="form-field"
          v-model="originPassword"
          :rules="[value => checkPassword(value)]"
          variant="outlined"
          label="Password"
          @click:append-inner="visible = !visible"
          name="user_pass"
          hint="정보 변경 시 비밀번호를 입력해 주세요"
          persistent-hint
          @keyup.enter="mod"
          style="color: #5E35B1;"
      ></v-text-field>
      <v-text-field
          :append-inner-icon="visible ? 'mdi-eye-off' : 'mdi-eye'"
          :type="visible ? 'text' : 'password'"
          class="form-field"
          v-model="password"
          :rules="[value => checkNewPassword(value)]"
          variant="outlined"
          label="New Password"
          @click:append-inner="visible = !visible"
          name="user_pass"
          hint="영문자와 툭수문자 숫자 포함/ 6~12자"
          persistent-hint
          @keyup.enter="mod"
          style="color: #5E35B1;"
      ></v-text-field>
      <v-text-field
          v-model="repassword"
          :append-inner-icon="visible ? 'mdi-eye-off' : 'mdi-eye'"
          :type="visible ? 'text' : 'password'"
          class="form-field"
          :rules="[value => recheckPassword(value)]"
          variant="outlined"
          label="Re-Enter New Password"
          @click:append-inner="visible = !visible"
          hint="비밀번호를 다시 작성해 주세요"
          persistent-hint
          @keyup.enter="mod"
          style="color: #5E35B1;"
      ></v-text-field>
      <v-btn
          color="deep-purple-lighten-1"
          type="submit"
          :loading="loading"
          @click="mod"
          block
          class="mt-2">Submit</v-btn>
  </v-sheet>
</template>

<script>

import axios from "@/axios/JwtToken";
import store from "@/store";

export default {
  data() {
    return {
      userInfo: {
        user_profile: '',
        user_email: '',
        deep: true // 객체 내부의 값도 감시
      },
      rules: '',
      loading: false,
      userName: '',
      email: '',
      password: '',
      repassword: '',
      profile: new File(["foo"], "foo.txt", {
        type: "text/plain",
      }),
      visible: false,
      preview: '',
      saveFile: null,
      nicknameErrorMessage: '',
      userProfileImage: '',
      originPassword: '',
      duplicateIdMessage: '',
      duplicateEmailMessage: '',
      alertMessage: '',
      fileInput: null,
      code: '',
      emailFlag: false,
      verificationCode: '',
      countdown: 0,
      showCodeModal: false, // 인증 코드 입력 모달 표시 여부
      emailFieldReadonly: false,
    };
  },
  created() {
    this.fetchUserInfo();
  },
  watch: {
    password: {
      handler() {
        this.recheckPassword(this.repassword);
      },
      deep: true
    },
    profile: {
      handler() {
        console.log('watch 진입')
        console.log(this.profile)
        if (Array.isArray(this.profile) && this.profile.length === 0) {
          console.log('profile 조건문 진입')
          this.preview = require('@/assets/default_profile.png');
          this.$refs.fileInput.reset();
        }
      }
    }
  },
  methods: {
    formatCountdown(seconds) {
      const minutes = Math.floor(seconds / 60);
      const remainingSeconds = seconds % 60;
      return `${minutes} 분 ${remainingSeconds} 초`;
    },
    async sendmail() {

      try {
        console.log(this.email);
        const formData = new FormData();
        formData.append("email", this.email.toString());
        // 이메일 발송을 위한 API 엔드포인트로 요청을 보냅니다.
        await axios.post("/user/verify", formData, {
          headers: {
            "Content-Type": "multipart/form-data", // 파일 업로드를 위한 Content-Type 설정
          },
        });

        // 이메일 발송에 성공한 경우 사용자에게 메시지를 표시하거나 다음 단계로 이동합니다.
        // 추가 작업: 남은 시간 표시
        this.startCountdown();
        this.showCodeModal = true;
      } catch (error) {
        // 이메일 발송에 실패한 경우 오류 처리
        alert("이메일 발송 중 오류가 발생했습니다.");
        console.error(error);
      }
    },
    async verifyCode() {
      if(this.countdown === 0) {
        alert("인증 시간이 만료되었습니다.")
        this.email='';
        this.showCodeModal = false;
        this.emailFlag = false;
      }
      try {
        console.log(this.email);
        const formData = new FormData();
        formData.append("email", this.email.toString());
        formData.append("code", this.verificationCode.toString());
        // 이메일 발송을 위한 API 엔드포인트로 요청을 보냅니다.
        const response = await axios.post("/user/verify-code", formData, {
          headers: {
            "Content-Type": "multipart/form-data", // 파일 업로드를 위한 Content-Type 설정
          },
        });

        if (response.status === 200 && response.data === '인증이 완료되었습니다.') {
          // 코드 일치 시 처리
          console.log("코드 일치");
          alert("인증이 완료되었습니다.");
          this.showCodeModal = false;
          this.emailFlag = false;
          console.log(this.emailFlag);
          this.$refs.emailField.$el.querySelector('input').readOnly = true;
          this.emailFieldReadonly = true;
        } else {
          // 코드 불일치 시 처리
          console.log("코드 불일치");
          alert("인증 코드가 일치하지 않습니다.");
        }
      } catch (error) {
        // 요청 실패 또는 오류 처리
        console.error(error);
      }
    },
    startCountdown() {
      // 5분 카운트 다운 시작
      this.countdown = 180; // 180 = 3분
      const countdownInterval = setInterval(() => {
        if (this.countdown > 0) {
          this.countdown -= 1;
        } else {
          clearInterval(countdownInterval);
        }
      }, 1000); // 1초마다 감소
    },
    async fetchUserInfo() {
      this.user_id = store.state.login.member;

      axios.post(`/user/getUserInfo?user_id=${this.user_id}`)
          .then(response => {
            this.userInfo = response.data;
            console.log(this.userInfo.user_profile)
            console.log(require('@/assets/default_profile.png'), "디폴트")
            console.log(`/assets/profile/${this.userInfo.user_profile}`, "프로필")
            if (this.userInfo.user_profile) {
              this.userProfileImage = `/assets/profile/${this.userInfo.user_profile}`;
            } else {
              this.userProfileImage = require('@/assets/default_profile.png');
            }

            console.log(this.userInfo.user_id);
          })
          .catch(error => {
            console.error("Error fetching user info:", error);
            this.userInfo = {};
          });
    },
    async mod() {
      console.log("수정 시작");
      if(!await this.isFormValid()) {
        console.log(this.isFormValid() , 'this.isFormValid()')
        alert('형식에 맞게 입력해 주세요')
        return;
      } else if (this.emailFlag === true && this.$refs.emailField.$el.querySelector('input').readOnly === false) {
        console.log('이메일 인증 조건문에 진입했습니다.');
        console.log(this.emailFlag);
        alert("이메일 인증을 진행해 주세요")
        return; // 유효성 검사 실패 시 데이터 전송 중단
      }
      this.user_id = store.state.login.member;
      console.log(this.user_id, "수정 전 기존 아이디");

      const formData = new FormData();
      formData.append("user_id", this.user_id);
      formData.append("user_originPass", this.originPassword);
      if (this.email) {
        formData.append("user_email", this.email.toString());
      }
      if (this.password) {
        formData.append("user_pass", this.password);
      }
      if (this.profile) {
        formData.append("user_profile", this.profile[0]);
      }

      console.log("Formdata값 확인 :", formData);

      axios
          .post("/user/mod", formData, {
            headers: {
              "Content-Type": "multipart/form-data",
            },
          })
          .then((res) => {
            if (res) {
              alert("회원 수정이 완료되었습니다.");
              this.fetchUserInfo();
              this.clearPreview();
              window.location.reload(); // 현재 페이지 새로 고침
              this.loading = false;
              console.log(this.userInfo, "수정 후 유저정보");
            } else {
              alert("회원 수정 중에 오류가 발생했습니다.");
            }

          })
          .catch((err) => {
            alert("회원 수정 중에 오류가 발생했습니다.");
            console.error(err);
          });
    },

    async isFormValid() {
      // 각 유효성 검사 메서드 호출하여 결과 확인
      console.log(this.checkEmail(this.email))
      console.log(this.checkPassword(this.originPassword))
      console.log(this.checkNewPassword(this.password))
      console.log(this.recheckPassword(this.repassword))


      const emailPromise = this.checkEmail(this.email);
      const [emailValid] = await Promise.all([
        emailPromise,
      ]);
      console.log(emailValid)
      // 모든 유효성 검사를 통과해야 유효한 폼으로 간주
      return (
          emailValid === true &&
          this.checkPassword(this.originPassword) === true &&
          this.checkNewPassword(this.password) === true &&
          this.recheckPassword(this.repassword) === true
      );
    },
    checkEmail(value) {
      const email = /^[0-9a-zA-Z]{1,50}([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/i;
      if (!value) {
        return true;
      }
      else if(!email.test(value)) {
        this.emailFlag = false;
        return `이메일 형식에 맞게 작성해 주세요.`
      } else if (value.length > 50) {
        this.emailFlag = false;
        return '입력 가능한 글자수를 초과했습니다. ';
      } else {
        return axios.post("/user/checkEmailDuplicate", null, {
          params: {
            user_email: this.email,
          },
        }).then((response) => {
          if (response.data === true) {
            this.emailFlag = false;
            return '이미 사용 중인 이메일입니다'
          } else {
            this.emailFlag = true;
            return true;
          }
        }).catch((error) => {
          console.error(error);
          return false;
        });
      }
    },
    checkPassword(value) {
      if (!value) {
        return '기존 비밀번호를 입력해 주세요.';
      }
      return true;
    },
    checkNewPassword(value) {
      let reg = /^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{6,12}$/
      const space = /\s/g;
      let checkKor = /[ㄱ-ㅎ|ㅏ-ㅣ|가-힣]/;

      if (!value) {
        return true;
      } else if (!reg.test(value)) {
        return '영문자, 숫자, 특수문자를 모두 포함한 6~12자 형식으로 입력해 주세요.';
      }
      else if(space.test(value)) {
        return 'User Nickname 에는 공백을 사용할 수 없습니다.'
      }
      else if(checkKor.test(value)) {
        return 'User Nickname 에는 한글을 사용할 수 없습니다.'
      }
      return true;
    },
    recheckPassword(value) {
      if (this.password !== '' && value !== this.password) {
        return '비밀번호가 다릅니다.';
      }
      return true;
    },
    previewFile(event){

      const file = event.target.files[0]; // 첫 번째 파일 가져오기
      if (file) {
        const maxSize = 2 * 1024 * 1024; // 2MB 제한 (원하는 크기로 수정 가능)
        const allowedExtensions = ['png', 'jpeg', 'bmp', 'jpg'];
        const fileName = file.name.toLowerCase();
        const fileExtension = fileName.slice(((fileName.lastIndexOf(".") - 1) >>> 0) + 2);

        if (file.size > maxSize) {
          alert("프로필 이미지의 크기는 2MB 이하여야 합니다.");
          this.$refs.fileInput.reset();
          return;
        }

        if (!allowedExtensions.includes(fileExtension)) {
          alert("허용되지 않는 파일 형식입니다. 허용된 형식은 png, jpeg, bmp, jpg 입니다.");
          this.$refs.fileInput.reset();
          return;
        }

        const reader = new FileReader();
        reader.onload = () => {
          this.preview = reader.result; // 미리보기 이미지 설정
        };
        reader.readAsDataURL(file);
      }
      return true;
    },
    clearPreview() {
      this.profile = []; // 프로필 초기화
      this.preview = null; // 미리보기 이미지도 초기화
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
.mt-2 {
  margin-bottom: 80px;
}
</style>