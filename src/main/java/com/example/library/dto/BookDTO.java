package com.example.library.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BookDTO {
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 3, message = "Title must be at least 3 characters")
    @Pattern(regexp = "^[A-Z].*", message = "Title must start with a capital letter")
    private String title;

    @NotBlank(message = "Author is required")
    @Pattern(
            regexp = "^[A-Z][a-z]+\\s[A-Z][a-z]+$",
            message = "Author must be in format: Name Surname (e.g., Paulo Coelho)"
    )
    private String author;

    @Min(value = 0, message = "Amount must be non-negative")
    private int amount;
}
