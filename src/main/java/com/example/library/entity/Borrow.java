package com.example.library.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Borrow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Member member;

    @ManyToOne
    private Book book;

    private LocalDate borrowDate;
}
