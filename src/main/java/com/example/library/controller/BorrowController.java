package com.example.library.controller;

import com.example.library.dto.BorrowedBookStatDTO;
import com.example.library.service.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrow")
@RequiredArgsConstructor
public class BorrowController {

    private final BorrowService borrowService;

    @PostMapping("/{memberId}/{bookId}")
    public void borrowBook(@PathVariable Long memberId, @PathVariable Long bookId) {
        borrowService.borrowBook(memberId, bookId);
    }

    @PostMapping("/return/{memberId}/{bookId}")
    public void returnBook(@PathVariable Long memberId, @PathVariable Long bookId) {
        borrowService.returnBook(memberId, bookId);
    }

    @GetMapping("/member")
    public List<String> getBooksBorrowedByMember(@RequestParam String name) {
        return borrowService.getBorrowedBooksByMemberName(name);
    }

    @GetMapping("/distinct")
    public List<String> getDistinctBorrowedBookNames() {
        return borrowService.getDistinctBorrowedBooks();
    }

    @GetMapping("/stats")
    public List<BorrowedBookStatDTO> getBorrowedBookNamesWithCount() {
        return borrowService.getBorrowedBookStats();
    }
}
