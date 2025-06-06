package com.example.library.service.impl;

import com.example.library.dto.MemberDTO;
import com.example.library.entity.Member;
import com.example.library.exception.NotFoundException;
import com.example.library.repository.MemberRepository;
import com.example.library.validation.MemberDTOValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MemberServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberDTOValidator validator;

    @InjectMocks
    private MemberServiceImpl memberService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateMember() {
        MemberDTO dto = new MemberDTO();
        dto.setName("Ivan");

        Member saved = new Member();
        saved.setId(1L);
        saved.setName("Ivan");
        saved.setMembershipDate(LocalDate.now());

        when(memberRepository.save(any(Member.class))).thenReturn(saved);

        MemberDTO result = memberService.createMember(dto);

        assertEquals("Ivan", result.getName());
        assertNotNull(result.getMembershipDate());
        verify(validator).validate(dto);
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    void testGetMemberById_Found() {
        Member member = new Member();
        member.setId(1L);
        member.setName("Olena");
        member.setMembershipDate(LocalDate.now());

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        MemberDTO result = memberService.getMemberById(1L);

        assertEquals("Olena", result.getName());
    }

    @Test
    void testGetMemberById_NotFound() {
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> memberService.getMemberById(1L));
    }

    @Test
    void testGetAllMembers() {
        Member member = new Member();
        member.setId(1L);
        member.setName("Test");
        member.setMembershipDate(LocalDate.now());

        when(memberRepository.findAll()).thenReturn(List.of(member));

        List<MemberDTO> result = memberService.getAllMembers();

        assertEquals(1, result.size());
        assertEquals("Test", result.get(0).getName());
    }

    @Test
    void testUpdateMember() {
        Member existing = new Member();
        existing.setId(1L);
        existing.setName("Old");

        when(memberRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(memberRepository.save(any(Member.class))).thenReturn(existing);

        MemberDTO dto = new MemberDTO();
        dto.setName("New");

        MemberDTO updated = memberService.updateMember(1L, dto);

        assertEquals("New", updated.getName());
        verify(validator).validate(dto);
    }

    @Test
    void testDeleteMember() {
        Member member = new Member();
        member.setId(1L);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        memberService.deleteMember(1L);

        verify(memberRepository).delete(member);
    }
}
