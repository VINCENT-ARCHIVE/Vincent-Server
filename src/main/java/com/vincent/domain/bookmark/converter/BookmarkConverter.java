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

  public static BookmarkResponseDto.BookmarkDto bookmarkDto(Bookmark bookmark){
    return BookmarkResponseDto.BookmarkDto.builder()
        .bookmarkId(bookmark.getId())
        .socketId(bookmark.getSocket().getId())
        .socketName(bookmark.getSocket().getName())
        .socketImage(bookmark.getSocket().getImage())
        .buildingName(bookmark.getSocket().getBuilding().getName())
        .build();
  }
  public static BookmarkResponseDto.BookmarkListDto bookmarkListDto(Page<Bookmark> bookmarkList){

    List<BookmarkResponseDto.BookmarkDto> bookmarkDtoList = bookmarkList.stream()
        .map(BookmarkConverter::bookmarkDto).collect(Collectors.toList());

    return BookmarkResponseDto.BookmarkListDto.builder()
        .isLast(bookmarkList.isLast())
        .isFirst(bookmarkList.isFirst())
        .totalPage(bookmarkList.getTotalPages())
        .totalElements(bookmarkList.getTotalElements())
        .listSize(bookmarkDtoList.size())
        .bookmarks(bookmarkDtoList)
        .build();
  }
}
