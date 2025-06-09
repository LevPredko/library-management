package com.example.library.service.impl;

import com.example.library.dto.MemberDTO;
import com.example.library.entity.Member;
import com.example.library.exception.DeleteConstraintException;
import com.example.library.exception.NotFoundException;
import com.example.library.repository.BorrowRepository;
import com.example.library.repository.MemberRepository;
import com.example.library.validation.MemberDTOValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MemberServiceImplTest {

    private MemberRepository memberRepository;
    private MemberDTOValidator validator;
    private BorrowRepository borrowRepository;
    private MemberServiceImpl memberService;

    @BeforeEach
    void setUp() {
        memberRepository = mock(MemberRepository.class);
        validator = mock(MemberDTOValidator.class);
        borrowRepository = mock(BorrowRepository.class);
        memberService = new MemberServiceImpl(memberRepository, validator, borrowRepository);
    }

    @Test
    void testGetAllMembers() {
        Member m1 = new Member();
        m1.setId(1L);
        m1.setName("John Doe");
        m1.setMembershipDate(LocalDate.of(2022, 1, 1));

        Member m2 = new Member();
        m2.setId(2L);
        m2.setName("Jane Smith");
        m2.setMembershipDate(LocalDate.of(2023, 3, 5));

        when(memberRepository.findAll()).thenReturn(List.of(m1, m2));

        List<MemberDTO> result = memberService.getAllMembers();

        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getName());
        assertEquals("Jane Smith", result.get(1).getName());
    }

    @Test
    void testGetMemberById_Success() {
        Member member = new Member();
        member.setId(1L);
        member.setName("Alice");
        member.setMembershipDate(LocalDate.of(2021, 6, 15));

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        MemberDTO dto = memberService.getMemberById(1L);

        assertEquals("Alice", dto.getName());
        assertEquals(LocalDate.of(2021, 6, 15), dto.getMembershipDate());
    }

    @Test
    void testGetMemberById_NotFound() {
        when(memberRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> memberService.getMemberById(999L));
    }

    @Test
    void testCreateMember_Success() {
        MemberDTO input = new MemberDTO();
        input.setName("Bob");

        Member saved = new Member();
        saved.setId(10L);
        saved.setName("Bob");
        saved.setMembershipDate(LocalDate.now());

        when(memberRepository.save(any(Member.class))).thenReturn(saved);

        MemberDTO result = memberService.createMember(input);

        assertEquals(10L, result.getId());
        assertEquals("Bob", result.getName());
        assertEquals(LocalDate.now(), result.getMembershipDate());
        verify(validator).validate(input);
    }

    @Test
    void testUpdateMember_Success() {
        Member existing = new Member();
        existing.setId(5L);
        existing.setName("Old Name");
        existing.setMembershipDate(LocalDate.of(2020, 1, 1));

        MemberDTO input = new MemberDTO();
        input.setName("New Name");

        when(memberRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(memberRepository.save(any(Member.class))).thenReturn(existing);

        MemberDTO result = memberService.updateMember(5L, input);

        assertEquals("New Name", result.getName());
        verify(validator).validate(input);
    }

    @Test
    void testUpdateMember_NotFound() {
        MemberDTO input = new MemberDTO();
        input.setName("Someone");

        when(memberRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> memberService.updateMember(100L, input));
    }

    @Test
    void testDeleteMember_Success() {
        Member member = new Member();
        member.setId(3L);

        when(memberRepository.findById(3L)).thenReturn(Optional.of(member));
        when(borrowRepository.existsByMemberIdAndReturnDateIsNull(3L)).thenReturn(false);

        memberService.deleteMember(3L);

        verify(memberRepository).delete(member);
    }

    @Test
    void testDeleteMember_WithActiveBorrows() {
        Member member = new Member();
        member.setId(4L);

        when(memberRepository.findById(4L)).thenReturn(Optional.of(member));
        when(borrowRepository.existsByMemberIdAndReturnDateIsNull(4L)).thenReturn(true);

        assertThrows(DeleteConstraintException.class, () -> memberService.deleteMember(4L));
    }

    @Test
    void testDeleteMember_NotFound() {
        when(memberRepository.findById(500L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> memberService.deleteMember(500L));
    }
}
