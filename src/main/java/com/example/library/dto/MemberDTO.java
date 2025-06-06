package com.example.library.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class MemberDTO {
    private Long id;
    private String name;
    private LocalDate membershipDate;
    private Set<Long> borrowedBookIds;
}
