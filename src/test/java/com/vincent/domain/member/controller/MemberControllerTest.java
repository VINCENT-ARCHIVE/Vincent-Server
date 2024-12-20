package com.vincent.domain.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincent.apipayload.ApiResponse;
import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.config.security.principal.PrincipalDetails;
import com.vincent.domain.member.controller.dto.MemberRequestDto;
import com.vincent.domain.member.controller.dto.MemberResponseDto.Login;
import com.vincent.domain.member.controller.dto.MemberResponseDto.Reissue;
import com.vincent.domain.member.entity.Member;
import com.vincent.domain.member.entity.enums.SocialType;
import com.vincent.domain.member.service.MemberService;
import com.vincent.domain.member.service.MemberService.LoginResult;
import com.vincent.domain.member.service.MemberService.ReissueResult;
import com.vincent.exception.handler.ErrorHandler;
import com.vincent.exception.handler.JwtExpiredHandler;
import com.vincent.exception.handler.JwtInvalidHandler;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

    @Mock
    private MemberService memberService;

    @InjectMocks
    private MemberController memberController;


    @Test
    @DisplayName("로그인_카카오")
    public void 로그인_카카오() {
        //given
        MemberRequestDto.Login request = new MemberRequestDto.Login("test@gmail.com", SocialType.KAKAO);
        MemberService.LoginResult result = new MemberService.LoginResult("accessToken", "refreshToken");

        //when
        when(memberService.login(request.getEmail(), request.getSocialType())).thenReturn(result);
        ApiResponse<Login> response = memberController.login(request);

        //then
        verify(memberService).login(request.getEmail(), request.getSocialType());
        Assertions.assertThat(response.getIsSuccess()).isTrue();
        Assertions.assertThat(response.getCode()).isEqualTo("COMMON200");
        Assertions.assertThat(response.getResult().getAccessToken()).isEqualTo("accessToken");
        Assertions.assertThat(response.getResult().getRefreshToken()).isEqualTo("refreshToken");
    }


    @Test
    @DisplayName("토큰 재발급 성공")
    public void 토큰재발급성공() {
        //given
        MemberRequestDto.Reissue request = new MemberRequestDto.Reissue("refreshToken");
        ReissueResult result = new ReissueResult("newAccessToken", "newRefreshToken");

        //when
        when(memberService.reissue(request.getRefreshToken())).thenReturn(result);
        ApiResponse<Reissue> response = memberController.reissue(request);

        //then
        verify(memberService).reissue(request.getRefreshToken());
        Assertions.assertThat(response.getIsSuccess()).isTrue();
        Assertions.assertThat(response.getCode()).isEqualTo("COMMON200");
        Assertions.assertThat(response.getResult().getAccessToken()).isEqualTo("newAccessToken");
        Assertions.assertThat(response.getResult().getRefreshToken()).isEqualTo("newRefreshToken");
    }

//    @Test
//    @DisplayName("토큰 재발급 실패 - 멤버 없음")
//    public void 토큰재발급실패_멤버없음() {
//        //given
//        MemberRequestDto.Reissue request = new MemberRequestDto.Reissue("refreshToken");
//
//        //when
//        when(memberService.reissue(request.getRefreshToken())).thenThrow(new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
//        ApiResponse<?> response = memberController.reissue(request);
//
//        //then
//        Assertions.assertThat(response.getIsSuccess()).isFalse();
//        Assertions.assertThat(response.getCode()).isEqualTo("MEMBER4001");
//        Assertions.assertThat(response.getMessage()).isEqualTo("회원 정보를 찾을 수 없습니다.");
//    }
//
//    @Test
//    @DisplayName("토큰 재발급 실패 - 토큰 오류(Invalid)")
//    public void 토큰재발급실패_Invalid() {
//        //given
//        MemberRequestDto.Reissue request = new MemberRequestDto.Reissue("Invalid refreshToken");
//
//        //when
//        when(memberService.reissue(request.getRefreshToken())).thenThrow(new ErrorHandler(ErrorStatus.JWT_REFRESH_TOKEN_EXPIRED));
//        ApiResponse<?> response = memberController.reissue(request);
//
//        //then
//        Assertions.assertThat(response.getIsSuccess()).isFalse();
//        Assertions.assertThat(response.getCode()).isEqualTo("COMMON500");
//        Assertions.assertThat(response.getMessage()).isEqualTo("서버 에러, 관리자에게 문의 바랍니다.");
//    }
//
//    @Test
//    @DisplayName("토큰 재발급 실패 - 토큰 오류(Expired)")
//    public void 토큰재발급실패_Expired() {
//        //given
//        MemberRequestDto.Reissue request = new MemberRequestDto.Reissue("Expired refreshToken");
//
//        //when
//        when(memberService.reissue(request.getRefreshToken())).thenThrow(new JwtExpiredHandler("Expired Token Exception"));
//        ApiResponse<?> response = memberController.reissue(request);
//
//        //then
//        Assertions.assertThat(response.getIsSuccess()).isFalse();
//        Assertions.assertThat(response.getCode()).isEqualTo("COMMON500");
//        Assertions.assertThat(response.getMessage()).isEqualTo("서버 에러, 관리자에게 문의 바랍니다.");
//    }

    @Test
    @DisplayName("로그아웃")
    public void 로그아웃() {
        //given
        MemberRequestDto.Logout request = new MemberRequestDto.Logout("accessToken", "refreshToken");

        //when
        ApiResponse<?> response = memberController.logout(request);

        //then
        verify(memberService).logout(request.getAccessToken(), request.getRefreshToken());
        Assertions.assertThat(response.getIsSuccess()).isTrue();
        Assertions.assertThat(response.getCode()).isEqualTo("COMMON200");
    }


//    @Test
//    @DisplayName("로그인_카카오")
//    public void 로그인_카카오() throws Exception {
//        //given
//        MemberRequestDto.Login request = new MemberRequestDto.Login("test@gmail.com", SocialType.KAKAO);
//        MemberService.LoginResult result = new MemberService.LoginResult("accessToken",
//            "refreshToken");
//
//        //when
//        when(memberService.login(request.getEmail(), request.getSocialType())).thenReturn(result);
//        ResultActions resultActions = mockMvc.perform(post("/v1/login")
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(objectMapper.writeValueAsString(request)));
//
//        //then
//        resultActions.andExpect(status().isOk())
//            .andExpect(jsonPath("$.isSuccess").value(true))
//            .andExpect(jsonPath("$.code").value("COMMON200"))
//            .andExpect(jsonPath("message").value("성공입니다"))
//            .andExpect(jsonPath("$.result.accessToken").value("accessToken"))
//            .andExpect(jsonPath("$.result.refreshToken").value("refreshToken"));
//    }
//
//    @Test
//    @DisplayName("로그인_애플")
//    public void 로그인_애플() throws Exception {
//        //given
//        MemberRequestDto.Login request = new MemberRequestDto.Login("test@gmail.com", SocialType.APPLE);
//        MemberService.LoginResult result = new MemberService.LoginResult("accessToken",
//            "refreshToken");
//
//        //when
//        when(memberService.login(request.getEmail(), request.getSocialType())).thenReturn(result);
//        ResultActions resultActions = mockMvc.perform(post("/v1/login")
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(objectMapper.writeValueAsString(request)));
//
//        //then
//        resultActions.andExpect(status().isOk())
//            .andExpect(jsonPath("$.isSuccess").value(true))
//            .andExpect(jsonPath("$.code").value("COMMON200"))
//            .andExpect(jsonPath("message").value("성공입니다"))
//            .andExpect(jsonPath("$.result.accessToken").value("accessToken"))
//            .andExpect(jsonPath("$.result.refreshToken").value("refreshToken"));
//    }
//
//    @Test
//    @DisplayName("토큰 재발급 성공")
//    public void 토큰재발급성공() throws Exception {
//        //given
//        MemberRequestDto.Reissue request = new MemberRequestDto.Reissue("refreshToken");
//        ReissueResult result = new ReissueResult("newAccessToken", "newRefreshToken");
//
//        //when
//        when(memberService.reissue(request.getRefreshToken())).thenReturn(result);
//        ResultActions resultActions = mockMvc.perform(post("/v1/reissue")
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(objectMapper.writeValueAsString(request)));
//
//        //then
//        resultActions.andExpect(status().isOk())
//            .andExpect(jsonPath("$.isSuccess").value(true))
//            .andExpect(jsonPath("$.code").value("COMMON200"))
//            .andExpect(jsonPath("message").value("성공입니다"))
//            .andExpect(jsonPath("$.result.accessToken").value("newAccessToken"))
//            .andExpect(jsonPath("$.result.refreshToken").value("newRefreshToken"));
//    }
//
//    @Test
//    @DisplayName("토큰 재발급 실패 - 멤버 없음")
//    public void 토큰재발급실패_멤버없음() throws Exception {
//        //given
//        MemberRequestDto.Reissue request = new MemberRequestDto.Reissue("refreshToken");
//
//        //when
//        when(memberService.reissue(request.getRefreshToken())).thenThrow(new ErrorHandler(
//            ErrorStatus.MEMBER_NOT_FOUND));
//
//        ResultActions resultActions = mockMvc.perform(post("/v1/reissue")
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(objectMapper.writeValueAsString(request)));
//
//        //then
//        resultActions
//            .andExpect(jsonPath("$.isSuccess").value(false))
//            .andExpect(jsonPath("$.code").value("MEMBER4001"))
//            .andExpect(jsonPath("message").value("회원 정보를 찾을 수 없습니다."));
//    }
//
//    @Test
//    @DisplayName("토큰 재발급 실패 - 토큰 오류(Invalid)")
//    public void 토큰재발급실패_Invalid() throws Exception {
//        //given
//        MemberRequestDto.Reissue request = new MemberRequestDto.Reissue("Invalid refreshToken");
//
//        //when
//        when(memberService.reissue(request.getRefreshToken())).thenThrow(
//            new JwtInvalidHandler("Invalid Token Exception"));
//
//        ResultActions resultActions = mockMvc.perform(post("/v1/reissue")
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(objectMapper.writeValueAsString(request)));
//
//        //then
//        resultActions
//            .andExpect(jsonPath("$.isSuccess").value(false))
//            .andExpect(jsonPath("$.code").value("COMMON500"))
//            .andExpect(jsonPath("message").value("서버 에러, 관리자에게 문의 바랍니다."));
//    }
//
//    @Test
//    @DisplayName("토큰 재발급 실패 - 토큰 오류(Expired)")
//    public void 토큰재발급실패_Expired() throws Exception {
//        //given
//        MemberRequestDto.Reissue request = new MemberRequestDto.Reissue("Expired refreshToken");
//
//        //when
//        when(memberService.reissue(request.getRefreshToken())).thenThrow(
//            new JwtExpiredHandler("Expired Token Exception"));
//
//        ResultActions resultActions = mockMvc.perform(post("/v1/reissue")
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(objectMapper.writeValueAsString(request)));
//
//        //then
//        resultActions
//            .andExpect(jsonPath("$.isSuccess").value(false))
//            .andExpect(jsonPath("$.code").value("COMMON500"))
//            .andExpect(jsonPath("message").value("서버 에러, 관리자에게 문의 바랍니다."));
//    }
//
//    @Test
//    @DisplayName("토큰 재발급 실패 - 탈취 위험")
//    public void 토큰재발급실패_탈취위험() throws Exception {
//        //given
//        MemberRequestDto.Reissue request = new MemberRequestDto.Reissue("previous refreshToken");
//
//        //when
//        when(memberService.reissue(request.getRefreshToken())).thenThrow(
//            new ErrorHandler(ErrorStatus.ANOTHER_USER));
//
//        ResultActions resultActions = mockMvc.perform(post("/v1/reissue")
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(objectMapper.writeValueAsString(request)));
//
//        //then
//        resultActions
//            .andExpect(jsonPath("$.isSuccess").value(false))
//            .andExpect(jsonPath("$.code").value("COMMON404"))
//            .andExpect(jsonPath("message").value("토큰 탈취 위협이 있습니다. 다시 로그인하시기 바랍니다."));
//    }
//
//    @Test
//    @DisplayName("토큰 재발급 실패 - 토큰 만료")
//    public void 토큰재발급실퍠_토큰만료() throws Exception {
//        //given
//        MemberRequestDto.Reissue request = new MemberRequestDto.Reissue("refreshToken");
//
//        //when
//        when(memberService.reissue(request.getRefreshToken())).thenThrow(
//            new ErrorHandler(ErrorStatus.JWT_REFRESH_TOKEN_EXPIRED));
//
//        ResultActions resultActions = mockMvc.perform(post("/v1/reissue")
//            .contentType(MediaType.APPLICATION_JSON)
//            .content(objectMapper.writeValueAsString(request)));
//
//        //then
//        resultActions
//            .andExpect(jsonPath("$.isSuccess").value(false))
//            .andExpect(jsonPath("$.code").value("JWT4003"))
//            .andExpect(jsonPath("message").value("리프레시 토큰이 만료되었습니다. 다시 로그인하시기 바랍니다."));
//    }
//
//    @Test
//    @DisplayName("로그아웃")
//    public void 로그아웃() throws Exception {
//        //given
//        MemberRequestDto.Logout request = new MemberRequestDto.Logout("accessToken",
//            "refreshToken");
//
//        //when
//        doNothing().when(memberService).logout(request.getAccessToken(), request.getRefreshToken());
//
//        //then
//        mockMvc.perform(post("/v1/logout")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(request)))
//            .andExpect(status().isOk());
//
//        verify(memberService, times(1)).logout(request.getAccessToken(), request.getRefreshToken());
//    }

//    @Test
//    @DisplayName("회원탈퇴 - 성공")
//    @WithMockUser(username = "1")
//    public void 회원탈퇴() throws Exception {
//        // given
//        Long memberId = 1L;
//
//        doNothing().when(memberService).withdraw(memberId);
//
//        // when & then
//        ResultActions resultActions = mockMvc.perform(delete("/v1/withdraw")
//                .contentType(MediaType.APPLICATION_JSON))
//            .andExpect(status().isOk());
//
//        resultActions.andExpect(status().isOk())
//            .andExpect(jsonPath("$.isSuccess").value(true))
//            .andExpect(jsonPath("$.code").value("COMMON200"))
//            .andExpect(jsonPath("$.message").value("성공입니다"));
//    }


}
