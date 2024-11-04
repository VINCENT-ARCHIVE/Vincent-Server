package com.vincent.domain.socket.controller;


import com.vincent.apipayload.ApiResponse;
import com.vincent.config.security.principal.PrincipalDetails;
import com.vincent.domain.bookmark.service.BookmarkService;
import com.vincent.domain.socket.controller.dto.SocketResponseDto;
import com.vincent.domain.socket.converter.SocketConverter;
import com.vincent.domain.socket.entity.Socket;
import com.vincent.domain.socket.service.SocketService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/v1")
public class SocketController {

    private final SocketService socketService;
    private final BookmarkService bookmarkService;

    @Operation(summary = "개별 콘센트 조회하기", description = "공간 지도에서 한 콘센트의 마크를 클릭 했을 때 보여지는 정보를 조회함(콘센트 정보와 콘센트 찜 여부 함께 조회)")
    @GetMapping("/socket/{socketId}")
    public ApiResponse<SocketResponseDto.SocketInfo> socketInfo(
        @PathVariable("socketId") Long socketId,
        Authentication authentication) {

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        Long memberId = principalDetails.getMemberId();
        Boolean isBookmarkExist = bookmarkService.getBookmarkExist(socketId, memberId);
        Socket socketInfo = socketService.getSocketInfo(socketId);
        return ApiResponse.onSuccess(
            SocketConverter.toSocketInfoResponse(socketInfo, isBookmarkExist));
    }


    @Operation(summary = "층 콘센트 조회하기", description = "빌딩의 해당 층에 있는 모든 소켓의 Id와 위도, 경도를 조회함")
    @GetMapping("/socket")
    public ApiResponse<SocketResponseDto.SocketLocationList> getSocketLocationList(
        @RequestParam("buildingId") Long buildingId,
        @RequestParam("level") Integer level) {
        return ApiResponse.onSuccess((
            SocketConverter.toSocketLocationList(socketService.getSocketList(buildingId, level))));


    }

    @Operation(summary = "콘센트 존재 장소 조회하기", description = "콘센트Id로 콘센트가 존재하는 건물의 Id와 층을 조회함")
    @GetMapping("/socket/place")
    public ApiResponse<SocketResponseDto.SocketPlace> getSocketPlace(
        @RequestParam("socketId") Long socketId) {
        return ApiResponse.onSuccess((
            SocketConverter.toSocketPlace(socketService.getSocketPlace(socketId))));


    }

}
