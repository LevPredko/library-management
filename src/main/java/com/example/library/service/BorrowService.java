package com.example.library.service;

import com.example.library.dto.BorrowedBookStatDTO;

import java.util.List;

public interface BorrowService {
    void borrowBook(Long memberId, Long bookId);
    void returnBook(Long memberId, Long bookId);
    List<String> getBorrowedBooksByMemberName(String name);
    List<String> getDistinctBorrowedBooks();
    List<BorrowedBookStatDTO> getBorrowedBookStats();
}