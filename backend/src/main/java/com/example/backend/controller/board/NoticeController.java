package com.example.backend.controller.board;

import com.example.backend.domain.Faq;
import com.example.backend.domain.Notice;
import com.example.backend.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Notice controller.
 */
@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/notice")
public class NoticeController {
    private final NoticeService noticeService;

    /**
     * Gets notice list.
     *
     * @param page         the page
     * @param itemsPerPage the items per page
     * @return the notice list
     */
    @PostMapping(path = "/list", produces = "application/json")
    public ResponseEntity<Map<String, Object>> getNoticeList(@RequestParam(defaultValue = "1") int page,
                                                          @RequestParam(defaultValue = "10") int itemsPerPage) {
        // 전체 게시글 수 가져오기
        int totalItems = noticeService.getTotalNoticeCount();

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

        List<Notice> noticeList = noticeService.getNoticeList(limit, offset); // 데이터 가져오기
        System.out.println(noticeList);
        Map<String, Object> response = new HashMap<>();
        response.put("totalItems", totalItems);
        response.put("totalPages", totalPages);
        response.put("currentPage", page);
        response.put("noticeList", noticeList);
        System.out.println(response);

        return ResponseEntity.ok(response);
    }

    /**
     * Gets notice cont.
     *
     * @param idx the idx
     * @return the notice cont
     */
    @PostMapping("/getNotice")
    public ResponseEntity<Notice> getNoticeCont(@RequestParam int idx) {

        Notice noticeCont = noticeService.getNoticeCont(idx); // 데이터 가져오기
        System.out.println(noticeCont+"공지글 컨트롤러 반환값");

        return ResponseEntity.ok(noticeCont);
    }

}
