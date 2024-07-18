package com.vincent.domain.member.controller;

import com.vincent.apipayload.ApiResponse;
import com.vincent.domain.member.controller.dto.MemberRequestDto;
import com.vincent.domain.member.controller.dto.MemberResponseDto;
import com.vincent.domain.member.converter.MemberConverter;
import com.vincent.domain.member.service.MemberService;
import com.vincent.domain.member.service.MemberService.LoginResult;
import com.vincent.domain.member.service.MemberService.ReissueResult;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
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

    @PostMapping("/login")
    public ApiResponse<MemberResponseDto.Login> login(@RequestBody MemberRequestDto.Login request) {
        LoginResult result = memberService.login(request.getEmail());
        return ApiResponse.onSuccess(
            MemberConverter.toLoginResponse(result.getAccessToken(), result.getRefreshToken()));
    }

    @PostMapping("/reissue")
    public ApiResponse<MemberResponseDto.Reissue> reissue(
        @RequestBody MemberRequestDto.Reissue request) {
        ReissueResult result = memberService.reissue(request.getRefreshToken());
        return ApiResponse.onSuccess(
            MemberConverter.toReissueResponse(result.getAccessToken(), result.getRefreshToken()));
    }

}
