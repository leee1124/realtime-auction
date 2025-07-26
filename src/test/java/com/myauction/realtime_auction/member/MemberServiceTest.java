package com.myauction.realtime_auction.member;

import com.myauction.realtime_auction.global.config.jwt.JwtTokenProvider;
import com.myauction.realtime_auction.member.domain.Member;
import com.myauction.realtime_auction.member.dto.request.LoginRequestDTO;
import com.myauction.realtime_auction.member.dto.request.MemberSignUpRequestDTO;
import com.myauction.realtime_auction.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional // 각 테스트 후 DB 롤백으로 테스트 간 독립성 보장
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp(){
        MemberSignUpRequestDTO signupDTO = new MemberSignUpRequestDTO("testuser", "password123", "테스트유저");
        memberService.signUp(signupDTO);
    }

    @Test
    @DisplayName("회원가입에 성공한다")
    void signUp_success() {
        // given: 회원가입을 위한 DTO 준비
        MemberSignUpRequestDTO requestDto = new MemberSignUpRequestDTO("newUser", "password123", "새로운유저");

        // when: 회원가입 서비스 호출
        Long savedMemberId = memberService.signUp(requestDto);

        // then: 반환된 ID가 null이 아니어야 하며, DB에서 조회가 가능해야 한다
        assertThat(savedMemberId).isNotNull();

        Member foundMember = memberRepository.findById(savedMemberId)
                .orElseThrow(() -> new AssertionError("저장된 회원을 찾을 수 없습니다."));

        assertThat(foundMember.getUsername()).isEqualTo("newUser");
        assertThat(foundMember.getNickname()).isEqualTo("새로운유저");
        // 비밀번호가 암호화되어 저장되었는지 확인 (원본과 달라야 함)
        assertThat(foundMember.getPassword()).isNotEqualTo("password123");
    }

    @Test
    @DisplayName("이미 존재하는 아이디로 회원가입 시 IllegalArgumentException 예외가 발생한다")
    void signUp_fail_with_duplicate_username() {
        // given: 먼저 사용자를 한 명 가입시켜 둠
        memberService.signUp(new MemberSignUpRequestDTO("existingUser", "password123", "기존유저"));

        // when: 동일한 아이디로 다시 회원가입을 시도할 DTO 준비
        MemberSignUpRequestDTO duplicateRequestDto = new MemberSignUpRequestDTO("existingUser", "anotherPassword", "다른유저");

        // then: signUp 메소드를 호출하면 예외가 발생해야 한다
        assertThatThrownBy(() -> memberService.signUp(duplicateRequestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 ID 입니다."); // 서비스 코드의 예외 메시지와 일치하는지 확인
    }

    @Test
    @DisplayName("로그인 성공 시 유효한 JWT 토큰을 반환한다")
    void login_success() {
        // given: 올바른 로그인 정보
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("testuser", "password123");

        // when: 로그인을 시도하면
        String token = memberService.login(loginRequestDTO);

        // then: 반환된 토큰은 유효해야 하며, 올바른 사용자 정보를 담고 있어야 한다.
        // 더 이상 고정된 문자열을 비교하지 않습니다.

        // 1. 토큰이 비어있지 않은지 기본 검증
        assertThat(token).isNotBlank();

        // 2. JwtTokenProvider를 이용해 토큰이 유효한지 검증
        assertThat(jwtTokenProvider.validateToken(token)).isTrue();

        // 3. 토큰에서 사용자 정보를 꺼내서, 로그인한 사용자와 일치하는지 검증
        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        assertThat(authentication.getName()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인 시 BadCredentialsException 예외가 발생한다")
    void login_fail_with_wrong_password() {
        // given: 비밀번호가 틀린 로그인 정보
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("testuser", "wrongpassword");

        // when, then: 이 테스트는 로직 변경이 없습니다. 그대로 성공합니다.
        assertThatThrownBy(() -> memberService.login(loginRequestDTO))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("ID 또는 비밀번호가 일치하지 않습니다.");
    }

    @Test
    @DisplayName("존재하지 않는 아이디로 로그인 시 BadCredentialsException 예외가 발생한다")
    void login_fail_with_non_existent_username() {
        // given: 존재하지 않는 아이디 정보
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("nonexistentuser", "password123");

        // when, then: 이 테스트도 로직 변경이 없습니다. 그대로 성공합니다.
        assertThatThrownBy(() -> memberService.login(loginRequestDTO))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("ID 또는 비밀번호가 일치하지 않습니다.");
    }
}