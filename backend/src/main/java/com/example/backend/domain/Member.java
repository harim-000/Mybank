package com.example.backend.domain;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * The type Member.
 */
@Data
@Component
public class Member {
    private String user_id;
    private String user_pass;
    private String user_email;
    private String reg_date;
    private String mod_date;
    private String type;
    private boolean enabled;
    private String user_profile;

}
