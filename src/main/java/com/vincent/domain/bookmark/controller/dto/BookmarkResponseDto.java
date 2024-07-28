package com.vincent.domain.bookmark.controller.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class BookmarkResponseDto {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Bookmark {

        Long bookmarkId;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookmarkList {

        List<BookmarkDetail> bookmarkDetails;
        Integer listSize;
        Integer totalPage;
        Long totalElements;
        Boolean isFirst;
        Boolean isLast;

    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookmarkDetail {

        Long bookmarkId;
        Long socketId;
        String socketName;
        String socketImage;
        String buildingName;

    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookmarkExistence {

        Boolean isBookmarkExist;
    }
}
