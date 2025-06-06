package com.example.library.service;

import com.example.library.dto.MemberDTO;

import java.util.List;

public interface MemberService {
    List<MemberDTO> getAllMembers();
    MemberDTO getMemberById(Long id);
    MemberDTO createMember(MemberDTO memberDTO);
    MemberDTO updateMember(Long id, MemberDTO memberDTO);
    void deleteMember(Long id);
}
