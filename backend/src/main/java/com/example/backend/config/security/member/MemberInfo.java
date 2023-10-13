package com.example.backend.config.security.member;

import com.example.backend.domain.Member;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * The type Member info.
 */
public class MemberInfo {

    /**
     * Sets member cookie.
     *
     * @param response the response
     * @param member   the member
     * @return the member cookie
     */
    public static String setMemberCookie(HttpServletResponse response, Member member) {
        member.setUser_pass(null);

        return(member.getUser_id());
    }
}
