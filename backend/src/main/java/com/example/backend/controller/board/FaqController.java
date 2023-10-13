package com.example.backend.controller.board;

import com.example.backend.domain.Faq;
import com.example.backend.domain.Faq_reply;
import com.example.backend.domain.MyQuestionRequest;
import com.example.backend.service.FaqService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Faq controller.
 */
@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/faq")
public class FaqController {
    private final FaqService faqService;

    /**
     * Write faq response entity.
     *
     * @param faq the faq
     * @return the response entity
     */
    @PostMapping("/write")
    public ResponseEntity<String> writeFaq(@RequestBody Faq faq) {

        faq.setReg_date(String.valueOf(LocalDateTime.now())); // 현재 시간으로 설정

        faqService.insertFaq(faq);

        return ResponseEntity.ok("게시글 추가 성공");
    }

    /**
     * Gets faq list.
     *
     * @param page         the page
     * @param itemsPerPage the items per page
     * @return the faq list
     */
    @PostMapping(path = "/list", produces = "application/json")
    public ResponseEntity<Map<String, Object>> getFaqList(@RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = "10") int itemsPerPage) {
        // 전체 게시글 수 가져오기
        int totalItems = faqService.getTotalFaqCount();

        if (totalItems == 0) {
            // Handle the case where there are no items to retrieve
            Map<String, Object> response = new HashMap<>();
            response.put("totalItems", 0);
            response.put("totalPages", 0);
            response.put("currentPage", page);
            response.put("faqList", Collections.emptyList()); // Return an empty list
            return ResponseEntity.ok(response);
        }

        // 전체 게시글을 한 페이지에 모두 출력하는 페이징 로직
        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);

        // 페이지 번호가 유효한 범위 내로 조정
        if (page < 1) {
            page = 1;
        } else if (page > totalPages) {
            page = totalPages;
        }

        // 시작 번호와 끝 번호 계산
        int startNo = (page - 1) * itemsPerPage + 1;
        int endNo = startNo + itemsPerPage - 1;

        List<Faq> faqList = faqService.getFaqList(startNo, endNo); // 데이터 가져오기
        System.out.println(faqList);
        Map<String, Object> response = new HashMap<>();
        response.put("totalItems", totalItems);
        response.put("totalPages", totalPages);
        response.put("currentPage", page);
        response.put("faqList", faqList);
        System.out.println(response);

        return ResponseEntity.ok(response);
    }

    /**
     * Gets my faq list.
     *
     * @param user_id the user id
     * @return the faq list
     */
    @PostMapping("/my-questions")
    public ResponseEntity<Map<String, Object>> getMyFaqList(@RequestParam(defaultValue = "1") int page,
                                                  @RequestParam String user_id,
                                                  @RequestParam(defaultValue = "5") int itemsPerPage) {
        // 전체 게시글 수 가져오기
        int totalItems = faqService.getTotalMyFaqCount(user_id);


        if (totalItems == 0) {
            // Handle the case where there are no items to retrieve
            Map<String, Object> response = new HashMap<>();
            response.put("totalItems", 0);
            response.put("totalPages", 0);
            response.put("currentPage", page);
            response.put("faqList", Collections.emptyList()); // Return an empty list
            return ResponseEntity.ok(response);
        }

        // 전체 게시글을 한 페이지에 모두 출력하는 페이징 로직
        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);

        // 페이지 번호가 유효한 범위 내로 조정
        if (page < 1) {
            page = 1;
        } else if (page > totalPages) {
            page = totalPages;
        }

        // 시작 번호와 끝 번호 계산
        int offset = (page - 1) * itemsPerPage;
        int limit = itemsPerPage;
        System.out.println(totalPages+"totalPages");

        MyQuestionRequest myQuestionRequest = new MyQuestionRequest();
        myQuestionRequest.setOffset(offset);
        myQuestionRequest.setLimit(limit);
        myQuestionRequest.setUser_id(user_id);
        List<Faq> faqList = faqService.getMyFaqList(myQuestionRequest); // 데이터 가져오기
        System.out.println(faqList+"내 질문글");
        Map<String, Object> response = new HashMap<>();
        response.put("totalItems", totalItems);
        response.put("totalPages", totalPages);
        response.put("currentPage", page);
        response.put("faqList", faqList);
        System.out.println(response);
        return ResponseEntity.ok(response);
    }

    /**
     * Gets my reply list.
     *
     * @param user_id the user id
     * @return the reply list
     */
    @PostMapping("/my-replies")
    public ResponseEntity<Map<String, Object>> getMyReplyList(@RequestParam(defaultValue = "1") int page,
                                                    @RequestParam String user_id,
                                                    @RequestParam(defaultValue = "5") int itemsPerPage) {

        // 전체 게시글 수 가져오기
        int totalItems = faqService.getTotalMyFaqReplyCount(user_id);

        if (totalItems == 0) {
            // Handle the case where there are no items to retrieve
            Map<String, Object> response = new HashMap<>();
            response.put("totalItems", 0);
            response.put("totalPages", 0);
            response.put("currentPage", page);
            response.put("faqList", Collections.emptyList()); // Return an empty list
            return ResponseEntity.ok(response);
        }

        // 전체 게시글을 한 페이지에 모두 출력하는 페이징 로직
        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);

        // 페이지 번호가 유효한 범위 내로 조정
        if (page < 1) {
            page = 1;
        } else if (page > totalPages) {
            page = totalPages;
        }

        // 시작 번호와 끝 번호 계산
        int offset = (page - 1) * itemsPerPage;
        int limit = itemsPerPage;
        System.out.println(totalPages+"totalPages");
        MyQuestionRequest myQuestionRequest = new MyQuestionRequest();
        myQuestionRequest.setOffset(offset);
        myQuestionRequest.setLimit(limit);
        myQuestionRequest.setUser_id(user_id);

        List<Faq> faqReplyList = faqService.getMyReplyList(myQuestionRequest); // 데이터 가져오기
        System.out.println(faqReplyList+"내 댓글");

        Map<String, Object> response = new HashMap<>();
        response.put("totalItems", totalItems);
        response.put("totalPages", totalPages);
        response.put("currentPage", page);
        response.put("faqReplyList", faqReplyList);
        System.out.println(response);
        return ResponseEntity.ok(response);
    }

    /**
     * Gets faq.
     *
     * @param idx the idx
     * @return the faq
     */
    @PostMapping("/content/{idx}")
    public Faq getFaq(@PathVariable int idx) {
        System.out.println(idx+"인덱스");
        System.out.println(faqService.getFaq(idx)+"질문게시글 상세페이제 내용");
        return faqService.getFaq(idx);
    }

    /**
     * Update faq int.
     *
     * @param idx     the idx
     * @param content the content
     * @param title   the title
     * @param open    the open
     * @return the int
     */
    @PostMapping("/update")
    public int updateFaq(@RequestParam Integer idx, @RequestParam String content, @RequestParam String title, @RequestParam String open){
        System.out.println(idx+"인덱스?");
        Faq faq = faqService.getFaq(idx);
        faq.setMod_date(LocalDateTime.now().toString());
        faq.setContent(content);
        faq.setOpen(open);
        faq.setTitle(title);
        int updatedFaq = faqService.updateFaq(faq);
        System.out.println(updatedFaq+"수정됨?");
        return updatedFaq;
    }

    /**
     * Delete faq.
     *
     * @param idx the idx
     */
    @PostMapping("/delete")
    public void deleteFaq(@RequestParam int idx) {
        System.out.println(idx+"삭제 인덱스?");

        faqService.deleteFaq(idx);
        faqService.deleteFaqReply(idx);

    }

    /**
     * Write reply response entity.
     *
     * @param faqReply the faq reply
     * @return the response entity
     */
    @PostMapping("/reply/add")
    public ResponseEntity<String> writeReply(@RequestBody Faq_reply faqReply) {
        faqReply.setReg_date(String.valueOf(LocalDateTime.now()));
        System.out.println(faqReply+"답변글 내용");
        faqService.insertFaqReply(faqReply);
        return ResponseEntity.ok("댓글 추가 성공");
    }

    /**
     * Gets replies.
     *
     * @param idx the idx
     * @return the replies
     */
    @PostMapping("/replies/{idx}")
    public ResponseEntity<List<Faq_reply>> getReplies(@PathVariable int idx) {
        List<Faq_reply> replies = faqService.getRepliesByFaqId(idx);
        System.out.println(replies+"댓글 불러오기 객체");
        return ResponseEntity.ok(replies);
    }

    /**
     * Update reply response entity.
     *
     * @param idx     the idx
     * @param content the content
     * @return the response entity
     */
    @PostMapping("/reply/update")
    public ResponseEntity<String> updateReply(@RequestParam int idx, @RequestParam String content) {
        try {
            Faq_reply faq_Reply = faqService.getReplyById(idx);
            if (faq_Reply == null) {
                return new ResponseEntity<>("해당 답변을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
            }

            faq_Reply.setContent(content);
            faq_Reply.setMod_date(LocalDateTime.now().toString());
            faqService.saveReply(faq_Reply);

            return new ResponseEntity<>("답변이 수정되었습니다.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("답변 수정 오류: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    /**
     * Delete reply response entity.
     *
     * @param idx the idx
     * @return the response entity
     */
    @PostMapping("/reply/delete")
    public ResponseEntity<String> deleteReply(@RequestParam int idx) {
        try {
            Faq_reply faq_Reply = faqService.getReplyById(idx);
            if (faq_Reply == null) {
                return new ResponseEntity<>("해당 답변을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
            }

            faqService.deleteReply(idx);

            return new ResponseEntity<>("답변이 삭제되었습니다.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("답변 삭제 오류: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
