package com.example.backend.repository;

import com.example.backend.domain.Member;
import com.example.backend.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

/**
 * The type Member repository.
 */
@Repository
@RequiredArgsConstructor
public class MemberRepository implements MemberMapper {

    private final SqlSessionTemplate sqlSessionTemplate;
    private final String NAME_SPACE = "com.example.backend.mapper.member.";

    @Override
    public Member selectOneById(String id) {
        return sqlSessionTemplate.selectOne(NAME_SPACE+"selectOneById", id);
    }

    @Override
    public void insertMember(Member member) {
        sqlSessionTemplate.insert(NAME_SPACE + "insertMember", member);
    }

    @Override
    public Member selectOneByEmail(String email) {
        return sqlSessionTemplate.selectOne(NAME_SPACE+"selectOneByEmail", email);
    }

    @Override
    public void updateMember(Member member) {
        sqlSessionTemplate.update(NAME_SPACE + "updateMember", member);
    }

    @Override
    public void deleteById(String user_id) {
        sqlSessionTemplate.delete(NAME_SPACE+"deleteById", user_id);
    }

    @Override
    public void deleteAccountById(String user_id) {
        sqlSessionTemplate.delete(NAME_SPACE+"deleteAccountById", user_id);
    }

    @Override
    public void deleteFaqById(String user_id) {
        sqlSessionTemplate.delete(NAME_SPACE+"deleteFaqById", user_id);
    }

    @Override
    public void deleteFaq_replyById(String user_id) {
        sqlSessionTemplate.delete(NAME_SPACE+"deleteFaq_replyById", user_id);
    }

    @Override
    public Member isIdEmailDuplicate(String user_id) {
        return sqlSessionTemplate.selectOne(NAME_SPACE+"isIdEmailDuplicate", user_id);
    }

    @Override
    public String findId(String toEmail) {
        return sqlSessionTemplate.selectOne(NAME_SPACE+"findId", toEmail);
    }

    @Override
    public void updatePwd(Member member) {
        sqlSessionTemplate.update(NAME_SPACE+"updatePwd", member);
    }


}
