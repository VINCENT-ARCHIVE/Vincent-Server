package com.vincent.domain.bookmark.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.domain.bookmark.controller.dto.BookmarkResponseDto;
import com.vincent.domain.bookmark.converter.BookmarkConverter;
import com.vincent.domain.bookmark.entity.Bookmark;
import com.vincent.domain.bookmark.service.BookmarkService;
import com.vincent.domain.building.entity.Building;
import com.vincent.domain.socket.entity.Socket;
import com.vincent.exception.handler.ErrorHandler;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookmarkController.class)
@MockBean(JpaMetamodelMappingContext.class)
public class BookmarkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookmarkService bookmarkService;

    @MockBean
    private BookmarkConverter bookmarkConverter;

    @Autowired
    private ObjectMapper objectMapper;

    // 테스트 데이터
    Long socketId = 1L;
    Long memberId = 1L;
    @Test
    @WithMockUser(username = "1")
    public void 북마크성공() throws Exception {

        BookmarkService.BookmarkResult result = new BookmarkService.BookmarkResult(socketId);

        // Mocking 서비스 응답
        when(bookmarkService.bookmark(eq(socketId), eq(memberId))).thenReturn(result);

        // 요청 실행
        ResultActions resultActions = mockMvc.perform(post("/v1/bookmark/1")
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf()));

        // 응답 검증
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("COMMON200"))
                .andExpect(jsonPath("$.message").value("성공입니다"))
                .andExpect(jsonPath("$.result.bookmarkId").value(result.getBookmarkId()));
    }



    @Test
    @WithMockUser(username = "1")
    public void 북마크실패_이미북마크존재() throws Exception {


        // Mocking 서비스 응답
        when(bookmarkService.bookmark(eq(socketId), eq(memberId)))
            .thenThrow(new ErrorHandler(ErrorStatus.BOOKMARK_ALREADY_EXIST));

        // 요청 실행
        ResultActions resultActions = mockMvc.perform(post("/v1/bookmark/1")
            .contentType(MediaType.APPLICATION_JSON)
            .with(SecurityMockMvcRequestPostProcessors.csrf()));

        // 응답 검증
        resultActions.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.isSuccess").value(false))
            .andExpect(jsonPath("$.code").value(ErrorStatus.BOOKMARK_ALREADY_EXIST.getReason().getCode()))
            .andExpect(jsonPath("$.message").value(ErrorStatus.BOOKMARK_ALREADY_EXIST.getReason().getMessage()));
    }

    @Test
    @WithMockUser(username = "1")
    public void 북마크실패_멤버없음() throws Exception {


        // Mocking 서비스 응답
        when(bookmarkService.bookmark(eq(socketId), eq(memberId)))
            .thenThrow(new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));

        // 요청 실행
        ResultActions resultActions = mockMvc.perform(post("/v1/bookmark/1")
            .contentType(MediaType.APPLICATION_JSON)
            .with(SecurityMockMvcRequestPostProcessors.csrf()));

        // 응답 검증
        resultActions.andExpect(status().isNotFound())
            .andExpect(jsonPath("$.isSuccess").value(false))
            .andExpect(jsonPath("$.code").value(ErrorStatus.MEMBER_NOT_FOUND.getReason().getCode()))
            .andExpect(jsonPath("$.message").value(ErrorStatus.MEMBER_NOT_FOUND.getReason().getMessage()));
    }

    @Test
    @WithMockUser(username = "1")
    public void 북마크실패_소켓없음() throws Exception {


        // Mocking 서비스 응답
        when(bookmarkService.bookmark(eq(socketId), eq(memberId)))
            .thenThrow(new ErrorHandler(ErrorStatus.SOCKET_NOT_FOUND));

        // 요청 실행
        ResultActions resultActions = mockMvc.perform(post("/v1/bookmark/1")
            .contentType(MediaType.APPLICATION_JSON)
            .with(SecurityMockMvcRequestPostProcessors.csrf()));

        // 응답 검증
        resultActions.andExpect(status().isNotFound())
            .andExpect(jsonPath("$.isSuccess").value(false))
            .andExpect(jsonPath("$.code").value(ErrorStatus.SOCKET_NOT_FOUND.getReason().getCode()))
            .andExpect(jsonPath("$.message").value(ErrorStatus.SOCKET_NOT_FOUND.getReason().getMessage()));
    }

    @Test
    @WithMockUser(username = "1")
    public void 북마크취소_성공() throws Exception {

        // Mocking 서비스 응답
        doNothing().when(bookmarkService).deleteBookmark(eq(socketId), eq(memberId));

        // 요청 실행
        ResultActions resultActions = mockMvc.perform(delete("/v1/bookmark/1")
            .contentType(MediaType.APPLICATION_JSON)
            .with(SecurityMockMvcRequestPostProcessors.csrf()));

        // 응답 검증
        resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("$.isSuccess").value(true))
            .andExpect(jsonPath("$.code").value("COMMON200"))
            .andExpect(jsonPath("$.message").value("성공입니다"));
    }

    @Test
    @WithMockUser(username = "1")
    public void 북마크취소실패_북마크존재하지않음() throws Exception {

        // Mocking 서비스 응답
        doThrow(new ErrorHandler(ErrorStatus.BOOKMARK_ALREADY_DELETED)).when(bookmarkService).deleteBookmark(eq(socketId), eq(memberId));

        // 요청 실행
        ResultActions resultActions = mockMvc.perform(delete("/v1/bookmark/1")
            .contentType(MediaType.APPLICATION_JSON)
            .with(SecurityMockMvcRequestPostProcessors.csrf()));

        // 응답 검증
        resultActions.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.isSuccess").value(false))
            .andExpect(jsonPath("$.code").value(ErrorStatus.BOOKMARK_ALREADY_DELETED.getReason().getCode()))
            .andExpect(jsonPath("$.message").value(ErrorStatus.BOOKMARK_ALREADY_DELETED.getReason().getMessage()));
    }

    @Test
    @WithMockUser(username = "1")
    public void 북마크취소실패_멤버없음() throws Exception {

        // Mocking 서비스 응답
        doThrow(new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND)).when(bookmarkService).deleteBookmark(eq(socketId), eq(memberId));

        // 요청 실행
        ResultActions resultActions = mockMvc.perform(delete("/v1/bookmark/1")
            .contentType(MediaType.APPLICATION_JSON)
            .with(SecurityMockMvcRequestPostProcessors.csrf()));

        // 응답 검증
        resultActions.andExpect(status().isNotFound())
            .andExpect(jsonPath("$.isSuccess").value(false))
            .andExpect(jsonPath("$.code").value(ErrorStatus.MEMBER_NOT_FOUND.getReason().getCode()))
            .andExpect(jsonPath("$.message").value(ErrorStatus.MEMBER_NOT_FOUND.getReason().getMessage()));
    }

    @Test
    @WithMockUser(username = "1")
    public void 북마크취소실패_소켓없음() throws Exception {

        // Mocking 서비스 응답
        doThrow(new ErrorHandler(ErrorStatus.SOCKET_NOT_FOUND)).when(bookmarkService).deleteBookmark(eq(socketId), eq(memberId));

        // 요청 실행
        ResultActions resultActions = mockMvc.perform(delete("/v1/bookmark/1")
            .contentType(MediaType.APPLICATION_JSON)
            .with(SecurityMockMvcRequestPostProcessors.csrf()));

        // 응답 검증
        resultActions.andExpect(status().isNotFound())
            .andExpect(jsonPath("$.isSuccess").value(false))
            .andExpect(jsonPath("$.code").value(ErrorStatus.SOCKET_NOT_FOUND.getReason().getCode()))
            .andExpect(jsonPath("$.message").value(ErrorStatus.SOCKET_NOT_FOUND.getReason().getMessage()));
    }



    @Test
    @WithMockUser(username = "1")
    public void 북마크리스트조회성공() throws Exception {

        int page = 0;

        Bookmark bookmark = Mockito.mock(Bookmark.class);
        when(bookmark.getId()).thenReturn(1L);
        when(bookmark.getSocket()).thenReturn(Mockito.mock(Socket.class));
        when(bookmark.getSocket().getId()).thenReturn(1L);
        when(bookmark.getSocket().getName()).thenReturn("socketName");
        when(bookmark.getSocket().getImage()).thenReturn("socketImage");
        when(bookmark.getSocket().getBuilding()).thenReturn(Mockito.mock(Building.class));
        when(bookmark.getSocket().getBuilding().getName()).thenReturn("buildingName");

        Page<Bookmark> bookmarkPage = new PageImpl<>(Collections.singletonList(bookmark), PageRequest.of(page, 10), 1);
        BookmarkResponseDto.BookmarkListDto bookmarkListDto = new BookmarkResponseDto.BookmarkListDto(
            Collections.singletonList(new BookmarkResponseDto.BookmarkDto(1L, 1L, "socketName", "socketImage", "buildingName")),
            1, 1, 1L, true, true
        );

        when(bookmarkService.findBookmarkList(eq(memberId), eq(page))).thenReturn(bookmarkPage);
        when(BookmarkConverter.bookmarkListDto(eq(bookmarkPage))).thenReturn(bookmarkListDto);

        ResultActions resultActions = mockMvc.perform(get("/v1/bookmark")
            .param("page", String.valueOf(page))
            .with(SecurityMockMvcRequestPostProcessors.csrf()));

        resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("$.isSuccess").value(true))
            .andExpect(jsonPath("$.code").value("COMMON200"))
            .andExpect(jsonPath("$.message").value("성공입니다"))
            .andExpect(jsonPath("$.result.bookmarks").isArray())
            .andExpect(jsonPath("$.result.bookmarks[0].bookmarkId").value(1L))
            .andExpect(jsonPath("$.result.bookmarks[0].socketId").value(1L))
            .andExpect(jsonPath("$.result.bookmarks[0].socketName").value("socketName"))
            .andExpect(jsonPath("$.result.bookmarks[0].socketImage").value("socketImage"))
            .andExpect(jsonPath("$.result.bookmarks[0].buildingName").value("buildingName"));
    }

    @Test
    @WithMockUser(username = "1")
    public void 북마크리스트조회실패_멤버없음() throws Exception {

        int page = 0;

        when(bookmarkService.findBookmarkList(eq(memberId), eq(page)))
            .thenThrow(new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));

        ResultActions resultActions = mockMvc.perform(get("/v1/bookmark")
            .param("page", String.valueOf(page))
            .with(SecurityMockMvcRequestPostProcessors.csrf()));

        resultActions.andExpect(status().isNotFound())
            .andExpect(jsonPath("$.isSuccess").value(false))
            .andExpect(jsonPath("$.code").value(ErrorStatus.MEMBER_NOT_FOUND.getReason().getCode()))
            .andExpect(jsonPath("$.message").value(ErrorStatus.MEMBER_NOT_FOUND.getReason().getMessage()));
    }
}