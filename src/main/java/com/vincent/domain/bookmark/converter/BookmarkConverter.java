package com.vincent.domain.bookmark.converter;

import com.vincent.domain.bookmark.controller.dto.BookmarkResponseDto;
import com.vincent.domain.member.controller.dto.MemberResponseDto;

import java.time.LocalDateTime;

public class BookmarkConverter {

    public static BookmarkResponseDto.Addition toAdditionResponse(Long bookmarkId){
        return BookmarkResponseDto.Addition.builder()
                .bookmarkId(bookmarkId)
                .build();
    }
}
