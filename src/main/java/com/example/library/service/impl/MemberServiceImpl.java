package com.example.library.service.impl;

import com.example.library.dto.MemberDTO;
import com.example.library.entity.Member;
import com.example.library.exception.DeleteConstraintException;
import com.example.library.exception.NotFoundException;
import com.example.library.repository.BorrowRepository;
import com.example.library.repository.MemberRepository;
import com.example.library.service.MemberService;
import com.example.library.validation.MemberDTOValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final MemberDTOValidator memberDTOValidator;
    private final BorrowRepository borrowRepository;

    private MemberDTO toDTO(Member member) {
        MemberDTO dto = new MemberDTO();
        dto.setId(member.getId());
        dto.setName(member.getName());
        dto.setMembershipDate(member.getMembershipDate());
        return dto;
    }

    private Member toEntity(MemberDTO dto) {
        Member member = new Member();
        member.setName(dto.getName());
        member.setMembershipDate(dto.getMembershipDate());
        return member;
    }

    @Override
    public List<MemberDTO> getAllMembers() {
        return memberRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MemberDTO getMemberById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Member with ID " + id + " not found"));
        return toDTO(member);
    }

    @Override
    public MemberDTO createMember(MemberDTO memberDTO) {
        memberDTOValidator.validate(memberDTO);
        Member member = toEntity(memberDTO);
        member.setMembershipDate(java.time.LocalDate.now());
        Member saved = memberRepository.save(member);
        return toDTO(saved);
    }

    @Override
    public MemberDTO updateMember(Long id, MemberDTO memberDTO) {
        memberDTOValidator.validate(memberDTO);
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Member with ID " + id + " not found"));
        member.setName(memberDTO.getName());
        Member updated = memberRepository.save(member);
        return toDTO(updated);
    }

    @Override
    public void deleteMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Member with ID " + id + " not found"));

        boolean hasActiveBorrows = borrowRepository.existsByMemberIdAndReturnDateIsNull(id);
        if (hasActiveBorrows) {
            throw new DeleteConstraintException("Cannot delete member with active borrowed books.");
        }
        memberRepository.delete(member);
    }

}
