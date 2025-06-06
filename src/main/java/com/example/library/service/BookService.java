package com.example.library.service;

import com.example.library.dto.BookDTO;

import java.util.List;

public interface BookService {
    BookDTO addBook(BookDTO bookDTO);
    BookDTO updateBook(Long id, BookDTO bookDTO);
    void deleteBook(Long id);
    List<BookDTO> listBooks();
    BookDTO getBookById(Long id);
}
