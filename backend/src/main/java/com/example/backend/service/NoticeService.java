package com.example.backend.service;

import com.example.backend.domain.Notice;
import com.example.backend.mapper.NoticeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeMapper noticeMapper;
    public int getTotalNoticeCount() {
        return noticeMapper.getTotalNoticeCount();
    }

    public List<Notice> getNoticeList(int limit, int offset) {
        return noticeMapper.getNoticeList(limit, offset);
    }

    public Notice getNoticeCont(int idx) {
        return noticeMapper.getNoticeCont(idx);
    }
}
