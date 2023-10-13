package com.example.backend.repository;

import com.example.backend.domain.Faq;
import com.example.backend.domain.Notice;
import com.example.backend.mapper.NoticeMapper;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Notice repository.
 */
@Repository
@RequiredArgsConstructor
public class NoticeRepository implements NoticeMapper {

    private final SqlSessionTemplate sqlSessionTemplate;
    private final String NAME_SPACE = "com.example.backend.mapper.notice.";

    @Override
    public int getTotalNoticeCount() {
        return sqlSessionTemplate.selectOne(NAME_SPACE+"getTotalNoticeCount");
    }

    @Override
    public List<Notice> getNoticeList(int limit, int offset) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("limit", limit);
        parameterMap.put("offset", offset);
        return sqlSessionTemplate.selectList(NAME_SPACE + "getNoticeList", parameterMap);    }

    @Override
    public Notice getNoticeCont(int idx) {
        return sqlSessionTemplate.selectOne(NAME_SPACE + "getNoticeCont", idx);
    }
}
