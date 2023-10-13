package com.example.backend.config.security.member;

import com.example.backend.domain.Member;
import com.example.backend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * The type Member details service.
 */
@RequiredArgsConstructor
@Service
public class MemberDetailsService implements UserDetailsService {


    private final MemberRepository memberRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.selectOneById(username);
        if(member == null) {
            throw new UsernameNotFoundException(username);
        }
        return new MemberDetails(member);
    }
}