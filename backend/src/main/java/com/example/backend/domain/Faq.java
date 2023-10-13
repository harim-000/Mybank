package com.example.backend.domain;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * The type Faq.
 */
@Data
@Component
public class Faq {
    private int idx;
    private String user_id;
    private String title;
    private String content;
    private String reg_date;
    private String mod_date;
    private String open;
    private String answered;
}
