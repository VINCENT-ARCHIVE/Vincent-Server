package com.vincent.domain.bookmark.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincent.domain.bookmark.service.BookmarkService;
import com.vincent.domain.member.controller.MemberController;
import com.vincent.domain.member.controller.dto.MemberRequestDto;
import com.vincent.domain.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookmarkController.class)
public class BookmarkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookmarkService bookmarkService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("북마크 추가")
    @WithMockUser(username = "1")
    public void Addition() throws Exception {
        // 테스트 데이터 준비
        Long socketId = 1L;
        Long memberId = 1L; // 인증된 사용자 ID
        LocalDateTime accessExpireTime = LocalDateTime.now().plusMinutes(30);
        BookmarkService.AdditionResult result = new BookmarkService.AdditionResult(socketId);

        // Mocking 서비스 응답
        when(bookmarkService.Addition(eq(socketId), eq(memberId))).thenReturn(result);

        // 요청 실행
        ResultActions resultActions = mockMvc.perform(post("/v1/bookmark/1")
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf()));

        // 응답 검증
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("COMMON200"))
                .andExpect(jsonPath("$.message").value("성공입니다"))
                .andExpect(jsonPath("$.result.bookmarkId").value(result.getBookmarkId()));
    }
}