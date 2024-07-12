package com.vincent.domain.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincent.domain.member.controller.dto.MemberRequestDto;
import com.vincent.domain.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(MemberController.class)
@AutoConfigureMockMvc(addFilters = false)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private MemberService memberService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("로그인")
    public void 로그인() throws Exception {
        MemberRequestDto.Login request = new MemberRequestDto.Login("test@gmail.com");
        LocalDateTime accessExpireTime = LocalDateTime.now().plusMinutes(30);
        MemberService.LoginResult result = new MemberService.LoginResult("accessToken", accessExpireTime);

        when(memberService.login(request.getEmail())).thenReturn(result);

        ResultActions resultActions = mockMvc.perform(post("/v1/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        String formattedExpireTime = accessExpireTime.format(formatter);

        resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("$.isSuccess").value(true))
            .andExpect(jsonPath("$.code").value("COMMON200"))
            .andExpect(jsonPath("message").value("성공입니다"))
            .andExpect(jsonPath("$.result.accessToken").value(result.getAccessToken()))
            .andExpect(jsonPath("$.result.accessExpireTime").value(formattedExpireTime));
    }
}
