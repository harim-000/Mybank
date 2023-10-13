<script>
import axios from "axios";
import store from "@/store";
import router from "@/router/index";

export default {
  data() {
    return {
      page: 1,
      itemsPerPage: 10,
      totalItems: 0,
      user_id: '',
      totalPages: 0, // 초기에는 0으로 설정
      //searchOptions: ['제목', '내용', '글쓴이'],
      headers: [
        {
          align: 'start',
          key: 'title',
          sortable: false,
          title: '제목',
        },
        { key: 'user_id', title: '글쓴이' },
        { key: 'content', title: '내용' },
        { key: 'reg_date', title: '등록일'},
        { key: 'mod_date', title: '수정일'}
      ],
      faqList: [],
    }
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
  watch: {
    page: {
      handler: 'fetchFaqList',
      immediate: true, // 컴포넌트가 생성될 때도 데이터를 가져오도록 설정
    },
  },
  created() {
    this.fetchFaqList();
    this.user_id = store.state.login.member;
  },
  beforeUpdate() {
    // 페이지가 변경되기 전에 데이터를 업데이트합니다.
    this.fetchFaqList();
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
    async onPageChange(newPage) {
      this.page = newPage;
      console.log(newPage);
      await this.fetchFaqList();
    },
    async fetchFaqList() {
      console.log("Fetching data for page:", this.page);

      try {
        const response = await axios.post(`faq/list?page=${this.page}&itemsPerPage=${this.itemsPerPage}`);
        this.faqList = response.data.faqList;
        console.log(this.faqList, "게시글 목록");
        this.totalItems = response.data.totalItems;
        this.totalPages = response.data.totalPages;
        console.log(this.totalPages, "게시글 수");
        console.log(this.page, "현재 페이지")
      } catch (error) {
        console.error('게시글 불러오기 오류:', error);
      }
    },
    getTitleText(item) {
      if (item.user_id === this.user_id || item.open === "0") {
        return this.processHTML(item.title);
      } else if (item.open === "1") {
        return "비밀글입니다";
      }
      return "";
    },
    getContentText(item) {
      if (item.user_id === this.user_id || item.open === "0") {
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
    goToDetail(item) {
      // 여기에 해당 제목을 클릭했을 때 실행될 페이지 이동 로직을 추가
      // 예를 들어, Vue Router를 사용하는 경우
      router.push({
        path: '/faqCont',
        query: {
          idx: item.idx
        }
      });

    },
    /*filterFaqList() {
      const searchOption = this.searchOption;
      const searchText = this.searchText.toLowerCase();
      this.faqList = this.faqList.filter(item => {
        if (searchOption === '제목') {
          return item.title.toLowerCase().includes(searchText);
        } else if (searchOption === '내용') {
          return item.content.toLowerCase().includes(searchText);
        } else if (searchOption === '글쓴이') {
          return item.user_id.toLowerCase().includes(searchText);
        }
        return false;
      });
    },*/
    async handleScroll(event) {
      const tableView = event.target;
      if (tableView.offsetHeight + tableView.scrollTop >= tableView.scrollHeight) {
        // 스크롤이 끝에 도달했을 때 페이지 변경
        this.page++;
        console.log(this.page+"스크롤 페이지 변함?")
        await this.fetchFaqList();
      }
    }
  }
}
</script>

<template>
  <v-card>
    <v-card-title>
      FAQ
      <v-btn
          color="deep-purple-accent-4"
          variant="plain"
          value="write"
          text="글쓰기"
          to="/write"
      >
      </v-btn>
      <v-spacer></v-spacer>
<!--      <v-select v-model="searchOption" :items="searchOptions" label="검색 조건">
        {{searchOption}}
      </v-select>-->
    </v-card-title>
    <v-data-table
        v-model:pagination="page"
        :headers="headers"
        :items="faqList"
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
        <th style="text-align: center">글쓴이</th>
        <th style="text-align: center">내용</th>
        <th style="text-align: center">등록일</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="(item, index) in faqList" :key="index">
        <td style="text-align: left">
          <v-btn
              variant="text"
              v-if="item.user_id === user_id || item.open === '0'"
              @click="goToDetail(item)"
          >
            {{ truncateContent(getTitleText(item)) }}
          </v-btn>
          <div v-else-if="item.open === '1'">
            <v-icon icon="mdi-lock"></v-icon>
            비밀글입니다
          </div>
        </td>
        <td style="text-align: center">{{ item.user_id }}</td>
        <td style="text-align: left">
          <v-btn
              variant="text"
              v-if="item.user_id === user_id || item.open === '0'"
              @click="goToDetail(item)"
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
  </v-card>
</template>

<style></style>
