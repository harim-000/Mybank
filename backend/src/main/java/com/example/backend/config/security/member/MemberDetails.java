package com.example.backend.config.security.member;

import com.example.backend.domain.Member;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

/**
 * The type Member details.
 */
@Data
public class MemberDetails implements UserDetails {
    private Member member;

    /**
     * Instantiates a new Member details.
     *
     * @param member the member
     */
    public MemberDetails(Member member) {
        this.member = member;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList();
        authorities.add((new SimpleGrantedAuthority("ROLE_USER")));
        return authorities;
    }

    @Override
    public String getPassword() {
        return member.getUser_pass();
    }

    @Override
    public String getUsername() {
        return member.getUser_id(); // 현재 유저의 이름을 반환하도록 수정
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return member.isEnabled();
    }

}