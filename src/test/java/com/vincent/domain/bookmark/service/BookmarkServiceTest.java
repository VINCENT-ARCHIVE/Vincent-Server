package com.vincent.domain.bookmark.service;

import com.vincent.domain.bookmark.entity.Bookmark;
import com.vincent.domain.bookmark.service.data.BookmarkDataService;
import com.vincent.domain.member.entity.Member;
import com.vincent.domain.member.service.data.MemberDataService;
import com.vincent.domain.socket.entity.Socket;
import com.vincent.domain.socket.service.data.SocketDataService;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookmarkServiceTest {

    @InjectMocks
    private BookmarkService bookmarkService;

    @Mock
    private MemberDataService memberDataService;

    @Mock
    private SocketDataService socketDataService;

    @Mock
    private BookmarkDataService bookmarkDataService;

    @Test
    public void 북마크성공() {
        //given
        Long socketId = 1L;
        Long memberId = 1L;
        Member member = Member.builder().id(1L).build();
        Socket socket = Socket.builder().id(1L).build();
        Bookmark bookmark = Bookmark.builder().member(member).socket(socket).build();
        Bookmark savedBookmark = Bookmark.builder().id(1L).member(member).socket(socket).build();
        //when
        when(memberDataService.findById(memberId)).thenReturn(member);
        when(socketDataService.findById(socketId)).thenReturn(socket);
        doNothing().when(bookmarkDataService).isBookmarkExists(socket, member);
        when(bookmarkDataService.save(any(Bookmark.class))).thenReturn(savedBookmark); // 특정 Bookmark 객체 대신 어떤 Bookmark 객체든 저장하면 savedBookmark를 반환

        //then
        Long result = bookmarkService.bookmark(socketId, memberId);
        assertNotNull(result);
        assertEquals(1L, result);
        verify(bookmarkDataService, times(1)).save(any(Bookmark.class));
        verify(memberDataService, times(1)).findById(memberId);
        verify(socketDataService, times(1)).findById(socketId);
        verify(bookmarkDataService, times(1)).isBookmarkExists(socket, member);
    }


    @Test
    public void 북마크취소() {
        //given
        Long socketId = 1L;
        Long memberId = 1L;
        Member member = Member.builder().id(1L).build();
        Socket socket = Socket.builder().id(1L).build();

        Bookmark bookmark = Bookmark.builder()
            .member(member)
            .socket(socket)
            .build();

        //when
        when(memberDataService.findById(memberId)).thenReturn(member);
        when(socketDataService.findById(socketId)).thenReturn(socket);
        doNothing().when(bookmarkDataService).isBookmarkDeleted(socket,member);
        when(bookmarkDataService.findByMemberAndSocket(member, socket)).thenReturn(bookmark);
        doNothing().when(bookmarkDataService).delete(any(Bookmark.class));

        //then
        bookmarkService.deleteBookmark(socketId, memberId);

        verify(bookmarkDataService, times(1)).delete(any(Bookmark.class));
        verify(memberDataService, times(1)).findById(memberId);
        verify(socketDataService, times(1)).findById(socketId);
        verify(bookmarkDataService, times(1)).isBookmarkDeleted(socket, member);
        verify(bookmarkDataService, times(1)).findByMemberAndSocket(member, socket);

    }

    @Test
    public void 북마크리스트조회성공() {
        //given
        Long memberId = 1L;
        Integer page = 0;
        Page<Bookmark> expectedPage = new PageImpl<>(Collections.emptyList());
        Member member = Member.builder().id(memberId).build();

        //when
        when(memberDataService.findById(memberId)).thenReturn(member);
        when(bookmarkDataService.findAllByMember(member, page)).thenReturn(expectedPage);

        //then
        Page<Bookmark> result = bookmarkService.findBookmarkList(memberId, page);
        verify(memberDataService).findById(memberId);
        verify(bookmarkDataService).findAllByMember(member, page);
        assertEquals(expectedPage, result);
    }

    @Test
    public void 북마크존재여부확인성공() {
        //given
        Long memberId = 1L;
        Long socketId = 1L;
        Member member = Member.builder().id(1L).build();
        Socket socket = Socket.builder().id(1L).build();

        //when
        when(memberDataService.findById(memberId)).thenReturn(member);
        when(socketDataService.findById(socketId)).thenReturn(socket);
        when(bookmarkDataService.existsBySocketAndMember(socket, member)).thenReturn(true);

        //then
        Boolean result = bookmarkService.getBookmarkExist(socketId, memberId);
        verify(memberDataService).findById(memberId);
        verify(socketDataService).findById(socketId);
        verify(bookmarkDataService).existsBySocketAndMember(socket, member);
        assertEquals(true, result);
    }

}
