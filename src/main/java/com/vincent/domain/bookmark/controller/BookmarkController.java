package com.vincent.domain.bookmark.controller;

import com.vincent.apipayload.ApiResponse;
import com.vincent.domain.bookmark.controller.dto.BookmarkResponseDto;
import com.vincent.domain.bookmark.converter.BookmarkConverter;
import com.vincent.domain.bookmark.entity.Bookmark;
import com.vincent.domain.bookmark.service.BookmarkService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/v1")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping("/bookmark/{socketId}")
    public ApiResponse<BookmarkResponseDto.Bookmark> bookmark(
        @PathVariable("socketId") Long socketId,
        Authentication authentication) {

        Long memberId = Long.parseLong(authentication.getName());
        BookmarkService.BookmarkResult result = bookmarkService.bookmark(socketId, memberId);
        return ApiResponse.onSuccess(BookmarkConverter.toBookmarkResponse(result.getBookmarkId()));
    }

  @GetMapping("/bookmark")
  public ApiResponse<BookmarkResponseDto.BookmarkListDto> bookmarkList(@RequestParam(name = "page") Integer page, Authentication authentication) {

    Long memberId = Long.parseLong(authentication.getName());
    Page<Bookmark> bookmarkList = bookmarkService.findBookmarkList(memberId, page);
    return ApiResponse.onSuccess(BookmarkConverter.bookmarkListDto(bookmarkList));
  }


}
