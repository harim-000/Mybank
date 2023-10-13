<script>
import axios from "axios";
import store from "@/store";
import router from "@/router";

export default {
  /*  filters: {
      unescape: function(value) {
        const parser = new DOMParser();
        const decodedHTML = parser.parseFromString(value, 'text/html').body.textContent;
        return decodedHTML;
      }
    },*/
  //components: {'ckeditor': CKEditor.component },
  data() {
    return {
      page: 1,
      itemsPerPage: 10,
      totalItems: 0,
      idx: 0,
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
      title: '',
      noticeCont: {}, // 초기에 빈 객체로 설정
    }
  },
  computed: {
    escapedTitle() {
      // noticeCont와 noticeCont.title의 존재 여부를 확인한 후 이스케이핑된 HTML 생성
      if (this.noticeCont && this.noticeCont.title) {
        return this.processHTML(this.noticeCont.title);
      } else {
        return ''; // 데이터가 없을 때는 빈 문자열 반환
      }
    },
  },
  created() {
    this.idx = this.$route.query.idx;
    this.fetchNoticeCont(this.idx);
    this.curr_user_id = store.state.login.member;
    console.log(this.idx,"인덱스")
    console.log(this.noticeCont, "공지글 내용 created")
  },
  methods: {
    processHTML(html) {
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
      await this.fetchNoticeCont(this.idx);
    },
    async fetchNoticeCont() {
      console.log("Fetching data for page:", this.page);

      try {
        const response = await axios.post(`notice/getNotice?idx=${this.idx}`);
        this.noticeCont = response.data;

        // noticeCont.file을 처리하기 전에 특수 문자 처리
        if (this.noticeCont && this.noticeCont.file) {
          this.noticeCont.file = this.processHTML(this.noticeCont.file);
        }
        if (!this.noticeCont || Object.keys(this.noticeCont).length === 0) {
          router.replace('/error');
        }
        console.log(this.noticeCont, "공지글 내용");
        console.log(this.noticeCont.file, "공지글 파일");
        console.log(this.idx,"인덱스")
      } catch (error) {
        console.error('게시글 불러오기 오류:', error);
      }
    },
    truncateContent(content) {
      if (content.length > 5) {
        return content.slice(0, 5) + "...";
      }
      return content;
    },
    async handleScroll(event) {
      const tableView = event.target;
      if (tableView.offsetHeight + tableView.scrollTop >= tableView.scrollHeight) {
        // 스크롤이 끝에 도달했을 때 페이지 변경
        this.page++;
        console.log(this.page+"스크롤 페이지 변함?")
        await this.fetchNoticeCont();
      }
    },
    isImage(file) {
      if (file) {
        // 파일 확장자를 가져옵니다.
        const fileExtension = file.split('.').pop().toLowerCase();
        // 이미지 확장자 목록 (jpg, jpeg, png, gif 등)을 확인하여 이미지인 경우 true 반환
        return ['jpg', 'jpeg', 'png', 'gif'].includes(fileExtension);
      } else {
        return false;
      }
    },
    getFilePath(filename) {
      // 파일 경로를 생성합니다.
      const sanitizedFilename = this.processHTML(filename);
      return `/assets/noticefile/${sanitizedFilename}`;
    },

  }
}
</script>

<template>
    <v-card class="mx-auto" style="width: 90%; max-width: none;" max-height="70%">
      <h5>작성자</h5>
      <div v-text="noticeCont ? noticeCont.user_id : ''" class="writer-container"></div>
      <br>
      <br>
      <h5>제목</h5>
      <div v-html="escapedTitle" class="title-container"></div>
      <br>
      <div v-if="isImage(noticeCont.file)" style="width: 200px; height: 200px;">
        <h5>첨부이미지</h5>
        <img :src="getFilePath(noticeCont.file)" alt="이미지" style="width: 100%; height: 100%;" >
      </div>
      <div v-else-if="noticeCont.file">
        <h5>첨부파일</h5>
        <a :href="getFilePath(noticeCont.file)" download>{{ processHTML(noticeCont.file) }}</a>
      </div>
      <div v-else></div>
      <br>
      <h5>내용</h5>
      <div v-html="noticeCont ? noticeCont.content : ''"  class="content-container"></div>
    </v-card>
</template>

<style>
.writer-container {
  width: 50px;
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
  height: 1000px; /* 원하는 높이로 설정 */
  border: 1px solid #7E57C2; /* 테두리 스타일 추가 */
  background-color: #EDE7F6;
  overflow-y: auto; /* 내용이 높이를 초과할 경우 스크롤바 추가 */
}
</style>