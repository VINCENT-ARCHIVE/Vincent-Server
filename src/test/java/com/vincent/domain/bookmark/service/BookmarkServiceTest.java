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
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

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


    Long socketId = 1L;
    Long memberId = 1L;
    Integer page = 0;


    @Test
    public void 북마크성공() {

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

        when(memberRepository.findById(memberId)).thenReturn(java.util.Optional.empty());

        ErrorHandler thrown = assertThrows(ErrorHandler.class, () -> {
            bookmarkService.bookmark(socketId, memberId);
        });

        assertEquals(ErrorStatus.MEMBER_NOT_FOUND, thrown.getCode());
        verify(bookmarkRepository, never()).save(any(Bookmark.class));
    }

    @Test
    public void 북마크실패_소켓없음() {

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

        when(memberRepository.findById(memberId)).thenReturn(java.util.Optional.of(member));
        when(socketRepository.findById(socketId)).thenReturn(java.util.Optional.of(socket));
        when(bookmarkRepository.existsBySocketAndMember(socket, member)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> {
            bookmarkService.bookmark(socketId, memberId);
        });

        verify(bookmarkRepository, times(1)).save(any(Bookmark.class));
    }

    @Test
    public void 북마크취소_성공() {


        Bookmark bookmark = Bookmark.builder()
            .member(member)
            .socket(socket)
            .build();

        when(memberRepository.findById(memberId)).thenReturn(java.util.Optional.of(member));
        when(socketRepository.findById(socketId)).thenReturn(java.util.Optional.of(socket));
        when(bookmarkRepository.existsBySocketAndMember(socket, member)).thenReturn(true);
        when(bookmarkRepository.findByMemberAndSocket(member, socket)).thenReturn(bookmark);

        bookmarkService.deleteBookmark(socketId, memberId);

        verify(bookmarkRepository, times(1)).delete(bookmark);
    }

    @Test
    public void 찜하기취소실패_북마크존재하지않음() {

        when(memberRepository.findById(memberId)).thenReturn(java.util.Optional.of(member));
        when(socketRepository.findById(socketId)).thenReturn(java.util.Optional.of(socket));
        when(bookmarkRepository.existsBySocketAndMember(socket, member)).thenReturn(false);

        ErrorHandler thrown = assertThrows(ErrorHandler.class, () -> {
            bookmarkService.deleteBookmark(socketId, memberId);
        });

        assertEquals(ErrorStatus.BOOKMARK_ALREADY_DELETED, thrown.getCode());
        verify(bookmarkRepository, never()).delete(any(Bookmark.class));
    }

    @Test
    public void 찜하기취소실패_멤버없음() {

        when(memberRepository.findById(memberId)).thenReturn(java.util.Optional.empty());

        ErrorHandler thrown = assertThrows(ErrorHandler.class, () -> {
            bookmarkService.deleteBookmark(socketId, memberId);
        });

        assertEquals(ErrorStatus.MEMBER_NOT_FOUND, thrown.getCode());
        verify(bookmarkRepository, never()).delete(any(Bookmark.class));
    }

    @Test
    public void 찜하기취소실패_소켓없음() {

        when(memberRepository.findById(memberId)).thenReturn(java.util.Optional.of(member));
        when(socketRepository.findById(socketId)).thenReturn(java.util.Optional.empty());

        ErrorHandler thrown = assertThrows(ErrorHandler.class, () -> {
            bookmarkService.deleteBookmark(socketId, memberId);
        });

        assertEquals(ErrorStatus.SOCKET_NOT_FOUND, thrown.getCode());
        verify(bookmarkRepository, never()).delete(any(Bookmark.class));
    }

    @Test
    public void 북마크리스트조회성공() {

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        Page<Bookmark> bookmarkPage = new PageImpl<>(Collections.singletonList(bookmark),
                PageRequest.of(page, 10), 1);
        when(bookmarkRepository.findAllByMemberOrderByCreatedAtDesc(any(Member.class),
                any(PageRequest.class))).thenReturn(bookmarkPage);

        Page<Bookmark> result = bookmarkService.findBookmarkList(memberId, page);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(bookmark, result.getContent().get(0));
    }

    @Test
    public void 북마크리스트조회실패_멤버없음() {

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        ErrorHandler thrown = assertThrows(ErrorHandler.class, () -> {
            bookmarkService.findBookmarkList(memberId, page);
        });

        assertEquals(ErrorStatus.MEMBER_NOT_FOUND, thrown.getCode());
    }

    @Test
    public void 북마크여부조회성공() {

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(socketRepository.findById(socketId)).thenReturn(Optional.of(socket));
        when(bookmarkRepository.existsBySocketAndMember(socket, member)).thenReturn(true);

        Boolean result = bookmarkService.getBookmarkExist(socketId, memberId);

        assertTrue(result);
        verify(memberRepository).findById(memberId);
        verify(socketRepository).findById(socketId);
        verify(bookmarkRepository).existsBySocketAndMember(socket, member);
    }

    @Test
    public void 북마크여부조회실패_멤버없음() {

        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());
        when(socketRepository.findById(socketId)).thenReturn(Optional.of(socket));

        ErrorHandler thrown = assertThrows(ErrorHandler.class, () -> {
            bookmarkService.getBookmarkExist(socketId, memberId);
        });

        assertEquals(ErrorStatus.MEMBER_NOT_FOUND, thrown.getCode());
        verify(bookmarkRepository, never()).existsBySocketAndMember(any(Socket.class),
                any(Member.class));
    }

    @Test
    public void 북마크여부조회실패_소켓없음() {

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(socketRepository.findById(socketId)).thenReturn(Optional.empty());

        ErrorHandler thrown = assertThrows(ErrorHandler.class, () -> {
            bookmarkService.getBookmarkExist(socketId, memberId);
        });

        assertEquals(ErrorStatus.SOCKET_NOT_FOUND, thrown.getCode());
        verify(bookmarkRepository, never()).existsBySocketAndMember(any(Socket.class),
                any(Member.class));
    }

}

