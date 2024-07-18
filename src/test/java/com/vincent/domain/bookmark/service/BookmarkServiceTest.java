package com.vincent.domain.bookmark.service;
import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.domain.bookmark.entity.Bookmark;
import com.vincent.domain.bookmark.repository.BookmarkRepository;
import com.vincent.domain.bookmark.service.BookmarkService.BookmarkResult;
import com.vincent.domain.member.entity.Member;
import com.vincent.domain.member.repository.MemberRepository;
import com.vincent.domain.socket.entity.Socket;

import com.vincent.domain.socket.repository.SocketRepository;
import com.vincent.exception.handler.ErrorHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class BookmarkServiceTest {

    @InjectMocks
    private BookmarkService bookmarkService;

    @Mock
    private BookmarkRepository bookmarkRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private SocketRepository socketRepository;

    @Mock
    private Member member;

    @Mock
    private Socket socket;

    @Mock
    private Bookmark bookmark;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void 북마크성공() {
        Long socketId = 1L;
        Long memberId = 1L;

        when(memberRepository.findById(memberId)).thenReturn(java.util.Optional.of(member));
        when(socketRepository.findById(socketId)).thenReturn(java.util.Optional.of(socket));
        when(bookmarkRepository.existsBySocketAndMember(socket, member)).thenReturn(false);
        when(bookmarkRepository.save(any(Bookmark.class))).thenReturn(bookmark);
        when(bookmark.getId()).thenReturn(1L);

        BookmarkService.BookmarkResult result = bookmarkService.bookmark(socketId, memberId);

        assertNotNull(result);
        assertEquals(1L, result.getBookmarkId());
        verify(bookmarkRepository, times(1)).save(any(Bookmark.class));
    }

    @Test
    public void 북마크실패_이미북마크존재() {
        Long socketId = 1L;
        Long memberId = 1L;

        when(memberRepository.findById(memberId)).thenReturn(java.util.Optional.of(member));
        when(socketRepository.findById(socketId)).thenReturn(java.util.Optional.of(socket));
        when(bookmarkRepository.existsBySocketAndMember(socket, member)).thenReturn(true);

        ErrorHandler thrown = assertThrows(ErrorHandler.class, () -> {
            bookmarkService.bookmark(socketId, memberId);
        });

        assertEquals(ErrorStatus.BOOKMARK_ALREADY_EXIST, thrown.getCode());
        verify(bookmarkRepository, never()).save(any(Bookmark.class));
    }

    @Test
    public void 북마크실패_멤버없음() {
        Long socketId = 1L;
        Long memberId = 1L;

        when(memberRepository.findById(memberId)).thenReturn(java.util.Optional.empty());

        ErrorHandler thrown = assertThrows(ErrorHandler.class, () -> {
            bookmarkService.bookmark(socketId, memberId);
        });

        assertEquals(ErrorStatus.MEMBER_NOT_FOUND, thrown.getCode());
        verify(bookmarkRepository, never()).save(any(Bookmark.class));
    }

    @Test
    public void 북마크실패_소켓없음() {
        Long socketId = 1L;
        Long memberId = 1L;

        when(memberRepository.findById(memberId)).thenReturn(java.util.Optional.of(member));
        when(socketRepository.findById(socketId)).thenReturn(java.util.Optional.empty());

        ErrorHandler thrown = assertThrows(ErrorHandler.class, () -> {
            bookmarkService.bookmark(socketId, memberId);
        });

        assertEquals(ErrorStatus.SOCKET_NOT_FOUND, thrown.getCode());
        verify(bookmarkRepository, never()).save(any(Bookmark.class));
    }

    @Test
    public void 북마크실패_북마크없음() {
        Long socketId = 1L;
        Long memberId = 1L;

        when(memberRepository.findById(memberId)).thenReturn(java.util.Optional.of(member));
        when(socketRepository.findById(socketId)).thenReturn(java.util.Optional.of(socket));
        when(bookmarkRepository.existsBySocketAndMember(socket, member)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> {
            bookmarkService.bookmark(socketId, memberId);
        });

        verify(bookmarkRepository, times(1)).save(any(Bookmark.class));
    }
}
