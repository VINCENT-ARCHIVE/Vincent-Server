package com.vincent.domain.bookmark.controller;

import com.vincent.apipayload.ApiResponse;
import com.vincent.config.security.principal.PrincipalDetails;
import com.vincent.domain.bookmark.controller.dto.BookmarkResponseDto;
import com.vincent.domain.bookmark.converter.BookmarkConverter;
import com.vincent.domain.bookmark.entity.Bookmark;
import com.vincent.domain.bookmark.service.BookmarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/v1")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @Operation(summary = "콘센트 찜하기", description = "콘센트를 찜하여 북마크 목록에 추가함")
    @Parameter(name = "socketId", description = "찜하려는 콘센트의 Id")
    @PostMapping("/bookmark/{socketId}")
    public ApiResponse<BookmarkResponseDto.Bookmark> bookmark(
        @PathVariable("socketId") Long socketId,
        Authentication authentication) {

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        Long memberId = principalDetails.getMemberId();
        BookmarkService.BookmarkResult result = bookmarkService.bookmark(socketId, memberId);
        return ApiResponse.onSuccess(BookmarkConverter.toBookmarkResponse(result.getBookmarkId()));
    }

    @Operation(summary = "콘센트 찜 취소하기", description = "북마크 목록에서 찜한 콘센트를 삭제함")
    @Parameter(name = "socketId", description = "삭제하고자하는 찜한 콘센트의 Id")
    @DeleteMapping("/bookmark/{socketId}")
    public ApiResponse<?> deleteBookmark(@PathVariable("socketId") Long socketId,
        Authentication authentication) {

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        Long memberId = principalDetails.getMemberId();
        bookmarkService.deleteBookmark(socketId, memberId);
        return ApiResponse.onSuccess(null);
    }

    @Operation(summary = "찜한 콘센트 조회하기", description = "사용자가 찜한 콘센트들의 목록을 최신순으로 조회함(한 페이지에 최대 10개씩)")
    @Parameter(name = "page", description = "찜한 콘센트 목록의 페이지 번호(0부터 시작)")
    @GetMapping("/bookmark")
    public ApiResponse<BookmarkResponseDto.BookmarkList> bookmarkList(
        @RequestParam(name = "page") Integer page, Authentication authentication) {

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        Long memberId = principalDetails.getMemberId();
        Page<Bookmark> bookmarkList = bookmarkService.findBookmarkList(memberId, page);
        return ApiResponse.onSuccess(BookmarkConverter.toBookmarkListResponse(bookmarkList));
    }


    /*
    @GetMapping("/bookmark/{socketId}")
    public ApiResponse<BookmarkResponseDto.BookmarkExistence> getBookmarkExist(
        @PathVariable("socketId") Long socketId,
        Authentication authentication) {

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        Long memberId = principalDetails.getMemberId();
        Boolean result = bookmarkService.getBookmarkExist(socketId, memberId);
        return ApiResponse.onSuccess(BookmarkConverter.toBookmarkExistenceResponse(result));
    }

     */


}
