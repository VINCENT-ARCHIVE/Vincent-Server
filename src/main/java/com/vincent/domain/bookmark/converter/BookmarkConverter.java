package com.vincent.domain.bookmark.converter;

import com.vincent.domain.bookmark.controller.dto.BookmarkResponseDto;
import com.vincent.domain.member.controller.dto.MemberResponseDto;
import java.time.LocalDateTime;

public class BookmarkConverter {

    public static BookmarkResponseDto.Bookmark toBookmarkResponse(Long bookmarkId) {
        return BookmarkResponseDto.Bookmark.builder()
            .bookmarkId(bookmarkId)
            .build();
    }
}
