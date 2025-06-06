package com.example.library.validation;

import com.example.library.dto.MemberDTO;
import com.example.library.entity.Member;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

@Component
public class MemberDTOValidator {

    public void validate(MemberDTO memberDTO) {
        if (memberDTO == null) {
            throw new IllegalArgumentException("MemberDTO cannot be null");
        }

        if (!StringUtils.hasText(memberDTO.getName())) {
            throw new IllegalArgumentException("Member name must not be empty");
        }

        if (memberDTO.getMembershipDate() != null && memberDTO.getMembershipDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Membership date cannot be in the future");
        }
    }
}
