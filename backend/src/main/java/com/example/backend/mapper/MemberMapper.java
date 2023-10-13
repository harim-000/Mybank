package com.example.backend.mapper;

import com.example.backend.domain.Member;
import org.apache.ibatis.annotations.Mapper;

/**
 * The interface Member mapper.
 */
@Mapper
public interface MemberMapper {

    /**
     * Select one by id member.
     *
     * @param id the id
     * @return the member
     */
    Member selectOneById(String id);

    /**
     * Insert member.
     *
     * @param member the member
     */
    void insertMember(Member member);

    /**
     * Select one by email member.
     *
     * @param email the email
     * @return the member
     */
    Member selectOneByEmail(String email);

    /**
     * Update member.
     *
     * @param member the member
     */
    void updateMember(Member member);

    /**
     * Delete by id.
     *
     * @param user_id the user id
     */
    void deleteById(String user_id);

    void deleteAccountById(String user_id);

    void deleteFaqById(String user_id);

    void deleteFaq_replyById(String user_id);

    Member isIdEmailDuplicate(String userId);

    String findId(String toEmail);

    void updatePwd(Member member);
}
