package com.example.backend.controller.account;

import com.example.backend.domain.Account;
import com.example.backend.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * The type Account controller.
 */
@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/account")
public class accountController {

    private final AccountService accountService;

    /**
     * Insert response entity.
     *
     * @param requestMap the request map
     * @return the response entity
     */
    @PostMapping("/insert")
    public ResponseEntity<String> insert(@RequestBody Map<String, Object> requestMap) {
        String user_id = (String) requestMap.get("user_id");
        String dateStr  = (String) requestMap.get("date");
        String description = (String) requestMap.get("description");
        long amount = Long.parseLong((String) requestMap.get("amount"));
        String category = (String) requestMap.get("category");
        String sort = (String) requestMap.get("sort");


        // 날짜를 java.util.Date 객체로 파싱
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date date = null;
        try {
            date = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // 날짜를 하루 증가
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, 1);

        // 증가된 날짜를 문자열로 변환
        String incrementedDateStr = dateFormat.format(calendar.getTime());

        Account account = new Account();
        account.setUser_id(user_id);
        account.setDate(incrementedDateStr);
        account.setDescription(description);
        account.setAmount(amount);
        account.setSort(sort);
        System.out.println(category +"항목값");
        if(category.equals("수입")) {
            account.setCategory("1");
        } else {
            account.setCategory("0");
        }

        accountService.insertAccount(account);

        return ResponseEntity.ok("account 입력 성공!");
    }

    /**
     * Gets account list.
     *
     * @param user_id the user id
     * @return the account list
     */
    @PostMapping(path = "/list", produces = "application/json")
    public ResponseEntity<List<Account>> getAccountList(@RequestParam String user_id, @RequestParam String selectedYear) {
        Account account = new Account();
        account.setDate(selectedYear);
        account.setUser_id(user_id);
        List<Account> accountList = accountService.getAccountList(account); // AccountService에서 데이터 가져오기
        //System.out.println(accountList+"accountList 객체");
        return ResponseEntity.ok(accountList);
    }

    /**
     * Delete response entity.
     *
     * @param request the request
     * @return the response entity
     */
    @PostMapping("/delete")
    public ResponseEntity<String> delete(@RequestBody Account request) {
        String date = request.getReg_date();
        System.out.println(request+" 삭제 객체");
        System.out.println(request.getReg_date()+"삭제 날짜");
        accountService.deleteAccount(date);
        return ResponseEntity.ok("삭제 성공!");
    }

    /**
     * Send selected items response entity.
     *
     * @param selectedItems the selected items
     * @return the response entity
     */
    @PostMapping("/sendSelectedItems")
    public ResponseEntity<String> sendSelectedItems(@RequestBody List<Account> selectedItems) {
        try {
            System.out.println(selectedItems);
            // 선택된 아이템들의 수입과 지출을 계산하는 로직을 구현
            double totalIncome = 0;
            double totalExpense = 0;

            for (Account item : selectedItems) {
                if ("1".equals(item.getCategory())) { // 수입인 경우
                    totalIncome += item.getAmount();
                } else if ("0".equals(item.getCategory())) { // 지출인 경우
                    totalExpense += item.getAmount();
                }
            }

            // 계산된 결과를 사용하여 정산 로직을 수행하거나 결과를 반환
            // 예를 들어, 수입에서 지출을 뺀 금액을 정산하거나, 계산 결과 메시지를 반환할 수 있습니다.

            double settlementAmount = totalIncome - totalExpense; // 정산 금액 계산
            DecimalFormat currencyFormat = new DecimalFormat("#,###");
            String formattedTotalIncome = currencyFormat.format(totalIncome);
            String formattedTotalExpense = currencyFormat.format(totalExpense);
            String formattedSettlementAmount = currencyFormat.format(settlementAmount);

            String resultMessage = "정산이 완료되었습니다. 총 수입: " + formattedTotalIncome + "원, 총 지출: " + formattedTotalExpense + "원. 정산(수입 - 지출) 금액: " + formattedSettlementAmount + "원";
            return ResponseEntity.ok(resultMessage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("정산 중 오류가 발생하였습니다.");
        }
    }

    @PostMapping("/years")
    public ResponseEntity<List<Integer>> accountYear(@RequestParam String user_id) {
        List<Integer> years = accountService.getAccountYears(user_id);
        //System.out.println(years+"years객체");
        System.out.println(user_id+"user_id의 accountYear");

        return ResponseEntity.ok(years);
    }

}
