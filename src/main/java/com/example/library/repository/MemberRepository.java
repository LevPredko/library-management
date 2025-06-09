package com.example.library.repository;

import com.example.library.entity.Book;
import com.example.library.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByName(String name);
    Optional<Member> findByNameIgnoreCase(String name);
    boolean existsByBorrows_Book(Book book);
}
