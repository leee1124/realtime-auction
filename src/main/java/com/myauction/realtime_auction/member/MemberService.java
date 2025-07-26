package com.myauction.realtime_auction.member;

import com.myauction.realtime_auction.member.dto.MemberSignUpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long signUp(MemberSignUpRequest requestDto) {

        if (memberRepository.findByUsername(requestDto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 ID 입니다.");
        }

        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        Member member = new Member(
                requestDto.getUsername(),
                encodedPassword,
                requestDto.getNickname()
        );

        Member savedMember = memberRepository.save(member);
        return savedMember.getId();
    }
}