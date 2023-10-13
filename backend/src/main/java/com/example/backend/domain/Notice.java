package com.example.backend.domain;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * The type Notice.
 */
@Data
@Component
public class Notice {

    private int idx;
    private String user_id;
    private String title;
    private String content;
    private String reg_date;
    private String mod_date;
    private String type;
    private String file;

}
