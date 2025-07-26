package com.myauction.realtime_auction.member;

import com.myauction.realtime_auction.member.dto.MemberSignUpRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody MemberSignUpRequest requestDto) {
        @Valid
        Long memberId = memberService.signUp(requestDto);
        return ResponseEntity.ok("회원가입 성공! 회원 ID: " + memberId);
    }
}