package com.example.library.validation;

import com.example.library.dto.BookDTO;
import com.example.library.entity.Book;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class BookDTOValidator {

    public void validate(BookDTO bookDTO) {
        if (bookDTO == null) {
            throw new IllegalArgumentException("BookDTO cannot be null");
        }

        if (!StringUtils.hasText(bookDTO.getTitle())) {
            throw new IllegalArgumentException("Book title must not be empty");
        }

        if (!StringUtils.hasText(bookDTO.getAuthor())) {
            throw new IllegalArgumentException("Book author must not be empty");
        }

        if (bookDTO.getAmount() < 0) {
            throw new IllegalArgumentException("Book amount cannot be negative");
        }
    }
}
