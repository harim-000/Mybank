<template>
  <v-card class="mx-auto" style="width: 90%; max-width: none;">
      <v-radio-group v-model="selectedOption">
        <v-radio label="공개" value="public"></v-radio>
        <v-radio label="비공개" value="private"></v-radio>
      </v-radio-group>
        <v-text-field v-model="title" label="제목" :maxlength="20" counter="20"></v-text-field>
        <v-textarea v-model="content" label="내용" :maxlength="1000" counter="1000"></v-textarea>
        <v-divider></v-divider>

      <v-btn variant="text" @click="resetForm" color="pink-lighten-1"> 다시쓰기 </v-btn>
      <v-spacer></v-spacer>
      <v-btn
          type="submit"
          @click="writeFaq"
          color="deep-purple-accent-4"
      >
        게시글 등록
      </v-btn>
  </v-card>
</template>

<script>
import axios from "axios";
import store from "@/store";
import {required} from "simple-vue-validator/src/templates";
import router from "@/router";

export default {
  data() {
    return {
      selectedOption: '',
      isPublic: false,
      isPrivate: false,
      content: '',
      title: '',
    };
  },
  computed: {
    // writer에 processHTML 함수 적용
    processedContent() {
      return this.sanitizeInput(this.content);
    },
    // title에 processHTML 함수 적용
    processedTitle() {
      return this.sanitizeInput(this.title);
    },
  },
  watch: {
    selectedOption(newOption) {
      if (newOption === 'public') {
        this.isPublic = true;
        this.isPrivate = false;
      } else if (newOption === 'private') {
        this.isPublic = false;
        this.isPrivate = true;
      }
    }
  },
  validations: {
    selectedOption: { required },
    content: { required },
    title: { required },
  },

  methods: {
    sanitizeInput(value) {
      value = value.replace(/</g, "&lt;").replace(/>/g, "&gt;");
      value = value.replace(/\(/g, "&#40;").replace(/\)/g, "&#41;");
      value = value.replace(/eval\((.*)\)/g, "");
      value = value.replace(/["'][\s]*javascript:(.*)["']/g, "\"\"");
      return value;
    },
    resetForm() {
      this.selectedOption = ''; // 라디오 버튼 초기화
      this.content = ''; // 내용 초기화
      this.title = ''; // 제목 초기화
    },

    writeFaq() {
      this.curr_user_id = store.state.login.member;
      const visibility = this.isPublic ? "0" : (this.isPrivate ? "1" : "0");
      const postData = {
        open: visibility,
        content:this.processedContent, // XSS 방어
        user_id: this.curr_user_id,
        title: this.processedTitle, // XSS 방어
      };

      // 제목과 내용이 빈 문자열인지 확인
      if (this.title === "") {
        alert('제목을 입력해 주세요');
      }
      else if (this.content === "") {
        alert('내용을 입력해 주세요');
      }
      else if (this.selectedOption === "") {
        alert('공개 여부를 입력해 주세요');
      }
        // 제목과 내용이 모두 비어 있지 않은 경우에만 게시글 등록 시도
      else {  axios.post('faq/write', postData)
            .then(() => {
              alert("게시글 추가 성공")
              router.push("/faq");
            })
            .catch((e) => {
              console.error(e)
              alert("게시글 추가 실패")
              this.resetForm(); // 게시글 등록 후 폼 초기화
            });
      }
    },
  }
};
</script>

<style scoped>

</style>