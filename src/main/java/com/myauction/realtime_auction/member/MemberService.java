package com.myauction.realtime_auction.member;

import com.myauction.realtime_auction.global.config.jwt.JwtTokenProvider;
import com.myauction.realtime_auction.member.domain.Member;
import com.myauction.realtime_auction.member.dto.request.LoginRequestDTO;
import com.myauction.realtime_auction.member.dto.request.MemberSignUpRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Transactional
    public Long signUp(MemberSignUpRequestDTO requestDto) {

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

    @Transactional
    public String login(LoginRequestDTO requestDTO){
        Member member = memberRepository.findByUsername(requestDTO.getUsername()).orElseThrow(()->
                new BadCredentialsException("ID 또는 비밀번호가 일치하지 않습니다."));
        if(!passwordEncoder.matches(requestDTO.getPassword(), member.getPassword())){
            throw new BadCredentialsException("ID 또는 비밀번호가 일치하지 않습니다.");
        }

        UserDetails userDetails = new User(member.getUsername(), "", Collections.emptyList());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());

        return jwtTokenProvider.generateToken(authentication);
    }

}