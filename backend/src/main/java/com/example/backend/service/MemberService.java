package com.example.backend.service;

import com.example.backend.domain.Member;
import com.example.backend.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {


    private final MemberMapper memberMapper;
    public Member selectOneById(String id) {
        return memberMapper.selectOneById(id);
    }

    public void insertMember(Member member) {
        memberMapper.insertMember(member);
    }

    public boolean isIdDuplicate(String user_id) {
        Member res = memberMapper.selectOneById(user_id);
        return res != null;
    }

    public boolean isEmailDuplicate(String user_email) {
        Member res = memberMapper.selectOneByEmail(user_email);
        return res != null;
    }

    public void updateMember(Member member) {
        memberMapper.updateMember(member);
    }

    public void deleteById(String user_id) {
        memberMapper.deleteById(user_id);
    }

    public void deleteAccount(String user_id) {
        memberMapper.deleteAccountById(user_id);
    }

    public void deleteFaq(String user_id) {
        memberMapper.deleteFaqById(user_id);

    }
    public void deleteFaq_Reply(String user_id) {
        memberMapper.deleteFaq_replyById(user_id);

    }
    public Member isIdEmailDuplicate(String user_id) {
        return memberMapper.isIdEmailDuplicate(user_id);
    }

    public String findId(String toEmail) {
        return memberMapper.findId(toEmail);
    }
    @Transactional
    public void updatePwd(Member member) {
        memberMapper.updatePwd(member);
    }
}
