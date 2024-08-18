package com.vincent.domain.bookmark.controller;

import com.vincent.apipayload.ApiResponse;
import com.vincent.config.security.principal.PrincipalDetails;
import com.vincent.domain.bookmark.controller.dto.BookmarkResponseDto;
import com.vincent.domain.bookmark.controller.dto.BookmarkResponseDto.Bookmark;
import com.vincent.domain.bookmark.service.BookmarkService;
import com.vincent.domain.building.entity.Building;
import com.vincent.domain.building.entity.Floor;
import com.vincent.domain.building.entity.Space;
import com.vincent.domain.member.entity.Member;
import com.vincent.domain.socket.entity.Socket;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.Authentication;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookmarkControllerTest {

    @Mock
    private BookmarkService bookmarkService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private BookmarkController bookmarkController;

    private PrincipalDetails principalDetails;

    @BeforeEach
    public void setUp() {
        Member member = Member.builder().id(1L).build();
        principalDetails = new PrincipalDetails(member);
        when(authentication.getPrincipal()).thenReturn(principalDetails);
    }

    @Test
    public void 북마크_성공() {
        // given
        Long socketId = 1L;
        BookmarkService.BookmarkResult bookmarkResult = new BookmarkService.BookmarkResult(
            socketId);

        //when
        when(bookmarkService.bookmark(socketId, principalDetails.getMemberId())).thenReturn(
            bookmarkResult);

        // then
        ApiResponse<Bookmark> response = bookmarkController.bookmark(socketId, authentication);
        Bookmark result = response.getResult();

        verify(bookmarkService).bookmark(socketId, principalDetails.getMemberId());

        Assertions.assertThat(response.getIsSuccess()).isTrue();
        Assertions.assertThat(response.getCode()).isEqualTo("COMMON200");
        Assertions.assertThat(result.getBookmarkId()).isEqualTo(bookmarkResult.getBookmarkId());
    }

    @Test
    public void 북마크취소_성공() {
        // given
        Long socketId = 1L;

        // then
        ApiResponse<?> response = bookmarkController.deleteBookmark(socketId, authentication);

        verify(bookmarkService).deleteBookmark(socketId, principalDetails.getMemberId());

        Assertions.assertThat(response.getIsSuccess()).isTrue();
        Assertions.assertThat(response.getCode()).isEqualTo("COMMON200");
    }

    @Test
    public void 북마크리스트조회_성공() {
        //given
        int page = 0;
        Building building = Building.builder().name("Test Building").build();
        Floor floor = Floor.builder().building(building).level(1).build();
        Space space = Space.builder().floor(floor).name("Test Space").build();
        Socket socket = Socket.builder().space(space).name("Test Socket").build();
        com.vincent.domain.bookmark.entity.Bookmark bookmark = com.vincent.domain.bookmark.entity.Bookmark.builder()
            .socket(socket)
            .build();
        Page<com.vincent.domain.bookmark.entity.Bookmark> bookmarkPage = new PageImpl<>(
            List.of(bookmark));

        //when
        when(bookmarkService.findBookmarkList(principalDetails.getMemberId(), page)).thenReturn(
            bookmarkPage);

        //then
        ApiResponse<BookmarkResponseDto.BookmarkList> response = bookmarkController.bookmarkList(
            page, authentication);
        BookmarkResponseDto.BookmarkList result = response.getResult();

        Assertions.assertThat(response.getIsSuccess()).isTrue();
        Assertions.assertThat(response.getCode()).isEqualTo("COMMON200");
        Assertions.assertThat(result.getBookmarkDetails().size()).isEqualTo(1);
        BookmarkResponseDto.BookmarkDetail detail = result.getBookmarkDetails().get(0);
        Assertions.assertThat(detail.getSocketName()).isEqualTo("Test Socket");
        Assertions.assertThat(detail.getSpaceName()).isEqualTo("Test Space");
        Assertions.assertThat(detail.getBuildingName()).isEqualTo("Test Building");
        verify(bookmarkService).findBookmarkList(principalDetails.getMemberId(), page);
    }
}
