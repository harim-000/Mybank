package com.example.backend.service;

import com.example.backend.domain.Faq;
import com.example.backend.domain.Faq_reply;
import com.example.backend.domain.MyQuestionRequest;
import com.example.backend.mapper.FaqMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FaqService {

    private final FaqMapper faqMapper;

    public void insertFaq(Faq faq){
        faqMapper.insertFaq(faq);
    };

    public List<Faq> getFaqList(int startNo, int endNo) {
        return faqMapper.getFaqList(startNo, endNo);
    };

    public Faq getFaq(int idx) {
        return faqMapper.getFaq(idx);
    }

    public int updateFaq(Faq faq) {
        return faqMapper.updateFaq(faq);
    }

    public int getTotalFaqCount() {
        return faqMapper.getTotalFaqCount();
    }

    public void deleteFaq(int idx) {
        faqMapper.deleteFaq(idx);
    }

    public void insertFaqReply(Faq_reply faqReply) {
        faqMapper.insertFaqReply(faqReply);
    }

    public List<Faq_reply> getRepliesByFaqId(int idx) {
        return faqMapper.getRepliesByFaqId(idx);
    }

    public Faq_reply getReplyById(int idx) {
        return faqMapper.getReplyById(idx);
    }

    public void saveReply(Faq_reply faqReply) {
        faqMapper.saveReply(faqReply);
    }

    public void deleteReply(int idx) {
        faqMapper.deleteReply(idx);
    }

    public void deleteFaqReply(int faq_id) {
        faqMapper.deleteFaqReply(faq_id);
    }

    public List<Faq> getMyFaqList(MyQuestionRequest myQuestionRequest) {
        return faqMapper.getMyFaqList(myQuestionRequest);
    }

    public List<Faq> getMyReplyList(MyQuestionRequest myQuestionRequest) {
        return faqMapper.getMyReplyList(myQuestionRequest);
    }

    public int getTotalMyFaqCount(String userId) {
        return faqMapper.getTotalMyFaqCount(userId);
    }

    public int getTotalMyFaqReplyCount(String userId) {
        return faqMapper.getTotalMyFaqReplyCount(userId);
    }
}
