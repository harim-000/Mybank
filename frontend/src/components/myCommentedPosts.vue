<template>
  <div>
    <v-card  max-width="3000">
    <v-card-title>
      나의 댓글
      <v-spacer></v-spacer>
    </v-card-title>
      <div v-if="myReplyList === null || myReplyList.length === 0">
        <br>
        <br>
        <br>
        <br>
        <h3 align="center" style="color : #7E57C2">내가 작성한 댓글이 없습니다.</h3>
        <br>
        <br>
        <br>
        <br>
      </div>
      <div v-else>
    <v-data-table
        v-model:pagination="page"
        :items="myReplyList"
        item-key="title"
        class="elevation-1"
        hide-details
        hide-default-footer
        :items-per-page="itemsPerPage"
        @scroll="handleScroll"

    >
      <thead>
      <tr>
        <th style="text-align: center">제목</th>
        <th style="text-align: center">내용</th>
        <th style="text-align: center">등록일</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="(item, index) in myReplyList" :key="index">
        <td style="text-align: left">
          <v-btn
              variant="text"
              v-if="item.user_id === this.curr_user_id || item.open === '0'"
              @click="goToFaqContent(item.idx, item.user_id)"
          >
            {{ truncateContent(getTitleText(item)) }}
          </v-btn>
          <div v-else-if="item.open === '1'">
            <v-icon icon="mdi-lock"></v-icon>
            비밀글입니다
          </div>
        </td>
        <td style="text-align: left">
          <v-btn
              variant="text"
              v-if="item.user_id === this.curr_user_id || item.open === '0'"
              @click="goToFaqContent(item.idx, item.user_id)"
          >
            {{ truncateContent(getContentText(item)) }}
          </v-btn>
          <div v-else-if="item.open === '1'">
            <v-icon icon="mdi-lock"></v-icon>
            비밀글입니다
          </div>
          <div v-if="item.answered === '1'" style="color: #43A047">
            <v-icon icon="mdi-check"></v-icon>
            답변 완료
          </div>
        </td>
        <td style="text-align: center">{{ item.reg_date.substring(0, 10) }}</td>
      </tr>
      </tbody>
      <template v-slot:bottom>
        <div class="text-center pt-2">
          <v-pagination
              v-model="page"
              :length="totalPages"
              @input="onPageChange"
          ></v-pagination>
        </div>
      </template>
    </v-data-table>
      </div>
    </v-card>
  </div>
</template>

<script>
import store from "@/store";
import axios from "axios";

export default {
  data() {
    return {
      page: 1,
      itemsPerPage: 5,
      totalItems: 0,
      curr_user_id: '',
      faqReplyList: [],
      totalPages: 0, // 초기에는 0으로 설정
      myReplyList: []
    };
  },
  watch: {
    page: {
      handler: 'fetchMyReplyList',
      immediate: true, // 컴포넌트가 생성될 때도 데이터를 가져오도록 설정
    },
  },
  computed: {
    // writer에 processHTML 함수 적용
    processedContent() {
      return this.processHTML(this.content);
    },
    // title에 processHTML 함수 적용
    processedTitle() {
      return this.processHTML(this.title);
    },
  },
  beforeUpdate() {
    // 페이지가 변경되기 전에 데이터를 업데이트합니다.
    this.fetchMyQuestionList();
  },
  methods: {
    processHTML(html) {
      if (html === null) {
        return '';
      }
      const escapedHTML = html
          .replace(/&lt;/g, '<')
          .replace(/&gt;/g, '>')
          .replace(/&#40;/g, '(')
          .replace(/&#41;/g, ')')
          .replace(/&#39;/g, "'");
      return escapedHTML;
    },
    async fetchMyReplyList() {
      this.curr_user_id = store.state.login.member;
      try {
        console.log('악시오스 들어옴?')
        const response = await axios.post(`/faq/my-replies?user_id=${this.curr_user_id}&page=${this.page}&itemsPerPage=${this.itemsPerPage}`);
        console.log(response.data)
        this.totalItems = response.data.totalItems;
        this.totalPages = response.data.totalPages;

        this.myReplyList = response.data.faqReplyList; // 서버로부터 받아온 나의 질문글 목록을 설정
        console.log(this.myReplyList)
      } catch (error) {
        console.error('나의 댓글 목록 불러오기 오류:', error);
      }
    },
    goToFaqContent(idx, user_id) {
      // 해당 게시글의 내용 페이지로 이동하는 로직
      this.$router.push({
        path: "/faqCont", // FaqContent 컴포넌트 이름 설정 필요
        query: { idx, user_id }
      });
    },
  getTitleText(item) {
    if (item.user_id === this.curr_user_id || item.open === "0") {
      return this.processHTML(item.title);
    } else if (item.open === "1") {
      return "비밀글입니다";
    }
    return "";
  },
  getContentText(item) {
    if (item.user_id === this.curr_user_id || item.open === "0") {
      return this.processHTML(item.content);
    } else if (item.open === "1") {
      return "비밀글입니다";
    }
    return "";
  },
  truncateContent(content) {
    if (content.length > 15) {
      return content.slice(0, 15) + "...";
    }
    return content;
  },
  async onPageChange(newPage) {
    this.page = newPage;
    console.log(newPage);
    await this.fetchMyReplyList();
  },
  async handleScroll(event) {
    const tableView = event.target;
    if (tableView.offsetHeight + tableView.scrollTop >= tableView.scrollHeight) {
      // 스크롤이 끝에 도달했을 때 페이지 변경
      this.page++;
      console.log(this.page+"스크롤 페이지 변함?")
      await this.fetchMyReplyList();
    }
  }
 },
  created() {
    this.fetchMyReplyList(); // 컴포넌트 생성 시에 나의 질문글 목록을 불러오도록 호출
  }
};
</script>

<style scoped>

</style>
