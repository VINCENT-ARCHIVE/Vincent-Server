package com.vincent.domain.bookmark.service;
import com.vincent.apiPayload.status.ErrorStatus;
import com.vincent.domain.bookmark.entity.Bookmark;
import com.vincent.domain.bookmark.repository.BookmarkRepository;
import com.vincent.domain.bookmark.service.BookmarkService;
import com.vincent.domain.bookmark.service.BookmarkService.AdditionResult;
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
    public void testAddition_Success() {
        Long socketId = 1L;
        Long memberId = 1L;

        when(memberRepository.findById(memberId)).thenReturn(java.util.Optional.of(member));
        when(socketRepository.findById(socketId)).thenReturn(java.util.Optional.of(socket));
        when(bookmarkRepository.existsBySocketAndMember(socket, member)).thenReturn(false);
        when(bookmarkRepository.findBySocketIdAndMemberId(socketId, memberId)).thenReturn(java.util.Optional.of(bookmark));
        when(bookmark.getId()).thenReturn(1L);

        AdditionResult result = bookmarkService.Addition(socketId, memberId);

        assertNotNull(result);
        assertEquals(1L, result.getBookmarkId());
        verify(bookmarkRepository, times(1)).save(any(Bookmark.class));
    }

    //실패
    @Test
    public void testAddition_BookmarkAlreadyExists() {
        Long socketId = 1L;
        Long memberId = 1L;

        when(memberRepository.findById(memberId)).thenReturn(java.util.Optional.of(member));
        when(socketRepository.findById(socketId)).thenReturn(java.util.Optional.of(socket));
        when(bookmarkRepository.existsBySocketAndMember(socket, member)).thenReturn(true);

        ErrorHandler thrown = assertThrows(ErrorHandler.class, () -> {
            bookmarkService.Addition(socketId, memberId);
        });

        assertEquals(ErrorStatus.BOOKMARK_ALREADY_EXISTED, thrown.getErrorReason());
        verify(bookmarkRepository, never()).save(any(Bookmark.class));
    }

    @Test
    public void testAddition_MemberNotFound() {
        Long socketId = 1L;
        Long memberId = 1L;

        when(memberRepository.findById(memberId)).thenReturn(java.util.Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            bookmarkService.Addition(socketId, memberId);
        });

        verify(bookmarkRepository, never()).save(any(Bookmark.class));
    }

    @Test
    public void testAddition_SocketNotFound() {
        Long socketId = 1L;
        Long memberId = 1L;

        when(memberRepository.findById(memberId)).thenReturn(java.util.Optional.of(member));
        when(socketRepository.findById(socketId)).thenReturn(java.util.Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            bookmarkService.Addition(socketId, memberId);
        });

        verify(bookmarkRepository, never()).save(any(Bookmark.class));
    }

    @Test
    public void testAddition_BookmarkNotFound() {
        Long socketId = 1L;
        Long memberId = 1L;

        when(memberRepository.findById(memberId)).thenReturn(java.util.Optional.of(member));
        when(socketRepository.findById(socketId)).thenReturn(java.util.Optional.of(socket));
        when(bookmarkRepository.existsBySocketAndMember(socket, member)).thenReturn(false);
        when(bookmarkRepository.findBySocketIdAndMemberId(socketId, memberId)).thenReturn(java.util.Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            bookmarkService.Addition(socketId, memberId);
        });

        verify(bookmarkRepository, times(1)).save(any(Bookmark.class));
    }
}
