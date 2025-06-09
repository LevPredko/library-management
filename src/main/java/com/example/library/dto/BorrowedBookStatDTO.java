package com.example.library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BorrowedBookStatDTO {
    private String title;
    private Long count;
}
