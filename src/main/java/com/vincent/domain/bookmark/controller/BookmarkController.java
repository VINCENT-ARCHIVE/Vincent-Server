package com.vincent.domain.bookmark.controller;

import com.vincent.apipayload.ApiResponse;
import com.vincent.domain.bookmark.controller.dto.BookmarkResponseDto;
import com.vincent.domain.bookmark.converter.BookmarkConverter;
import com.vincent.domain.bookmark.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
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

   @DeleteMapping("/bookmark/{socketId}")
   public ApiResponse<?> deleteBookmark(@PathVariable("socketId") Long socketId,
      Authentication authentication) {

      Long memberId = Long.parseLong(authentication.getName());
      bookmarkService.deleteBookmark(socketId, memberId);
      return ApiResponse.onSuccess(null);
   }


}
