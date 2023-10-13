package com.example.backend.domain;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * The type Account.
 */
@Data
@Component
public class Account {

    private String user_id;
    private String description;
    private long amount;
    private String date;
    private String category;
    private String reg_date;
    private int idx;
    private String sort;

}
