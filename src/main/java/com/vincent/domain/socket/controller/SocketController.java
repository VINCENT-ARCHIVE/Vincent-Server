package com.vincent.domain.socket.controller;


import com.vincent.apipayload.ApiResponse;
import com.vincent.config.security.principal.PrincipalDetails;
import com.vincent.domain.bookmark.service.BookmarkService;
import com.vincent.domain.socket.controller.dto.SocketResponseDto;
import com.vincent.domain.socket.converter.SocketConverter;
import com.vincent.domain.socket.entity.Socket;
import com.vincent.domain.socket.service.SocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/v1")
public class SocketController {

    private final SocketService socketService;
    private final BookmarkService bookmarkService;

    @GetMapping("/socket/{socketId}")
    public ApiResponse<SocketResponseDto.SocketInfo> socketInfo(@PathVariable("socketId") Long socketId,
        Authentication authentication) {

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        Long memberId = principalDetails.getMemberId();
        Boolean isBookmarkExist = bookmarkService.getBookmarkExist(socketId, memberId);
        Socket socketInfo = socketService.getSocketInfo(socketId);
        return ApiResponse.onSuccess(SocketConverter.toSocketInfoResponse(socketInfo, isBookmarkExist));
    }



}
