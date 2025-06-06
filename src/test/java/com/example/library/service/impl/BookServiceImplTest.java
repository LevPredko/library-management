package com.example.library.service.impl;

import com.example.library.dto.BookDTO;
import com.example.library.entity.Book;
import com.example.library.exception.DeleteConstraintException;
import com.example.library.exception.NotFoundException;
import com.example.library.repository.BookRepository;
import com.example.library.repository.MemberRepository;
import com.example.library.validation.BookDTOValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private BookDTOValidator bookDTOValidator;
    @InjectMocks
    private BookServiceImpl bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private BookDTO createDTO() {
        BookDTO dto = new BookDTO();
        dto.setTitle("Title");
        dto.setAuthor("Author");
        dto.setAmount(1);
        return dto;
    }

    private Book createBook(Long id) {
        Book book = new Book();
        book.setId(id);
        book.setTitle("Title");
        book.setAuthor("Author");
        book.setAmount(1);
        return book;
    }

    @Test
    void shouldAddNewBookIfNotExists() {
        BookDTO dto = createDTO();
        when(bookRepository.findByTitleAndAuthor("Title", "Author")).thenReturn(Optional.empty());
        when(bookRepository.save(any())).thenAnswer(inv -> {
            Book b = inv.getArgument(0);
            b.setId(1L);
            return b;
        });

        BookDTO result = bookService.addBook(dto);

        assertEquals("Title", result.getTitle());
        assertEquals("Author", result.getAuthor());
        assertEquals(1, result.getAmount());
    }

    @Test
    void shouldIncreaseAmountIfBookAlreadyExists() {
        BookDTO dto = createDTO();
        Book existing = createBook(5L);
        existing.setAmount(2);

        when(bookRepository.findByTitleAndAuthor("Title", "Author")).thenReturn(Optional.of(existing));
        when(bookRepository.save(any())).thenReturn(existing);

        BookDTO result = bookService.addBook(dto);

        assertEquals(3, result.getAmount());
    }

    @Test
    void shouldUpdateBook() {
        BookDTO dto = createDTO();
        Book existing = createBook(1L);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(bookRepository.save(any())).thenReturn(existing);

        BookDTO result = bookService.updateBook(1L, dto);

        assertEquals("Title", result.getTitle());
        assertEquals("Author", result.getAuthor());
    }

    @Test
    void shouldThrowWhenBookNotFoundOnUpdate() {
        BookDTO dto = createDTO();

        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookService.updateBook(99L, dto));
    }

    @Test
    void shouldDeleteBookIfNotBorrowed() {
        Book book = createBook(1L);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(memberRepository.existsByBorrowedBooksContaining(book)).thenReturn(false);

        bookService.deleteBook(1L);

        verify(bookRepository).delete(book);
    }

    @Test
    void shouldThrowIfBookNotFoundOnDelete() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookService.deleteBook(1L));
    }

    @Test
    void shouldThrowIfBookIsBorrowed() {
        Book book = createBook(1L);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(memberRepository.existsByBorrowedBooksContaining(book)).thenReturn(true);

        assertThrows(DeleteConstraintException.class, () -> bookService.deleteBook(1L));
    }

    @Test
    void shouldReturnListOfBooks() {
        Book book1 = createBook(1L);
        Book book2 = createBook(2L);
        book2.setTitle("Another");

        when(bookRepository.findAll()).thenReturn(Arrays.asList(book1, book2));

        List<BookDTO> books = bookService.listBooks();

        assertEquals(2, books.size());
        assertEquals("Title", books.get(0).getTitle());
        assertEquals("Another", books.get(1).getTitle());
    }

    @Test
    void shouldReturnBookById() {
        Book book = createBook(1L);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        BookDTO dto = bookService.getBookById(1L);

        assertEquals("Title", dto.getTitle());
    }

    @Test
    void shouldThrowIfBookNotFoundById() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookService.getBookById(99L));
    }
}
