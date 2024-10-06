package com.vincent.domain.feedback.controller;

import com.vincent.apipayload.ApiResponse;
import com.vincent.config.security.principal.PrincipalDetails;
import com.vincent.domain.feedback.controller.dto.FeedbackRequestDto;
import com.vincent.domain.feedback.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/v1")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @Operation(summary = "피드백 생성하기")
    @PostMapping("/feedback")
    public ApiResponse<?> addFeedback(@RequestBody @Valid FeedbackRequestDto.addFeedbackDto request,
        Authentication authentication) {

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        Long memberId = principalDetails.getMemberId();
        feedbackService.addFeedback(request.getContents(), memberId);
        return ApiResponse.onSuccess(null);
    }

}
