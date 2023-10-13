package com.example.backend.mapper;

import com.example.backend.domain.Faq;
import com.example.backend.domain.Notice;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.SqlSessionTemplate;

import java.util.List;

/**
 * The interface Notice mapper.
 */
@Mapper
public interface NoticeMapper {

    /**
     * Gets total notice count.
     *
     * @return the total notice count
     */
    int getTotalNoticeCount();

    /**
     * Gets notice list.
     *
     * @param limit the start no
     * @param offset   the end no
     * @return the notice list
     */
    List<Notice> getNoticeList(int limit, int offset);

    /**
     * Gets notice cont.
     *
     * @param idx the idx
     * @return the notice cont
     */
    Notice getNoticeCont(int idx);
}
