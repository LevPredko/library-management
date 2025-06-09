package com.example.library.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class MemberDTO {
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    private LocalDate membershipDate;
    private Set<Long> borrowedBookIds;
}
