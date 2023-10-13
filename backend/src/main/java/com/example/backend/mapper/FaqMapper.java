package com.example.backend.mapper;

import com.example.backend.domain.Faq;
import com.example.backend.domain.Faq_reply;
import com.example.backend.domain.MyQuestionRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * The interface Faq mapper.
 */
@Mapper
public interface FaqMapper {
    /**
     * Insert faq.
     *
     * @param faq the faq
     */
    void insertFaq(Faq faq);

    /**
     * Gets faq list.
     *
     * @param startNo the start no
     * @param endNo   the end no
     * @return the faq list
     */
    List<Faq> getFaqList(int startNo, int endNo);

    /**
     * Gets faq.
     *
     * @param idx the idx
     * @return the faq
     */
    Faq getFaq(int idx);

    /**
     * Update faq int.
     *
     * @param faq the faq
     * @return the int
     */
    int updateFaq(Faq faq);

    /**
     * Gets total faq count.
     *
     * @return the total faq count
     */
    int getTotalFaqCount();

    /**
     * Delete faq.
     *
     * @param idx the idx
     */
    void deleteFaq(int idx);

    /**
     * Insert faq reply.
     *
     * @param faqReply the faq reply
     */
    void insertFaqReply(Faq_reply faqReply);

    /**
     * Gets replies by faq id.
     *
     * @param faq_id the faq id
     * @return the replies by faq id
     */
    List<Faq_reply> getRepliesByFaqId(int faq_id);

    /**
     * Gets reply by id.
     *
     * @param idx the idx
     * @return the reply by id
     */
    Faq_reply getReplyById(int idx);

    /**
     * Save reply.
     *
     * @param faqReply the faq reply
     */
    void saveReply(Faq_reply faqReply);

    /**
     * Delete reply.
     *
     * @param idx the idx
     */
    void deleteReply(int idx);

    /**
     * Delete faq reply.
     *
     * @param faqId the faq id
     */
    void deleteFaqReply(int faqId);

    /**
     * Gets my faq list.
     *
     * @param  myQuestionRequest myQuestionRequest
     * @return my faq list
     */
    List<Faq> getMyFaqList(MyQuestionRequest myQuestionRequest);

    /**
     * Gets my reply list.
     *
     * @param user_id the user_id
     * @return the reply list
     */
    List<Faq> getMyReplyList(MyQuestionRequest myQuestionRequest);

    int getTotalMyFaqCount(String userId);

    int getTotalMyFaqReplyCount(String userId);
}
