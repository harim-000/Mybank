package com.example.backend.domain;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class MyQuestionRequest {
    private int offset;
    private String user_id;
    private int limit;

}
