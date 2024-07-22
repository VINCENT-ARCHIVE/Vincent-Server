package com.vincent.domain.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.config.security.provider.JwtProvider;
import com.vincent.domain.member.controller.dto.MemberRequestDto;
import com.vincent.domain.member.controller.dto.MemberResponseDto;
import com.vincent.domain.member.controller.dto.MemberResponseDto.Reissue;
import com.vincent.domain.member.service.MemberService;
import com.vincent.domain.member.service.MemberService.ReissueResult;
import com.vincent.exception.handler.ErrorHandler;
import com.vincent.exception.handler.JwtExpiredHandler;
import com.vincent.exception.handler.JwtInvalidHandler;
import com.vincent.redis.service.RedisService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(MemberController.class)
@AutoConfigureMockMvc(addFilters = false)
@MockBean(JpaMetamodelMappingContext.class)
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
        //given
        MemberRequestDto.Login request = new MemberRequestDto.Login("test@gmail.com");
        MemberService.LoginResult result = new MemberService.LoginResult("accessToken",
            "refreshToken");

        //when
        when(memberService.login(request.getEmail())).thenReturn(result);
        ResultActions resultActions = mockMvc.perform(post("/v1/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));

        //then
        resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("$.isSuccess").value(true))
            .andExpect(jsonPath("$.code").value("COMMON200"))
            .andExpect(jsonPath("message").value("성공입니다"))
            .andExpect(jsonPath("$.result.accessToken").value("accessToken"))
            .andExpect(jsonPath("$.result.refreshToken").value("refreshToken"));
    }

    @Test
    @DisplayName("토큰 재발급 성공")
    public void 토큰재발급성공() throws Exception {
        //given
        MemberRequestDto.Reissue request = new MemberRequestDto.Reissue("refreshToken");
        ReissueResult result = new ReissueResult("newAccessToken", "newRefreshToken");

        //when
        when(memberService.reissue(request.getRefreshToken())).thenReturn(result);
        ResultActions resultActions = mockMvc.perform(post("/v1/reissue")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));

        //then
        resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("$.isSuccess").value(true))
            .andExpect(jsonPath("$.code").value("COMMON200"))
            .andExpect(jsonPath("message").value("성공입니다"))
            .andExpect(jsonPath("$.result.accessToken").value("newAccessToken"))
            .andExpect(jsonPath("$.result.refreshToken").value("newRefreshToken"));
    }

    @Test
    @DisplayName("토큰 재발급 실패 - 멤버 없음")
    public void 토큰재발급실패_멤버없음() throws Exception {
        //given
        MemberRequestDto.Reissue request = new MemberRequestDto.Reissue("refreshToken");

        //when
        when(memberService.reissue(request.getRefreshToken())).thenThrow(new ErrorHandler(
            ErrorStatus.MEMBER_NOT_FOUND));

        ResultActions resultActions = mockMvc.perform(post("/v1/reissue")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));

        //then
        resultActions
            .andExpect(jsonPath("$.isSuccess").value(false))
            .andExpect(jsonPath("$.code").value("MEMBER4001"))
            .andExpect(jsonPath("message").value("회원 정보를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("토큰 재발급 실패 - 토큰 오류(Invalid)")
    public void 토큰재발급실패_Invalid() throws Exception {
        //given
        MemberRequestDto.Reissue request = new MemberRequestDto.Reissue("Invalid refreshToken");

        //when
        when(memberService.reissue(request.getRefreshToken())).thenThrow(
            new JwtInvalidHandler("Invalid Token Exception"));

        ResultActions resultActions = mockMvc.perform(post("/v1/reissue")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));

        //then
        resultActions
            .andExpect(jsonPath("$.isSuccess").value(false))
            .andExpect(jsonPath("$.code").value("COMMON500"))
            .andExpect(jsonPath("message").value("서버 에러, 관리자에게 문의 바랍니다."));
    }

    @Test
    @DisplayName("토큰 재발급 실패 - 토큰 오류(Expired)")
    public void 토큰재발급실패_Expired() throws Exception {
        //given
        MemberRequestDto.Reissue request = new MemberRequestDto.Reissue("Expired refreshToken");

        //when
        when(memberService.reissue(request.getRefreshToken())).thenThrow(
            new JwtExpiredHandler("Expired Token Exception"));

        ResultActions resultActions = mockMvc.perform(post("/v1/reissue")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));

        //then
        resultActions
            .andExpect(jsonPath("$.isSuccess").value(false))
            .andExpect(jsonPath("$.code").value("COMMON500"))
            .andExpect(jsonPath("message").value("서버 에러, 관리자에게 문의 바랍니다."));
    }

    @Test
    @DisplayName("토큰 재발급 실패 - 탈취 위험")
    public void 토큰재발급실패_탈취위험() throws Exception {
        //given
        MemberRequestDto.Reissue request = new MemberRequestDto.Reissue("previous refreshToken");

        //when
        when(memberService.reissue(request.getRefreshToken())).thenThrow(
            new ErrorHandler(ErrorStatus.ANOTHER_USER));

        ResultActions resultActions = mockMvc.perform(post("/v1/reissue")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));

        //then
        resultActions
            .andExpect(jsonPath("$.isSuccess").value(false))
            .andExpect(jsonPath("$.code").value("COMMON404"))
            .andExpect(jsonPath("message").value("토큰 탈취 위협이 있습니다. 다시 로그인하시기 바랍니다."));
    }

    @Test
    @DisplayName("토큰 재발급 실패 - 토큰 만료")
    public void 토큰재발급실퍠_토큰만료() throws Exception {
        //given
        MemberRequestDto.Reissue request = new MemberRequestDto.Reissue("refreshToken");

        //when
        when(memberService.reissue(request.getRefreshToken())).thenThrow(
            new ErrorHandler(ErrorStatus.JWT_REFRESH_TOKEN_EXPIRED));

        ResultActions resultActions = mockMvc.perform(post("/v1/reissue")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));

        //then
        resultActions
            .andExpect(jsonPath("$.isSuccess").value(false))
            .andExpect(jsonPath("$.code").value("JWT4003"))
            .andExpect(jsonPath("message").value("리프레시 토큰이 만료되었습니다. 다시 로그인하시기 바랍니다."));
    }

    @Test
    @DisplayName("로그아웃")
    public void 로그아웃() throws Exception {
        //given
        MemberRequestDto.Logout request = new MemberRequestDto.Logout("accessToken",
            "refreshToken");

        //when
        doNothing().when(memberService).logout(request.getAccessToken(), request.getRefreshToken());

        //then
        mockMvc.perform(post("/v1/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());

        verify(memberService, times(1)).logout(request.getAccessToken(), request.getRefreshToken());
    }
}
