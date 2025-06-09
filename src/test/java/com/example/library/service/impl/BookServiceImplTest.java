package com.example.library.service.impl;

import com.example.library.dto.BookDTO;
import com.example.library.entity.Book;
import com.example.library.exception.DeleteConstraintException;
import com.example.library.exception.NotFoundException;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BorrowRepository;
import com.example.library.validation.BookDTOValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceImplTest {

    private BookRepository bookRepository;
    private BorrowRepository borrowRepository;
    private BookDTOValidator validator;
    private BookServiceImpl bookService;

    @BeforeEach
    void setUp() {
        bookRepository = mock(BookRepository.class);
        borrowRepository = mock(BorrowRepository.class);
        validator = mock(BookDTOValidator.class);
        bookService = new BookServiceImpl(bookRepository, borrowRepository, validator);
    }

    @Test
    void testAddBook_NewBook_Success() {
        BookDTO dto = new BookDTO();
        dto.setTitle("1984");
        dto.setAuthor("George Orwell");
        dto.setAmount(5);

        when(bookRepository.findByTitleAndAuthor("1984", "George Orwell")).thenReturn(Optional.empty());

        Book saved = new Book();
        saved.setId(1L);
        saved.setTitle("1984");
        saved.setAuthor("George Orwell");
        saved.setAmount(5);

        when(bookRepository.save(any(Book.class))).thenReturn(saved);

        BookDTO result = bookService.addBook(dto);

        assertEquals(1L, result.getId());
        assertEquals("1984", result.getTitle());
        assertEquals("George Orwell", result.getAuthor());
        assertEquals(5, result.getAmount());
        verify(validator).validate(dto);
    }

    @Test
    void testAddBook_ExistingBook_UpdatesAmount() {
        BookDTO dto = new BookDTO();
        dto.setTitle("Dune");
        dto.setAuthor("Frank Herbert");
        dto.setAmount(3);

        Book existing = new Book();
        existing.setId(2L);
        existing.setTitle("Dune");
        existing.setAuthor("Frank Herbert");
        existing.setAmount(4);

        when(bookRepository.findByTitleAndAuthor("Dune", "Frank Herbert")).thenReturn(Optional.of(existing));
        when(bookRepository.save(any(Book.class))).thenReturn(existing);

        BookDTO result = bookService.addBook(dto);

        assertEquals(2L, result.getId());
        assertEquals(7, result.getAmount()); // 4 + 3
        verify(bookRepository).save(existing);
    }

    @Test
    void testUpdateBook_Success() {
        Long id = 1L;
        BookDTO dto = new BookDTO();
        dto.setTitle("Brave New World");
        dto.setAuthor("Aldous Huxley");
        dto.setAmount(10);

        Book existing = new Book();
        existing.setId(id);
        existing.setTitle("Old");
        existing.setAuthor("Author");
        existing.setAmount(1);

        when(bookRepository.findById(id)).thenReturn(Optional.of(existing));
        when(bookRepository.save(any(Book.class))).thenReturn(existing);

        BookDTO result = bookService.updateBook(id, dto);

        assertEquals("Brave New World", result.getTitle());
        assertEquals("Aldous Huxley", result.getAuthor());
        assertEquals(10, result.getAmount());
        verify(validator).validate(dto);
    }

    @Test
    void testUpdateBook_NotFound() {
        BookDTO dto = new BookDTO();
        dto.setTitle("Title");
        dto.setAuthor("Author");
        dto.setAmount(5);

        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookService.updateBook(99L, dto));
    }

    @Test
    void testDeleteBook_Success() {
        Book book = new Book();
        book.setId(3L);

        when(bookRepository.findById(3L)).thenReturn(Optional.of(book));
        when(borrowRepository.existsByBookIdAndReturnDateIsNull(3L)).thenReturn(false);

        bookService.deleteBook(3L);

        verify(bookRepository).delete(book);
    }

    @Test
    void testDeleteBook_ConstraintException() {
        Book book = new Book();
        book.setId(4L);

        when(bookRepository.findById(4L)).thenReturn(Optional.of(book));
        when(borrowRepository.existsByBookIdAndReturnDateIsNull(4L)).thenReturn(true);

        assertThrows(DeleteConstraintException.class, () -> bookService.deleteBook(4L));
    }

    @Test
    void testDeleteBook_NotFound() {
        when(bookRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookService.deleteBook(100L));
    }

    @Test
    void testListBooks() {
        Book b1 = new Book();
        b1.setId(1L);
        b1.setTitle("Book One");
        b1.setAuthor("Author A");
        b1.setAmount(2);

        Book b2 = new Book();
        b2.setId(2L);
        b2.setTitle("Book Two");
        b2.setAuthor("Author B");
        b2.setAmount(5);

        when(bookRepository.findAll()).thenReturn(List.of(b1, b2));

        List<BookDTO> result = bookService.listBooks();

        assertEquals(2, result.size());
        assertEquals("Book One", result.get(0).getTitle());
        assertEquals("Book Two", result.get(1).getTitle());
    }

    @Test
    void testGetBookById_Success() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setAuthor("Tester");
        book.setAmount(1);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        BookDTO result = bookService.getBookById(1L);

        assertEquals("Test Book", result.getTitle());
        assertEquals("Tester", result.getAuthor());
    }

    @Test
    void testGetBookById_NotFound() {
        when(bookRepository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookService.getBookById(404L));
    }
}
