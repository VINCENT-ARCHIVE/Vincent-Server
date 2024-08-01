package com.vincent.domain.feedback.controller;

import com.vincent.apipayload.ApiResponse;
import com.vincent.config.security.principal.PrincipalDetails;
import com.vincent.domain.feedback.controller.dto.FeedbackRequestDto;
import com.vincent.domain.feedback.service.FeedbackService;
import com.vincent.domain.member.controller.dto.MemberRequestDto;
import com.vincent.domain.member.controller.dto.MemberResponseDto;
import com.vincent.domain.member.controller.dto.MemberResponseDto.Login;
import com.vincent.domain.member.converter.MemberConverter;
import com.vincent.domain.member.service.MemberService.LoginResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/v1")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping("/feedback")
    public ApiResponse<?> addFeedback(@RequestBody @Valid FeedbackRequestDto.addFeedbackDto request,
        Authentication authentication) {

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        Long memberId = principalDetails.getMemberId();
        feedbackService.addFeedback(request.getContents(), memberId);
        return ApiResponse.onSuccess(null);
    }

}
