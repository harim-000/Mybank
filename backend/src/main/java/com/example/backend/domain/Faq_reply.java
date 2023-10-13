package com.example.backend.domain;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * The type Faq reply.
 */
@Data
@Component
public class Faq_reply {

    private int idx;
    private int faq_id;
    private String content;
    private String user_id;
    private String reg_date;
    private String mod_date;

}
