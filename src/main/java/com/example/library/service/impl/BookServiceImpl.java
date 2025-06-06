package com.example.library.service.impl;

import com.example.library.dto.BookDTO;
import com.example.library.entity.Book;
import com.example.library.exception.NotFoundException;
import com.example.library.exception.DeleteConstraintException;
import com.example.library.repository.BookRepository;
import com.example.library.repository.MemberRepository;
import com.example.library.service.BookService;
import com.example.library.validation.BookDTOValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final BookDTOValidator bookDTOValidator;

    private BookDTO mapToDTO(Book book) {
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setAmount(book.getAmount());
        return dto;
    }

    private Book mapToEntity(BookDTO dto) {
        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setAmount(dto.getAmount());
        return book;
    }

    @Override
    @Transactional
    public BookDTO addBook(BookDTO bookDTO) {
        bookDTOValidator.validate(bookDTO);
        Optional<Book> existing = bookRepository.findByTitleAndAuthor(bookDTO.getTitle(), bookDTO.getAuthor());
        if (existing.isPresent()) {
            Book existingBook = existing.get();
            existingBook.setAmount(existingBook.getAmount() + bookDTO.getAmount());
            Book saved = bookRepository.save(existingBook);
            return mapToDTO(saved);
        } else {
            Book bookToSave = mapToEntity(bookDTO);
            Book saved = bookRepository.save(bookToSave);
            return mapToDTO(saved);
        }
    }

    @Override
    @Transactional
    public BookDTO updateBook(Long id, BookDTO bookDTO) {
        bookDTOValidator.validate(bookDTO);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book with ID " + id + " not found"));
        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setAmount(bookDTO.getAmount());
        Book updated = bookRepository.save(book);
        return mapToDTO(updated);
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book with ID " + id + " not found"));

        boolean isBorrowed = memberRepository.existsByBorrowedBooksContaining(book);
        if (isBorrowed) {
            throw new DeleteConstraintException("Book is currently borrowed and cannot be deleted");
        }
        bookRepository.delete(book);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDTO> listBooks() {
        return bookRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BookDTO getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book with ID " + id + " not found"));
        return mapToDTO(book);
    }
}
