package com.vincent.domain.feedback.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.vincent.apipayload.ApiResponse;
import com.vincent.config.security.principal.PrincipalDetails;
import com.vincent.domain.feedback.controller.dto.FeedbackRequestDto;
import com.vincent.domain.feedback.service.FeedbackService;
import com.vincent.domain.member.entity.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
public class FeedbackControllerTest {

    @Mock
    private FeedbackService feedbackService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private FeedbackController feedbackController;

    private PrincipalDetails principalDetails;

    @BeforeEach
    public void setUp() {
        Member member = Member.builder().id(1L).build();
        principalDetails = new PrincipalDetails(member);
    }

    @Test
    public void 피드백_생성_성공() {
        when(authentication.getPrincipal()).thenReturn(principalDetails);
        FeedbackRequestDto.addFeedbackDto request = FeedbackRequestDto.addFeedbackDto.builder()
                .contents("test")
                .build();

        ApiResponse<?> response = feedbackController.addFeedback(request, authentication);
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getIsSuccess()).isTrue();
        Assertions.assertThat(response.getCode()).isEqualTo("COMMON200");
        Assertions.assertThat(response.getMessage()).isEqualTo("성공입니다");
    }




//    @Test
//    @WithMockUser(username = "1")
//    public void 피드백생성실패_멤버없음() throws Exception {
//
//        //requestDto 생성
//        FeedbackRequestDto.addFeedbackDto request = FeedbackRequestDto.addFeedbackDto.builder()
//                .contents("test contents")
//                .build();
//
//        // feedbackService.addFeedback 호출 시 예외를 던지도록 설정
//        doThrow(new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND)).when(feedbackService)
//                .addFeedback(eq(request.getContents()), eq(memberId));
//
//        ResultActions resultActions = mockMvc.perform(post("/v1/feedback")
//                .content(new ObjectMapper().writeValueAsString(request))
//                .contentType(MediaType.APPLICATION_JSON)
//                .with(SecurityMockMvcRequestPostProcessors.csrf()));
//
//        resultActions.andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.isSuccess").value(false))
//                .andExpect(jsonPath("$.code").value(
//                        ErrorStatus.MEMBER_NOT_FOUND.getReason().getCode()))
//                .andExpect(jsonPath("$.message").value(
//                        ErrorStatus.MEMBER_NOT_FOUND.getReason().getMessage()));
//
//
//    }


}
