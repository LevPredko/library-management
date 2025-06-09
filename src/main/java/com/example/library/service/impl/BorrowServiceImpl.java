package com.example.library.service.impl;

import com.example.library.dto.BorrowedBookStatDTO;
import com.example.library.entity.Book;
import com.example.library.entity.Borrow;
import com.example.library.entity.Member;
import com.example.library.exception.NotFoundException;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BorrowRepository;
import com.example.library.repository.MemberRepository;
import com.example.library.service.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BorrowServiceImpl implements BorrowService {

    private final BorrowRepository borrowRepository;
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;

    @Value("${member.borrow.limit}")
    private int maxAllowedBooks;

    @Override
    public void borrowBook(Long memberId, Long bookId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("Member with ID " + memberId + " not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Book with ID " + bookId + " not found"));

        List<Borrow> activeBorrows = borrowRepository.findByMemberIdAndReturnDateIsNull(memberId);
        if (activeBorrows.size() >= maxAllowedBooks) {
            throw new IllegalStateException("Member has reached the maximum allowed borrowed books.");
        }

        if (book.getAmount() <= 0) {
            throw new IllegalStateException("Book is currently unavailable.");
        }

        Borrow borrow = new Borrow();
        borrow.setMember(member);
        borrow.setBook(book);
        borrow.setBorrowDate(LocalDate.now());

        borrowRepository.save(borrow);

        book.setAmount(book.getAmount() - 1);
        bookRepository.save(book);
    }

    public void returnBook(Long memberId, Long bookId) {
        List<Borrow> borrows = borrowRepository.findByMemberIdAndReturnDateIsNull(memberId);

        Borrow matchingBorrow = borrows.stream()
                .filter(borrow -> borrow.getBook().getId().equals(bookId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Book with ID " + bookId + " is not currently borrowed by member " + memberId));

        Book book = matchingBorrow.getBook();
        book.setAmount(book.getAmount() + 1);
        matchingBorrow.setReturnDate(LocalDate.now());

        borrowRepository.save(matchingBorrow);
        bookRepository.save(book);
    }



    @Override
    public List<String> getBorrowedBooksByMemberName(String name) {
        Member member = memberRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new NotFoundException("Member with name " + name + " not found"));

        return borrowRepository.findByMemberIdAndReturnDateIsNull(member.getId())
                .stream()
                .map(b -> b.getBook().getTitle())
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getDistinctBorrowedBooks() {
        return borrowRepository.findAllBorrowedDistinctBookTitles();
    }

    @Override
    public List<BorrowedBookStatDTO> getBorrowedBookStats() {
        return borrowRepository.findBorrowedBookTitleWithCount()
                .stream()
                .map(obj -> new BorrowedBookStatDTO((String) obj[0], (Long) obj[1]))
                .collect(Collectors.toList());
    }
}
