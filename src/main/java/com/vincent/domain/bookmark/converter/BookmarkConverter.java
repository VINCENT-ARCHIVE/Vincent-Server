package com.vincent.domain.bookmark.converter;

import com.vincent.domain.bookmark.controller.dto.BookmarkResponseDto;
import com.vincent.domain.bookmark.entity.Bookmark;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;

public class BookmarkConverter {

    public static BookmarkResponseDto.Bookmark toBookmarkResponse(Long bookmarkId) {
        return BookmarkResponseDto.Bookmark.builder()
            .bookmarkId(bookmarkId)
            .build();
    }

    public static BookmarkResponseDto.BookmarkDetail toBookmarkDetailResponse(Bookmark bookmark) {
        return BookmarkResponseDto.BookmarkDetail.builder()
            .bookmarkId(bookmark.getId())
            .socketId(bookmark.getSocket().getId())
            .socketName(bookmark.getSocket().getName())
            .socketImage(bookmark.getSocket().getImage())
//        .buildingName(bookmark.getSocket().getBuilding().getName())
            .build();
    }

    public static BookmarkResponseDto.BookmarkList toBookmarkListResponse(
        Page<Bookmark> bookmarkList) {

        List<BookmarkResponseDto.BookmarkDetail> bookmarkDetailList = bookmarkList.stream()
            .map(BookmarkConverter::toBookmarkDetailResponse).collect(Collectors.toList());

        return BookmarkResponseDto.BookmarkList.builder()
            .isLast(bookmarkList.isLast())
            .isFirst(bookmarkList.isFirst())
            .totalPage(bookmarkList.getTotalPages())
            .totalElements(bookmarkList.getTotalElements())
            .listSize(bookmarkDetailList.size())
            .bookmarkDetails(bookmarkDetailList)
            .build();
    }

    public static BookmarkResponseDto.BookmarkExistence toBookmarkExistenceResponse(
        Boolean result) {
        return BookmarkResponseDto.BookmarkExistence.builder()
            .isBookmarkExist(result)
            .build();
    }
}
