package com.example.backend.repository;

import com.example.backend.domain.Faq;
import com.example.backend.domain.Faq_reply;
import com.example.backend.domain.MyQuestionRequest;
import com.example.backend.mapper.FaqMapper;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Faq repository.
 */
@Repository
@RequiredArgsConstructor
public class FaqRepository implements FaqMapper {

    private final SqlSessionTemplate sqlSessionTemplate;
    private final String NAME_SPACE = "com.example.backend.mapper.faq.";
    @Override
    public void insertFaq(Faq faq) {
        sqlSessionTemplate.insert(NAME_SPACE+"insertFaq", faq);
    }

    @Override
    public List<Faq> getFaqList(int startNo, int endNo) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("startNo", startNo);
        parameterMap.put("endNo", endNo);
        return sqlSessionTemplate.selectList(NAME_SPACE + "getFaqList", parameterMap);
    }

    @Override
    public Faq getFaq(int idx) {
        return sqlSessionTemplate.selectOne(NAME_SPACE + "getFaq", idx);
    }

    @Override
    public int updateFaq(Faq faq) {
        return sqlSessionTemplate.update(NAME_SPACE+"updateFaq", faq);
    }

    @Override
    public int getTotalFaqCount() {
        return sqlSessionTemplate.selectOne(NAME_SPACE+"getTotalFaqCount");
    }

    @Override
    public void deleteFaq(int idx) {
        sqlSessionTemplate.delete(NAME_SPACE+"deleteFaq", idx);
    }

    @Override
    public void insertFaqReply(Faq_reply faqReply) {
        sqlSessionTemplate.insert(NAME_SPACE+"insertFaqReply", faqReply);
    }

    @Override
    public List<Faq_reply> getRepliesByFaqId(int faq_id) {
        return sqlSessionTemplate.selectList(NAME_SPACE+"getRepliesByFaqId", faq_id);
    }

    @Override
    public Faq_reply getReplyById(int idx) {
        return sqlSessionTemplate.selectOne(NAME_SPACE+"getReplyById", idx);
    }

    @Override
    public void saveReply(Faq_reply faqReply) {
        sqlSessionTemplate.update(NAME_SPACE+"saveReply", faqReply);
    }

    @Override
    public void deleteReply(int idx) {
        sqlSessionTemplate.delete(NAME_SPACE+"deleteReply", idx);
    }

    @Override
    public void deleteFaqReply(int faq_id) {
        sqlSessionTemplate.delete(NAME_SPACE+"deleteFaqReply", faq_id);
    }

    @Override
    public List<Faq> getMyFaqList(MyQuestionRequest myQuestionRequest) {
        return sqlSessionTemplate.selectList(NAME_SPACE+"getMyFaqList", myQuestionRequest);
    }

    @Override
    public List<Faq> getMyReplyList(MyQuestionRequest myQuestionRequest) {
        return sqlSessionTemplate.selectList(NAME_SPACE+"getMyReplyList", myQuestionRequest);
    }

    @Override
    public int getTotalMyFaqCount(String userId) {
        return sqlSessionTemplate.selectOne(NAME_SPACE+"getTotalMyFaqCount", userId);
    }

    @Override
    public int getTotalMyFaqReplyCount(String userId) {
        return sqlSessionTemplate.selectOne(NAME_SPACE+"getTotalMyFaqReplyCount", userId);
    }
}
