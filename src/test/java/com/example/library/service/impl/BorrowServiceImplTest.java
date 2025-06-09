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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BorrowServiceImplTest {

    @InjectMocks
    private BorrowServiceImpl borrowService;

    @Mock
    private BorrowRepository borrowRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(borrowService, "maxAllowedBooks", 2);
    }

    @Test
    void borrowBook_success() {
        Member member = new Member();
        Book book = new Book();
        book.setAmount(3);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(bookRepository.findById(2L)).thenReturn(Optional.of(book));
        when(borrowRepository.findByMemberIdAndReturnDateIsNull(1L)).thenReturn(emptyList());

        borrowService.borrowBook(1L, 2L);

        verify(borrowRepository).save(any(Borrow.class));
        verify(bookRepository).save(book);
        assertEquals(2, book.getAmount());
    }

    @Test
    void borrowBook_memberNotFound() {
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> borrowService.borrowBook(1L, 2L));
        assertTrue(exception.getMessage().contains("Member with ID"));
    }

    @Test
    void borrowBook_bookNotFound() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(new Member()));
        when(bookRepository.findById(2L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> borrowService.borrowBook(1L, 2L));
        assertTrue(exception.getMessage().contains("Book with ID"));
    }

    @Test
    void borrowBook_limitExceeded() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(new Member()));
        when(bookRepository.findById(2L)).thenReturn(Optional.of(new Book()));
        when(borrowRepository.findByMemberIdAndReturnDateIsNull(1L))
                .thenReturn(List.of(new Borrow(), new Borrow()));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> borrowService.borrowBook(1L, 2L));
        assertTrue(exception.getMessage().contains("maximum allowed"));
    }

    @Test
    void borrowBook_bookUnavailable() {
        Book book = new Book();
        book.setAmount(0);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(new Member()));
        when(bookRepository.findById(2L)).thenReturn(Optional.of(book));
        when(borrowRepository.findByMemberIdAndReturnDateIsNull(1L)).thenReturn(emptyList());

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> borrowService.borrowBook(1L, 2L));
        assertTrue(exception.getMessage().contains("unavailable"));
    }

    @Test
    void returnBook_success() {
        Book book = new Book();
        book.setId(2L);
        book.setAmount(1);

        Borrow borrow = new Borrow();
        borrow.setBook(book);

        when(borrowRepository.findByMemberIdAndReturnDateIsNull(1L)).thenReturn(List.of(borrow));

        borrowService.returnBook(1L, 2L);

        verify(borrowRepository).save(borrow);
        verify(bookRepository).save(book);
        assertEquals(2, book.getAmount());
        assertEquals(LocalDate.now(), borrow.getReturnDate());
    }


    @Test
    void returnBook_notBorrowed() {
        when(borrowRepository.findByMemberIdAndReturnDateIsNull(1L)).thenReturn(emptyList());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> borrowService.returnBook(1L, 2L));
        assertTrue(exception.getMessage().contains("not currently borrowed"));
    }

    @Test
    void getBorrowedBooksByMemberName_success() {
        Member member = new Member();
        member.setId(1L);
        Book book = new Book();
        book.setTitle("Clean Code");
        Borrow borrow = new Borrow();
        borrow.setBook(book);

        when(memberRepository.findByNameIgnoreCase("Alice")).thenReturn(Optional.of(member));
        when(borrowRepository.findByMemberIdAndReturnDateIsNull(1L)).thenReturn(List.of(borrow));

        List<String> titles = borrowService.getBorrowedBooksByMemberName("Alice");

        assertEquals(List.of("Clean Code"), titles);
    }

    @Test
    void getBorrowedBooksByMemberName_notFound() {
        when(memberRepository.findByNameIgnoreCase("Alice")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> borrowService.getBorrowedBooksByMemberName("Alice"));
    }

    @Test
    void getDistinctBorrowedBooks() {
        when(borrowRepository.findAllBorrowedDistinctBookTitles())
                .thenReturn(List.of("Book A", "Book B"));

        List<String> titles = borrowService.getDistinctBorrowedBooks();

        assertEquals(List.of("Book A", "Book B"), titles);
    }
}
