package com.vincent.domain.bookmark.controller;

import com.vincent.apipayload.ApiResponse;
import com.vincent.domain.bookmark.controller.dto.BookmarkResponseDto;
import com.vincent.domain.bookmark.converter.BookmarkConverter;
import com.vincent.domain.bookmark.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/v1")
public class BookmarkController {

  private final BookmarkService bookmarkService;

  @PostMapping("/bookmark/{socketId}")
  public ApiResponse<BookmarkResponseDto.Bookmark> Bookmark(@PathVariable("socketId") Long socketId,
      Authentication authentication) {

    Long memberId = Long.parseLong(authentication.getName());
    BookmarkService.BookmarkResult result = bookmarkService.Bookmark(socketId, memberId);
    return ApiResponse.onSuccess(BookmarkConverter.toBookmarkResponse(result.getBookmarkId()));
  }

  @DeleteMapping("/bookmark/{socketId}")
  public ApiResponse<?> bookmarkDelete(@PathVariable("socketId") Long socketId,
      Authentication authentication) {

    Long memberId = Long.parseLong(authentication.getName());
    bookmarkService.deleteBookmark(socketId, memberId);
    return ApiResponse.onSuccess(null);
  }


}
