package com.vincent.domain.member.controller;

import com.vincent.apipayload.ApiResponse;
import com.vincent.config.security.principal.PrincipalDetails;
import com.vincent.domain.member.controller.dto.MemberRequestDto;
import com.vincent.domain.member.controller.dto.MemberResponseDto;
import com.vincent.domain.member.converter.MemberConverter;
import com.vincent.domain.member.service.MemberService;
import com.vincent.domain.member.service.MemberService.LoginResult;
import com.vincent.domain.member.service.MemberService.ReissueResult;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/v1")
public class MemberController {

    private final MemberService memberService;


    @Operation(summary = "로그인 하기")
    @PostMapping("/login")
    public ApiResponse<MemberResponseDto.Login> login(@RequestBody MemberRequestDto.Login request) {
        LoginResult result = memberService.login(request.getEmail());
        return ApiResponse.onSuccess(
                MemberConverter.toLoginResponse(result.getAccessToken(), result.getRefreshToken()));
    }

    @Operation(summary = "Refresh Token 재발급 하기")
    @PostMapping("/reissue")
    public ApiResponse<MemberResponseDto.Reissue> reissue(
            @RequestBody MemberRequestDto.Reissue request) {
        ReissueResult result = memberService.reissue(request.getRefreshToken());
        return ApiResponse.onSuccess(
                MemberConverter.toReissueResponse(result.getAccessToken(),
                        result.getRefreshToken()));
    }


    @Operation(summary = "로그아웃 하기")
    @PostMapping("/logout")
    public ApiResponse<?> logout(@RequestBody MemberRequestDto.Logout request) {
        memberService.logout(request.getAccessToken(), request.getRefreshToken());
        return ApiResponse.onSuccess(null);
    }

    @Operation(summary = "탈퇴 하기")
    @DeleteMapping("/withdraw")
    public ApiResponse<?> withdraw(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Long memberId = principal.getMemberId();
        memberService.withdraw(memberId);
        return ApiResponse.onSuccess(null);
    }

}
