package com.example.library.repository;

import com.example.library.entity.Borrow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface BorrowRepository extends JpaRepository<Borrow, Long> {

    List<Borrow> findByMemberIdAndReturnDateIsNull(Long memberId);

    boolean existsByMemberIdAndReturnDateIsNull(Long memberId);

    boolean existsByBookIdAndReturnDateIsNull(Long bookId);

    @Query("SELECT b.book.title FROM Borrow b WHERE b.returnDate IS NULL GROUP BY b.book.title")
    List<String> findAllBorrowedDistinctBookTitles();

    @Query("SELECT b.book.title, COUNT(b) FROM Borrow b WHERE b.returnDate IS NULL GROUP BY b.book.title")
    List<Object[]> findBorrowedBookTitleWithCount();
}