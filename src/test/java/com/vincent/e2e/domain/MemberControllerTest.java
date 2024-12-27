//package com.vincent.e2e.domain;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//import com.vincent.apipayload.ApiResponse;
//import com.vincent.domain.member.controller.dto.MemberRequestDto;
//import com.vincent.domain.member.controller.dto.MemberResponseDto;
//import com.vincent.domain.member.controller.dto.MemberResponseDto.Login;
//import com.vincent.domain.member.entity.Member;
//import com.vincent.domain.member.entity.enums.SocialType;
//import com.vincent.domain.member.repository.MemberRepository;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.test.context.ActiveProfiles;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ActiveProfiles("test")
//public class MemberControllerTest {
//    @Autowired
//    private TestRestTemplate restTemplate;
//
//    @LocalServerPort
//    private int port;
//
//    @Autowired
//    private MemberRepository memberRepository;
//
//    @Test
//    void 로그인() {
//        // Given
//        MemberRequestDto.Login requestDto = new MemberRequestDto.Login("test@gmail.com", SocialType.KAKAO);
//
//        // When
//        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(
//            "/v1/login", requestDto, ApiResponse.class
//        );
//
//        // Then
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals("COMMON200", response.getBody().getCode());
//    }
//
//}
