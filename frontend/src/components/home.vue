<script>
import axios from "axios";
import store from "@/store";

export default {
  data: function() {
    return {
      userInfo: {
        handler: true
      },
      user_id: '',
      years: [], // 데이터의 년도 배열
      search: '',
      resultMessage: "",
      select: [],
      chart: null,
      headers: [
        { title: '날짜', key: 'date'},
        { title: '내역', key: 'description' },
        { title: '금액', key: 'amount' },
        { title: `구분`, key: 'sort' },
        { title: '항목', key: 'category' },
        { title: '선택', key: 'isSelected' },
        { title: '삭제', key: 'deleteItem' },
      ],
      items: [

      ], //소득/지출 내역을 담을 배열
      newItem: { description: '', sort: '', date: '', category: '수입', amount: 0, isSelected: false,}, // 새로운 내역을 입력하는 데이터
      chartOptions: {
        chart: {
          id: "vuechart-example",
          type: 'bar',
          height: 350,
          stacked: true,
          toolbar: {
            show: true,
          },
          zoom: {
            enabled: true
          }
        },
        xaxis: {
          type: 'category',
          categories: null,
          tickPlacement: 'on',
          labels: {
            formatter: function(val) {
              return val ? val.substring(0, 7) : ''; // "YYYY-MM" 형식의 날짜에서 "YYYY-MM" 부분만 표시
            }
          }
        },
        yaxis: {
          labels: {
            formatter: function (value) {
              if (value >= 10000) {
                const manWon = Math.floor(value / 10000);
                const rest = value % 10000;
                const cheonWon = Math.floor(rest / 1000);
                const won = rest % 1000;
                let result = manWon + '만';
                if (cheonWon > 0) {
                  result += ' ' + cheonWon + '천';
                }
                if (won > 0) {
                  result += ' ' + won + '원';
                }
                return result;
              } else {
                return (value) + '원'; // 만원 미만은 그대로 원 단위로 표시
              }
            }
          },
        },
        legend: {
          position: 'right',
          offsetY: 40
        },
        fill: {
          opacity: 1
        },
        dataLabels: {
          formatter: function (value) {
            if (value >= 10000) {
              const manWon = Math.floor(value / 10000);
              const rest = value % 10000;
              const cheonWon = Math.floor(rest / 1000);
              const won = rest % 1000;
              let result = manWon + '만';
              if (cheonWon > 0) {
                result += ' ' + cheonWon + '천';
              }
              if (won > 0) {
                result += ' ' + won + '원';
              }
              return result;
            } else {
              return (value) + '원'; // 만원 미만은 그대로 원 단위로 표시
            }
          }
        },
        plotOptions: {
          bar: {
            horizontal: false,
            dataLabels: {
              total: {
                enabled: false,
                style: {
                  fontSize: '13px',
                  fontWeight: 900
                },
              }
            }
          },
        },
      },

      series: [],
      originalSeries: [], // 원본 데이터 시리즈
      selectedYear: '',
      colorMapping: {
        "월급": "#fa8500",
        "저축": "#1f7902", // Add color for "저축"
        "기타": "#6a0afa", // Add color for "기타수입"
        "공과금": "#D50000",
        "식비": "#05f3da", // Add color for "식비"
        "주거비": "#e81aef", // Add color for "주거비"
        "교통비": "#EEFF41", // Add color for "교통비"
        "통신비": "#1289e8", // Add color for "통신비"
        "자유항목": "#40ff00", // Add color for "자유항목"
        // 다른 그룹에 대한 색상도 여기에 추가
      },

    };
  },
  computed: {
    filteredItems() {
      if (!this.search) {
        return this.items;
      }

      // 디버깅: 어떤 값들이 있는지 확인
      console.log(this.items);

      return this.items.filter(item =>
          item.date.substring(0, 10).includes(this.search) ||
          item.description.toLowerCase().includes(this.search.toLowerCase()) ||
          item.amount.toString().includes(this.search) ||
          (item.category === "0" && "지출".includes(this.search)) ||
          (item.category === "1" && "수입".includes(this.search))
      );

    },
    incomeSortOptions() {
      // 수입 구분 항목 배열 반환
      return ['월급', '저축', '기타'];
    },
    expenseSortOptions() {
      // 지출 구분 항목 배열 반환
      return ['공과금', '식비', '주거비', '교통비', '통신비', '자유항목'];
    },
    newItemSortOptions() {
      if (this.newItem.category === '수입') {
        return this.incomeSortOptions;
      } else if (this.newItem.category === '지출') {
        return this.expenseSortOptions;
      } else {
        return [];
      }
    },
  },
  watch: {
    series: {
      handler(newSeries) {
        if (this.$refs.chart) { // Check if chart is available
          this.$nextTick(() => {
            this.$refs.chart.updateSeries(newSeries);
          });
        }
      },
      deep: true,
    },
    filteredItems: {
      handler(newFilteredItems) {
        newFilteredItems.forEach(item => {
          console.log(item.isSelected, "선택된 값");
        });
        console.log("filteredItems가 변경되었습니다.", newFilteredItems);
        // 여기에 원하는 로직을 추가하면 됩니다.
      },
      deep: true, // filteredItems 배열 내부 객체의 변화까지 감지
    },
    selectedYear(newYear, oldYear) {
      // selectedYear가 변경될 때 실행할 로직을 여기에 작성
      console.log('selectedYear가 변경되었습니다. 새로운 값:', newYear, '이전 값:', oldYear);
      this.loadDataByYear(newYear);
      // 변경된 selectedYear에 대한 다른 로직을 수행할 수 있습니다.
    },
    'newItem.category': function(newCategory) {
      // Reset the sort when the category changes
      this.newItem.sort = '';

      // Set the default sort option based on the category
      if (newCategory === '수입') {
        this.newItem.sort = '월급'; // Set to the default value '월급' from incomeSortOptions
      } else if (newCategory === '지출') {
        this.newItem.sort = '공과금'; // Set to the default value '공과금' from expenseSortOptions
      }
    },
    deep: true, // Watch for changes inside newItem
    'newItem.amount': function (newAmount, oldAmount) {
      if (newAmount > 1000000000000) {
        // 입력된 값이 1000 미만이면 이전 값으로 복원
        this.newItem.amount = oldAmount;
        alert('최대 금액을 입력하셨습니다.');
      }
    }
  },

  async created() {
    try {
      this.user_id = store.state.login.member; // user_id를 가져옴
      const response = await axios.post(`/user/getUserInfo?user_id=${this.user_id}`);
      this.userInfo = response.data; // 가져온 사용자 정보를 저장
      console.log(this.userInfo.user_id);
      console.log(this.userInfo.user_profile, "홈 프로필");
      console.log(this.user_id, "홈 아이디");
    } catch (error) {
      console.error("Error fetching user info:", error);
    }
  },
  async mounted() {
    this.user_id = store.state.login.member;
    await this.fetchYears(); // fetchYears 메서드 호출
    console.log("mounted 실행됨");
    // fetchYears 메서드가 완료되고 select 배열에 데이터가 채워진 후, select 배열의 첫 번째 값을 선택된 연도로 설정
    if (this.select.length > 0) {
      this.selectedYear = this.select[0];
      await this.loadDataByYear(this.selectedYear);
      await this.$refs.chart.updateSeries(this.series);
      console.log("mounted selectedYear", this.selectedYear);
      this.updateProfileImage();
      await this.fetchUserInfo();
    }

  },

  methods: {
    async fetchYears() {
      try {
        const response = await axios.post(`/account/years?user_id=${this.user_id}`);
        if (response.status === 200) {
          const newYears = response.data.filter(year => !this.select.includes(year));
          this.select.push(...newYears);
          console.log(this.select,"data 결과")
        } else {
          console.error('Error fetching years:', response.statusText);
        }
      } catch (error) {
        console.error('Error fetching years:', error);
      }
    },
    setColors(group) {
      return this.colorMapping[group] || "#0000FF"; // 그룹에 해당하는 색상이 없을 경우 기본 색상
    },

    async loadDataByYear(selectedYear) {
      try {
        // account 정보 받아오기
        console.log(selectedYear, "selectedYear 값");
        const response = await axios.post(`/account/list?user_id=${this.user_id}&selectedYear=${selectedYear}`);
        this.originalSeries = response.data.series;
        this.years = response.data.years;
        console.log(response.data);
        this.items = response.data; // 받아온 데이터를 items에 할당하여 표시
        console.log(this.items, "아이템 배열");
        console.log(this.items.length, "아이템 배열 길이");


        const uniqueGroupKeys = new Set();
        const combinedData = {};

        // 데이터를 월별로 그룹화하여 계산
        const groupedData = {};
        this.items.forEach((item) => {
          const date = new Date(item.date);
          const monthYear = `${date.getFullYear()}-${(date.getMonth() + 1)
              .toString()
              .padStart(2, "0")}`;
          const category = item.category === "1" ? "수입" : "지출";
          const group =
              category === "수입"
                  ? this.getIncomeGroup(item.sort) // 수입 그룹
                  : this.getExpenseGroup(item.sort); // 지출 그룹

          if (!groupedData[monthYear]) {
            groupedData[monthYear] = {
              수입: {},
              지출: {},
            };
          }

          if (!groupedData[monthYear][category][group]) {
            groupedData[monthYear][category][group] = 0;
          }

          groupedData[monthYear][category][group] += item.amount;
          if (!combinedData[group]) {
            combinedData[group] = {};
          }

          if (!combinedData[group][category]) {
            combinedData[group][category] = 0;
          }

          combinedData[group][category] += item.amount;

          uniqueGroupKeys.add(item.sort);

        });
        console.log(uniqueGroupKeys, 'uniqueGroupKeys')


        // 차트 데이터 형식으로 변환
        const series = [];
        const categories = Object.keys(groupedData);
        const uniqueGroupCategoryNames = {}; // 그룹 및 카테고리의 고유한 조합 이름을 저장합니다.

        for (const item of this.items) {
          const date = new Date(item.date);
          const monthYear = `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, "0")}`;
          const category = item.category === "1" ? "수입" : "지출";
          let group = category === "수입" ? this.getIncomeGroup(item.sort) : this.getExpenseGroup(item.sort);

          // 그룹 및 카테고리의 조합에 대한 고유한 이름 생성
          const name = `${group} (${category})`;

          if (!uniqueGroupCategoryNames[name]) {
            uniqueGroupCategoryNames[name] = true;

            // 모든 카테고리에 대한 값을 0으로 초기화하는 데이터 배열 생성
            const data = categories.map(() => {
              return 0;
            });

            // 현재 monthYear에 해당하는 카테고리 인덱스를 찾습니다.
            const categoryIndex = categories.indexOf(monthYear);

            if (categoryIndex !== -1) {
              // 카테고리 인덱스의 위치에 item의 금액을 설정합니다.
              data[categoryIndex] = item.amount;
            }

            series.push({
              name,
              group: category,
              data,
              color: this.setColors(group), // 설정한 색상을 사용합니다.
            });
          } else {
            // 기존 그룹 및 카테고리에 대한 데이터를 업데이트합니다.
            const existingSeries = series.find((s) => s.name === name);

            if (existingSeries) {
              const categoryIndex = categories.indexOf(monthYear);
              if (categoryIndex !== -1) {
                existingSeries.data[categoryIndex] += item.amount;
              }
            }
          }
        }

        // 차트 설정 수정
        this.series = series;
        this.chartOptions.xaxis.categories = categories;

        // 선택된 연도 업데이트
        this.selectedYear = selectedYear;

        // 차트 업데이트
        this.$refs.chart.updateOptions({
          xaxis: {
            categories: categories,
          },
        });

        this.$refs.chart.updateSeries(this.series);

        console.log("Categories:", categories);
        console.log("Series:", this.series);
        console.log(this.series, "series 차트값");
        console.log(this.chartOptions.xaxis.categories, "카테고리값");
      } catch (error) {
        console.error('데이터 가져오기 실패:', error);
      }
    },
    getIncomeGroup(sort) {
      // 수입 그룹을 결정하는 로직을 구현
      if (sort === "월급") return "월급";
      else if (sort === "저축") return "저축";
      else return "기타";
    },
    getExpenseGroup(sort) {
      // 지출 그룹을 결정하는 로직을 구현
      if (sort === "공과금") return "공과금";
      else if (sort === "식비") return "식비";
      else if (sort === "주거비") return "주거비";
      else if (sort === "교통비") return "교통비";
      else if (sort === "통신비") return "통신비";
      else return "자유항목"
    },
    // 추가: 내역을 추가하는 메서드
    async addNewItem() {
      try {
        if (this.user_id === null) {
          alert("로그인 후 이용해주세요")
          console.error('금액을 입력해주세요.');
          return;
        }
        if (!this.newItem.description) {
          alert("내역을 입력해주세요.")
          console.error('내역을 입력해주세요.');
          return;
        }
        if (!this.newItem.date) {
          alert("날짜를 입력해주세요.")
          console.error('날짜를 입력해주세요.');
          return;
        }
        if (this.newItem.amount === 0) {
          alert('금액을 입력해주세요.')
          console.error('금액을 입력해주세요.');
          return;
        }
        if (this.newItem.sort === '') {
          alert(`${this.newItem.category} 구분을 선택 해주세요.`)
          console.error('금액을 입력해주세요.');
          return;
        }

        this.user_id = store.state.login.member;

        // 데이터 생성
        const newItemData = {
          date: this.newItem.date,
          description: this.newItem.description,
          amount: this.newItem.amount,
          user_id: this.user_id,
          category: this.newItem.category,
          sort: this.newItem.sort,
        };
        console.log(newItemData, "가계부 입력 객체");

        // 백엔드에 데이터 전송
        const response = await axios.post('/account/insert', newItemData);

        // 응답 처리
        if (response.status === 200) {
          console.log('데이터가 성공적으로 전송되었습니다.');
          // 전송 성공 후 items 배열에 새로운 내역 추가
          this.items.push(newItemData);
          // 차트 업데이트
          this.series.push(newItemData);
          await this.loadDataByYear(this.selectedYear);
          this.$refs.chart.updateSeries(this.series);
          // 전송 성공 후 초기화
          this.newItem.description = ''; // 텍스트 필드 값만 초기화
          this.newItem.amount = 0; // 금액 필드 값 초기화
          this.newItem.category = '';
          this.newItem.sort = '';
          this.newItem.date = '';
        } else {
          console.error('데이터 전송에 실패하였습니다.');
        }
      } catch (error) {
        console.error('데이터 전송 중 에러가 발생하였습니다:', error);
      }
    },
    getCategoryText(category) {
      return category === "1" ? '수입' : '지출';
    },
    hasNullData(series) {
      if (!series || !Array.isArray(series)) {
        return false;
      }

      for (const data of series) {
        if (data.data.some(value => value === null)) {
          return true;
        }
      }

      return false;
    },
    formatAmount(amount) {
      // Use toLocaleString to format the amount with thousands separators
      var n = String(amount).replace(/,/g, '');
      var r = (n.length % 3);
      var rtn = n.substring(0, r) + n.substring(r).replace(/(\d{3})/g, ',$1');

      amount = rtn.replace(/^,/, '');

      return amount;
    },
    async sendSelectedItems() {
      console.log('selectedItems 함수 작동')
      try {
        const selectedItems = this.items.filter(item => item.isSelected);
        console.log(selectedItems, "정산 객체 확인")

        // 서버로 선택된 아이템들을 전송
        const response = await axios.post('/account/sendSelectedItems', selectedItems);

        if (response.status === 200) {
          console.log('선택된 아이템들이 성공적으로 전송되었습니다.');

          // 선택된 아이템들의 isSelected 값을 초기화
          selectedItems.forEach(item => {
            item.isSelected = false;
          });

          // 데이터를 다시 로드하여 변경 사항 반영
          await this.loadDataByYear(this.selectedYear);
          // 결과 메시지를 변수에 저장
          this.resultMessage = response.data;

        } else {
          console.error('아이템들 전송에 실패하였습니다.');
        }
      } catch (error) {
        console.error('아이템들 전송 중 에러가 발생하였습니다:', error);
      }
    },
    async deleteItem(item) {
      if(!confirm("정말 삭제하시겠습니까?")) {
        return false;
      }
      try {
        const response = await axios.post('/account/delete', item);
        console.log(item,"itemdelete")
        if (response.status === 200) {
          console.log('아이템이 성공적으로 삭제되었습니다.');
          // Remove the item from the local items array
          this.items = this.items.filter(existingItem => existingItem.date !== item.date);
          // 데이터를 다시 로드하여 변경 사항 반영

          await this.loadDataByYear(this.selectedYear);
          this.series = [...this.series, this.items];
          this.$refs.chart.updateSeries(this.series);

        } else {
          console.error('아이템 삭제에 실패하였습니다.');
        }
      } catch (error) {
        console.error('아이템 삭제 중 에러가 발생하였습니다:', error);
      }
    },
    toggleSelectAll() {
      this.filteredItems.forEach((item) => (item.isSelected = !this.selectAll));
      // selectAll 값을 토글합니다.
      this.selectAll = !this.selectAll;
    },
  },
};

</script>


<template>
  <div id="chart">

    <div class="chart-container">
      <div v-if="!filteredItems.length || hasNullData(series) || user_id === null">
        <h3 align="center">Example Chart</h3>
        <img :src="require('@/assets/example_chart.png')" alt="Default Image" style="width: 1800px; height: 500px;"/>
      </div>
      <div v-else>
        <v-select
            v-model="selectedYear"
            label="가계부 연도 선택"
            :items="select"
            @change="loadDataByYear(selectedYear)"
            :key="selectedYear"
        ></v-select>
        <h3 align="center" style="color:#5E35B1;">
          <v-avatar  style="width: 50px; height: 50px;">
            <img
                v-if="userInfo.user_profile"
                :src="`/assets/profile/${this.userInfo.user_profile}`"
                alt="Profile Image"
                style="width: 50px; height: 50px;"
            />
            <img
                v-else
                :src="require(`@/assets/default_profile.png`)"
                alt="Default Profile Image"
                style="width: 50px; height: 50px;"
            />
          </v-avatar>
          {{ userInfo.user_id }} 님의 연간 가계부 수입/지출 현황</h3>
        <apexchart
            key="series"
            width="1600"
            height="600"
            type="bar"
            :options="chartOptions"
            :series="series"
            ref="chart"
        ></apexchart>
      </div>
    </div>
    <div class="grid-container">
      <v-form class="form-container"  @submit.prevent="addNewItem">
        <h2 align="center" style="color:#5E35B1;">가계부 작성하기</h2>
        <br>
        <v-container class="mt-sm-16">
          <v-row justify="center">
            <v-date-picker
                class="mt-sm-n16"
                v-model="newItem.date"
                locale="ko"
                scrollable
                width="10"
                color="#B39DDB"
            ></v-date-picker>
          </v-row>
          <br>
        </v-container>
        <v-spacer></v-spacer>
        <v-select
            v-model="newItem.category"
            label="항목"
            :items="['수입', '지출']"
        ></v-select>
        <v-select
            v-model="newItem.sort"
            label="구분"
            :items="newItemSortOptions"
        ></v-select>
        <v-text-field v-model="newItem.description" :maxlength="100" counter="100" placeholder="내역" />
        <v-text-field v-model="newItem.amount" type="number" min="0" step="1000" placeholder="금액" />
        <v-btn @click="addNewItem">추가</v-btn>
      </v-form>
      <v-card>
        <div v-if="filteredItems.length">
        <v-text-field
            v-model="search"
            append-icon="mdi-magnify"
            label="가계부 내역 검색하기"
            :maxlength="50" counter="50"
            single-line
            hide-details
        ></v-text-field>
        <v-data-table
            :headers="headers"
            :items="filteredItems"
            class="elevation-1"
            height="400"
            :item-key="item => item.user_id"
            ref="datatable"
            outlined
        >
          <tr>
            <th v-for="header in headers" :key="header.key">{{ header.title }}</th>
          </tr>

          <tr v-for="item in filteredItems" :key="item.user_id">
            <td align="center">{{ item.date.substring(0, 10) }}</td>
            <td align="left">{{ item.description }}</td>
            <td align="right">{{ formatAmount(item.amount) }}</td>
            <td align="center">{{ formatAmount(item.sort) }}</td>
            <td align="center">{{ getCategoryText(item.category) }}</td>
            <td class="text-center">
              <v-checkbox class="checkbox-center"
                          v-model="item.isSelected"
              ></v-checkbox>
            </td>
            <td class="text-center" align="center">
              <v-icon
                  size="small"
                  @click="deleteItem(item)"
              >
                mdi-delete
              </v-icon>
            </td>
          </tr>
          <template #bottom>
            <br>
            <td colspan="5" class="bottom-buttons">
              <div v-if="resultMessage" class="result-message">
                {{ resultMessage }}
              </div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
              <br>
              <v-btn
                  color="deep-purple-lighten-1"
                  @click="sendSelectedItems"
              >정산</v-btn>
              &nbsp;&nbsp;&nbsp;&nbsp;
              <v-btn
                  color="deep-purple-lighten-1"
                  @click="toggleSelectAll"
              >전체 선택</v-btn>
            </td>
            <br>
          </template>
        </v-data-table>
        </div>
      </v-card>
    </div>
  </div>

</template>

<style scoped>
a {
  text-decoration: none;
}


.chart-container {
  flex: 1;
}

.grid-container {
  flex: 0.5;
  padding: 20px;
  border: 1px solid #ccc;
}
.form-container {
  padding: 20px;
  border: 1px solid #ccc;
  margin-top: 20px;
}
.bottom-buttons {
  justify-items: right;
  justify-content: flex-end;
  align-items: end;
  align-content: flex-end;
  display: flex;
  margin-top: 0;
  padding-top: 0;
  margin-bottom: -16px
}
.text-center {
  text-align: center;
  align-items: flex-end;
}

.checkbox-center {
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
