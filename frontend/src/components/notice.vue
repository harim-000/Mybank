<script>
import axios from "axios";
import router from "@/router/index";

export default {
  filters: {
    unescape: function(value) {
      const parser = new DOMParser();
      const decodedHTML = parser.parseFromString(value, 'text/html').body.textContent;
      return decodedHTML;
    }
  },
  data() {
    return {
      page: 1,
      itemsPerPage: 10,
      item: [],
      totalItems: 0,
      // dataProcessor 설정을 사용하여 컨텐츠 포맷 변경
      dataProcessor: {
        htmlToData(viewFragment) {
          // HTML을 데이터로 변환하는 로직
          // 예: <p> 태그를 사용하지 않고 데이터 처리
          const data = [];
          viewFragment.forEach(item => {
            data.push(item.data);
          });
          return data;
        },
        dataToHtml(data) {
          // 데이터를 HTML로 변환하는 로직
          // 예: <p> 태그를 사용하여 HTML 생성
          const viewFragment = new this.editor.view.DocumentFragment();
          data.forEach(item => {
            viewFragment.append(this.editor.data.processor.toView(item));
          });
          return viewFragment;
        },
      },
      content: '',
      user_id: '',
      totalPages: 0, // 초기에는 0으로 설정
      //searchOptions: ['제목', '내용', '글쓴이'],
      headers: [
        {
          key: 'title',
          sortable: false,
          title: '제목',
        },
        { key: 'writer', title: '글쓴이' },
        { key: 'content', title: '내용' },
        { key: 'reg_date', title: '등록일'},
        { key: 'mod_date', title: '수정일'},
      ],
      noticeList: [ ],
    }
  },

  computed: {
    // 타입이 "Y"인 항목과 나머지 항목을 분리하여 계산된 속성을 만듭니다.
    separatedNoticeList() {
      const importantItems = this.noticeList.filter(item => item.type === 'Y');
      const otherItems = this.noticeList.filter(item => item.type !== 'Y');
      return [...importantItems, ...otherItems];
    },
  },
  watch: {
    page: {
      handler: 'fetchNoticeList',
      immediate: true,
    },
  },
  created() {
    this.fetchNoticeList();
     console.log(this.noticeList);

  },

  mounted() {

    //alert(this.noticeList);
    this.fetchNoticeList(); // 데이터 로드

    console.log(this.noticeList);

  } ,
  methods: {
    processedContent(content) {
      // HTML 문자열을 파싱합니다.
      const parser = new DOMParser();
      const doc = parser.parseFromString(content, 'text/html');

      // 파싱된 결과를 순회하면서 태그를 구별하여 처리합니다.
      const nodes = doc.querySelectorAll('*');
      nodes.forEach(node => {
        // CKEditor에서 생성한 태그인지 확인합니다.
        if (node.outerHTML === node.innerHTML) {
          // CKEditor에서 생성한 태그는 그대로 표시합니다.
          return;
        }
        // 직접 삽입한 태그는 이스케이핑하여 출력합니다.
        node.innerHTML = node.textContent;
      });

      // 파싱된 결과를 문자열로 반환합니다.
      return new XMLSerializer().serializeToString(doc);
    },
    async onPageChange(newPage) {
      this.page = newPage;
      console.log(newPage);
      await this.fetchNoticeList();
    },
    async fetchNoticeList() {
      console.log("Fetching data for page:", this.page);

      try {
        const response = await axios.post(`notice/list?page=${this.page}&itemsPerPage=${this.itemsPerPage}`);
        this.noticeList = response.data.noticeList;

        console.log(this.noticeList, "게시글 목록");
        this.totalItems = response.data.totalItems;
        this.totalPages = response.data.totalPages;
        console.log(this.totalPages, "게시글 수");
        console.log(this.page, "현재 페이지")
      } catch (error) {
        console.error('게시글 불러오기 오류:', error);
      }
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
        path: '/noticeCont',
        query: {
          idx: item.idx
        }
      });

    },
    async handleScroll(event) {
      const tableView = event.target;
      if (tableView.offsetHeight + tableView.scrollTop >= tableView.scrollHeight) {
        // 스크롤이 끝에 도달했을 때 페이지 변경
        this.page++;
        console.log(this.page + "스크롤 페이지 변함?");
        await this.fetchNoticeList(); // 스크롤이 끝에 도달하면 새로운 페이지 데이터를 가져옴
      }
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

  },

}
</script>

<template>
  <v-card>
    <v-card-title>
      Notice
      <v-spacer></v-spacer>
      <!--      <v-select v-model="searchOption" :items="searchOptions" label="검색 조건">
              {{searchOption}}
            </v-select>-->
    </v-card-title>
    <v-data-table
        v-model:pagination="page"
        :headers="headers"
        :items="separatedNoticeList"
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
        <th style="text-align: center">등록일</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="(item, index) in separatedNoticeList" :key="index"  align="left">
        <td :style="{ backgroundColor: item.type === 'Y' ? '#CE93D8' : 'white' }">
          <v-btn @click="goToDetail(item)" variant="text">
            {{ processHTML(item.title) }}
          </v-btn>
        </td>
        <td :style="{ backgroundColor: item.type === 'Y' ? '#CE93D8' : 'white' }"  align="center">{{ item.user_id }}</td>
        <td :style="{ backgroundColor: item.type === 'Y' ? '#CE93D8' : 'white' }"  align="center">{{ item.reg_date.substring(0, 10) }}</td>
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

<style>
</style>
