package com.vincent.domain.bookmark.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.domain.feedback.controller.FeedbackController;
import com.vincent.domain.feedback.controller.dto.FeedbackRequestDto;
import com.vincent.domain.feedback.service.FeedbackService;
import com.vincent.exception.handler.ErrorHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(controllers = FeedbackController.class)
@MockBean(JpaMetamodelMappingContext.class)
public class FeedbackControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private FeedbackService feedbackService;

  Long memberId = 1L;

  @Test
  @WithMockUser(username = "1")
  public void 피드백생성성공() throws Exception {

    //requestDto 생성
    FeedbackRequestDto.addFeedbackDto request = FeedbackRequestDto.addFeedbackDto.builder()
        .contents("test contents")
        .build();



    ResultActions resultActions = mockMvc.perform(post("/v1/feedback")
        .content(new ObjectMapper().writeValueAsString(request))
        .contentType(MediaType.APPLICATION_JSON)
        .with(SecurityMockMvcRequestPostProcessors.csrf()));


    resultActions.andExpect(status().isOk())
        .andExpect(jsonPath("$.isSuccess").value(true))
        .andExpect(jsonPath("$.code").value("COMMON200"))
        .andExpect(jsonPath("$.message").value("성공입니다"));

    verify(feedbackService, times(1)).addFeedback(eq(request.getContents()), eq(memberId));



  }

  @Test
  @WithMockUser(username = "1")
  public void 피드백생성실패_멤버없음() throws Exception {

    //requestDto 생성
    FeedbackRequestDto.addFeedbackDto request = FeedbackRequestDto.addFeedbackDto.builder()
        .contents("test contents")
        .build();


    // feedbackService.addFeedback 호출 시 예외를 던지도록 설정
    doThrow(new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND)).when(feedbackService).addFeedback(eq(request.getContents()), eq(memberId));

    ResultActions resultActions = mockMvc.perform(post("/v1/feedback")
        .content(new ObjectMapper().writeValueAsString(request))
        .contentType(MediaType.APPLICATION_JSON)
        .with(SecurityMockMvcRequestPostProcessors.csrf()));


    resultActions.andExpect(status().isNotFound())
        .andExpect(jsonPath("$.isSuccess").value(false))
        .andExpect(jsonPath("$.code").value(ErrorStatus.MEMBER_NOT_FOUND.getReason().getCode()))
        .andExpect(jsonPath("$.message").value(ErrorStatus.MEMBER_NOT_FOUND.getReason().getMessage()));




  }






}
