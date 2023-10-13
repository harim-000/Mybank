<template>

  <v-card class="mx-auto" style="width: 90%; max-width: none;" v-if="secretFlag">
    <div v-if="isEditable">
      <v-radio-group v-model="selectedOption" :readonly="!isEditable">
        <v-radio label="공개" value="public"></v-radio>
        <v-radio label="비공개" value="private"></v-radio>
      </v-radio-group>
    </div>
    <div>{{ dateLabel }}</div>
    <div>{{ dateval }}</div>
    <br>
    <h4>작성자</h4>
    <div
        v-html="this.user_id"
        class="writer-container"
    >
    </div>
    <br>
    <h4>제목</h4>
    <div v-if="!isEditable" v-text="this.title" class="title-container"></div>
    <div v-else-if="isEditable">
    <v-text-field v-model="this.title" :maxlength="20" :value="processedTitle" label="제목" :readonly="!isEditable">
        <template>
          <v-counter v-model="this.title" :maxlength="20" :disabled="!isEditable"></v-counter>
        </template>
      </v-text-field>
    </div>
    <br>
    <h4>내용</h4>
    <div v-if="!isEditable" v-text="this.content" class="content-container"></div>
    <div v-else-if="isEditable">
    <v-textarea v-model="this.content" :maxlength="1000" :value="processedContent" label="내용" :readonly="!isEditable" no-resize>
        <template>
          <v-counter v-model="this.content" :maxlength="1000" :disabled="!isEditable"></v-counter>
        </template>
      </v-textarea>
    </div>
    <br>
    <v-spacer></v-spacer>
    <v-btn
        type="submit"
        color="deep-purple-accent-4"
        @click="toggleEditable"
        v-if="this.user_id === curr_user_id && !isEditable"
    >
      수정
    </v-btn>
    <v-btn
        type="submit"
        color="deep-purple-accent-4"
        @click="updateFaq"
        v-if="this.user_id === curr_user_id && isEditable"
    >
    저장
    </v-btn>
    &nbsp;&nbsp;&nbsp;&nbsp;
    <v-btn
        type="submit"
        color="deep-purple-accent-4"
        @click="deleteFaq"
        v-if="this.user_id === curr_user_id && !isEditable"
    >
      삭제
    </v-btn>
    <br>
    <v-spacer></v-spacer>
    <!-- ... (기존 코드) ... -->
    <v-divider></v-divider>
    <v-spacer></v-spacer>
    <br>
    <v-textarea
        v-model="newReplyContent"
        :maxlength="500"
        counter="500"
        label="댓글"
        style="height: 80px"
        no-resize
    ></v-textarea>
    <br>
    <br>
    <br>
    <br>
    <!-- 저장 버튼 (답변 추가) -->
    <v-btn
        type="submit"
        color="deep-purple-accent-4"
        @click="addReply"
    >
      답변 추가
    </v-btn>
    <br>
    <br>
    <transition-group name="fade" tag="div">
      <div v-for="(reply) in replies" :key="reply.idx">
        <v-divider></v-divider>
        <h5>작성자</h5>
        <div class="writer-container">{{ reply.user_id }}</div>
        <br>
        <h5>내용</h5>
        <div v-if="!reply.isReplyEditable" class="title-container">{{ reply.content }}</div>
        <div v-else-if="reply.isReplyEditable">
          <v-text-field
              :readonly="!reply.isReplyEditable"
              v-model="reply.content"
              :value="processedReplyContent(reply)"
              :maxlength="500"
              counter="500"
              label="내용"
          ></v-text-field>
        </div>
        <br>
        <v-spacer></v-spacer>
        <v-btn
            type="submit"
            color="deep-purple-accent-4"
            @click="toggleReplyEdit(reply)"
            v-if="reply.user_id === curr_user_id && !reply.isReplyEditable"
        >
          수정
        </v-btn>
        <v-btn
            type="submit"
            color="deep-purple-accent-4"
            @click="updateReply(reply)"
            v-if="reply.user_id === curr_user_id && reply.isReplyEditable"
        >
          저장
        </v-btn>
        &nbsp;&nbsp;&nbsp;&nbsp;
        <v-btn
            type="submit"
            color="deep-purple-accent-4"
            @click="deleteReply(reply)"
            v-if="reply.user_id === curr_user_id && !reply.isReplyEditable"
        >
          삭제
        </v-btn>
        <v-spacer></v-spacer>
        <br>

      </div>
    </transition-group>


  </v-card>
</template>

<script>
import axios from "axios";
import store from "@/store";
import router from "@/router";
export default {
  data() {
    return {
      selectedOption: "",
      secretFlag: false,
      title: "",
      content: "",
      mod_date: "",
      isValid: false,
      user_id: "",
      idx: "",
      isEditable: false,
      curr_user_id: '',
      newReplyContent: "",
      replies: "",
      reg_date: "",
      dateLabel: "",
      dateval: "",
    };
  },
  watch: {
    // mod_date를 감시하여 변경될 때 dateLabel 계산된 속성을 업데이트
    mod_date: function(newModDate) {
      this.dateLabel = this.reg_date === newModDate ? '등록일' : '수정일';
      this.dateval = this.reg_date === newModDate ? this.reg_date.substring(0, 10) : this.mod_date.substring(0, 10);
    },
  },
  computed: {
    // user_id processHTML 함수 적용
    processedContent() {
      return this.processHTML(this.content);
    },
    // title에 processHTML 함수 적용
    processedTitle() {
      return this.processHTML(this.title);
    },
  },
  created() {
    this.curr_user_id = store.state.login.member;
    this.idx = this.$route.query.idx;
    //console.log(this.idx, "인덱스 있음?");
    this.fetchFaqContent().then(()=>{
      if (this.curr_user_id !== this.user_id && this.selectedOption === 'private') {
        router.replace('/error');
      } else {
        this.secretFlag=true;
      }
    }).catch((error) => {
      // 데이터를 가져오지 못한 경우, 즉 존재하지 않는 idx
      console.error('게시글 내용 불러오기 오류:', error);
      router.replace('/error'); // 존재하지 않는 idx일 경우 /error로 리디렉션
    });
    this.fetchReplies();
  },
  methods: {
    sanitizeInput(value) {
      value = value.replace(/</g, "&lt;").replace(/>/g, "&gt;");
      value = value.replace(/\(/g, "&#40;").replace(/\)/g, "&#41;");
      value = value.replace(/eval\((.*)\)/g, "");
      value = value.replace(/["'][\s]*javascript:(.*)["']/g, "\"\"");
      return value;
    },
    processHTML(html) {
      const escapedHTML = html
          .replace(/&lt;/g, '<')
          .replace(/&gt;/g, '>')
          .replace(/&#40;/g, '(')
          .replace(/&#41;/g, ')')
          .replace(/&#39;/g, "'");
      return escapedHTML;
    },
    processedReplyContent(reply) {
      return this.processHTML(reply.content);
    },
    resetForm() {
      this.content = "";
      this.isValid = false;
    },
    async fetchFaqContent() {
      await axios.post(`/faq/content/${this.idx}`)
          .then(response => {
            const faqContent = response.data;
            if (!faqContent || Object.keys(faqContent).length === 0) {
                  router.replace('/error');
                } else {
                  this.selectedOption = faqContent.open === "1" ? "private" : "public";
                  this.title = faqContent.title;
                  this.content = faqContent.content;
                  this.user_id = faqContent.user_id;
                  this.mod_date = faqContent.mod_date;
                  this.reg_date = faqContent.reg_date;
                  this.dateLabel = faqContent.reg_date === faqContent.mod_date ? '등록일' : '수정일';
                  this.dateval = faqContent.reg_date === faqContent.mod_date ?  faqContent.reg_date.substring(0, 10) :  faqContent.mod_date.substring(0, 10);
                }
          })
          .catch(error => {
            console.error('게시글 내용 불러오기 오류:', error);
            router.replace('/error');
          });
    },
    updateFaq() {
      const data = new URLSearchParams();
      data.append('idx', this.idx);
      data.append('content', this.sanitizeInput(this.content));
      data.append('title', this.sanitizeInput(this.title));
      data.append('open', this.selectedOption === 'private' ? "1" : "0");

      const config = {
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
        }
      };
      if(this.title === "") {
        alert("제목을 입력해 주세요")
      }
      else if(this.content === "") {
        alert("내용을 입력해 주세요")
      }
      else if(this.selectedOption === "") {
        alert("공개 여부를 입력해 주세요")
      }
      else {
        axios.post("/faq/update", data, config)
            .then(() => {
              console.log(this.idx);
              this.toggleEditable(); // 토글 상태 변경
              this.fetchFaqContent();
              alert('게시글이 수정되었습니다.');
            })
            .catch(error => {
              console.error('게시글 수정 오류:', error);
            });
        }
      },
      deleteFaq() {
      if(!confirm("정말 삭제하시겠습니까?")) {
        return false;
      }
      const data = new URLSearchParams();
      data.append('idx', this.idx);

        const config = {
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
          }
        };
        axios.post("/faq/delete", data, config)
            .then(() => {
              alert('게시글이 삭제되었습니다.');
              router.push("/faq");
            })
            .catch(error => {
              console.error('게시글 삭제 오류:', error);
            });
      },
      toggleEditable() {
        this.isEditable = !this.isEditable;
      },
      toggleReplyEdit(reply) {
        reply.isReplyEditable = !reply.isReplyEditable;
      },
      fetchReplies() {
        axios.post(`/faq/replies/${this.idx}`)
            .then(response => {
              this.replies = response.data;
              console.log(this.replies,"답변글 목록")
              this.$forceUpdate(); // 컴포넌트 업데이트 강제 호출
            })
            .catch(error => {
              console.error('답변 내용 불러오기 오류:', error);
            });
      },
      addReply() {
        // 새로운 답변 추가 로직
        const postData = {
          faq_id: this.idx,
          content: this.newReplyContent,
          user_id: this.curr_user_id,
        };
        if(this.newReplyContent === "") {
          alert("내용을 입력해 주세요")
        }
        else {
          axios
              .post("/faq/reply/add", postData)
              .then(() => {
                alert("답변 추가 성공")
                // 새로운 답변이 추가된 후 로직 (예: 다시 답변 목록을 가져오기)
                this.newReplyContent = ""; // 입력 필드 초기화
                this.fetchReplies(); // 답변 목록을 다시 가져오도록 메서드 호출

              })
              .catch((error) => {
                console.error("답변 추가 오류:", error);
              });
        }
      },
      updateReply(reply) {
        // 수정 버튼을 누른 개별 답변 수정 로직
        const data = new URLSearchParams();
        data.append('idx', reply.idx);
        data.append('content', reply.content); // 수정된 데이터를 사용

        const config = {
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
          }
        };
        if(this.content === "") {
          alert("내용을 입력해 주세요")
        }
        else {
          axios.post("/faq/reply/update", data, config)
              .then(() => {
                this.toggleReplyEdit(reply); // 토글 상태 변경
                alert('답변이 수정되었습니다.');
              })
              .catch(error => {
                console.error('답변 수정 오류:', error);
              });
        }
      },
      deleteReply(reply) {
        if(!confirm("정말 삭제하시겠습니까?")) {
          return false;
        }
        // 삭제 버튼을 누른 개별 답변 삭제 로직
        const data = new URLSearchParams();
        data.append('idx', reply.idx);

        const config = {
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
          }
        };
        axios.post("/faq/reply/delete", data, config)
            .then(() => {
              const replyIndex = this.replies.indexOf(reply);
              if (replyIndex > -1) {
                this.replies.splice(replyIndex, 1); // 해당 답변을 배열에서 제거
              }
              alert('답변이 삭제되었습니다.');
            })
            .catch(error => {
              console.error('답변 삭제 오류:', error);
            });
      },
    },
};
</script>

<style scoped>
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.5s;
}
.fade-enter, .fade-leave-to {
  opacity: 0;
}

.writer-container {
  width: 100px;
  height: 30px; /* 원하는 높이로 설정 */
  background-color: #EDE7F6;
  border: 1px solid #7E57C2; /* 테두리 스타일 추가 */
}

.title-container {
  height: 50px; /* 원하는 높이로 설정 */
  width: 100%;
  background-color: #EDE7F6;
  border: 1px solid #7E57C2; /* 테두리 스타일 추가 */

}
.content-container {
  height: 400px; /* 원하는 높이로 설정 */
  border: 1px solid #7E57C2; /* 테두리 스타일 추가 */
  background-color: #EDE7F6;
  overflow-y: auto; /* 내용이 높이를 초과할 경우 스크롤바 추가 */
}

</style>