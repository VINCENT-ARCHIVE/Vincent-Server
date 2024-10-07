package com.vincent.domain.bookmark.service.data;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.domain.bookmark.entity.Bookmark;
import com.vincent.domain.bookmark.repository.BookmarkRepository;
import com.vincent.domain.member.entity.Member;
import com.vincent.domain.socket.entity.Socket;
import com.vincent.exception.handler.ErrorHandler;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class BookmarkDataServiceTest {
    @Mock
    private BookmarkRepository bookmarkRepository;

    @InjectMocks
    private BookmarkDataService bookmarkDataService;

    @Test
    void 삭제() {
        //given
        Bookmark bookmark = Bookmark.builder().id(1L).build();

        //when
        doNothing().when(bookmarkRepository).delete(bookmark);

        //then
        bookmarkDataService.delete(bookmark);
        verify(bookmarkRepository, times(1)).delete(bookmark);
    }

    @Test
    void 북마크저장() {
        //given
        Bookmark bookmark = Bookmark.builder().id(1L).build();

        //when
        when(bookmarkRepository.save(bookmark)).thenReturn(bookmark);

        //then
        bookmarkDataService.save(bookmark);
        verify(bookmarkRepository, times(1)).save(bookmark);

    }

    @Test
    void 존재확인_소켓멤버존재시_참반환() {
        //given
        Member member = Member.builder().id(1L).build();
        Socket socket = Socket.builder().id(1L).build();

        //when
        when(bookmarkRepository.existsBySocketAndMember(socket,member)).thenReturn(true);

        //then
        Boolean result = bookmarkDataService.existsBySocketAndMember(socket,
            member);
        Assertions.assertEquals(result, true);
        verify(bookmarkRepository, times(1)).existsBySocketAndMember(socket, member);
    }

    @Test
    void 존재검증_소켓멤버존재시_예외발생() {
        //given
        Member member = Member.builder().id(1L).build();
        Socket socket = Socket.builder().id(1L).build();

        //when
        when(bookmarkRepository.existsBySocketAndMember(socket, member)).thenReturn(true);

        //then
        ErrorHandler thrown = Assertions.assertThrows(ErrorHandler.class,
            () -> bookmarkDataService.isBookmarkExists(socket, member));
        Assertions.assertEquals(thrown.getCode(), ErrorStatus.BOOKMARK_ALREADY_EXIST);
    }

    @Test
    void 존재검증_소켓멤버존재하지않을시_예외없음(){
        //given
        Member member = Member.builder().id(1L).build();
        Socket socket = Socket.builder().id(1L).build();

        //when
        when(bookmarkRepository.existsBySocketAndMember(socket, member)).thenReturn(false);

        //then
        bookmarkDataService.isBookmarkExists(socket, member);
        verify(bookmarkRepository, times(1)).existsBySocketAndMember(socket, member);
    }

    @Test
    void 삭제검증_소켓멤버존재하지않을시_예외발생() {
        // given
        Member member = Member.builder().id(1L).build();
        Socket socket = Socket.builder().id(1L).build();

        //when
        when(bookmarkRepository.existsBySocketAndMember(socket, member)).thenReturn(false);

        // then
        ErrorHandler thrown = Assertions.assertThrows(ErrorHandler.class,
            () -> bookmarkDataService.isBookmarkDeleted(socket, member));
        Assertions.assertEquals(thrown.getCode(), ErrorStatus.BOOKMARK_ALREADY_DELETED);
    }


    @Test
    void 삭제검증_소켓멤버존재시_예외없음() {
        // given
        Member member = Member.builder().id(1L).build();
        Socket socket = Socket.builder().id(1L).build();

        //when
        when(bookmarkRepository.existsBySocketAndMember(socket, member)).thenReturn(true);

        // then
        bookmarkDataService.isBookmarkDeleted(socket, member);
        verify(bookmarkRepository, times(1)).existsBySocketAndMember(socket, member);
    }

    @Test
    void findByMemberAndSocket_성공() {
        // given
        Member member = Member.builder().id(1L).build();
        Socket socket = Socket.builder().id(1L).build();
        Bookmark bookmark = Bookmark.builder().member(member).socket(socket).build();

        // when
        when(bookmarkRepository.findByMemberAndSocket(member, socket)).thenReturn(Optional.of(bookmark));

        // then
        Bookmark result = bookmarkDataService.findByMemberAndSocket(member, socket);

        Assertions.assertEquals(bookmark, result);
        verify(bookmarkRepository, times(1)).findByMemberAndSocket(member, socket);
    }

    @Test
    void findByMemberAndSocket_실패_해당북마크없음() {
        // given
        Member member = Member.builder().id(1L).build();
        Socket socket = Socket.builder().id(1L).build();

        // when
        when(bookmarkRepository.findByMemberAndSocket(member, socket)).thenReturn(Optional.empty());

        // then
        ErrorHandler thrown = Assertions.assertThrows(ErrorHandler.class,
            () ->  bookmarkDataService.findByMemberAndSocket(member, socket));


        Assertions.assertEquals(thrown.getCode(), ErrorStatus.BOOKMARK_NOT_FOUND);
        verify(bookmarkRepository, times(1)).findByMemberAndSocket(member, socket);
    }

    @Test
    void 멤버별북마크조회_페이지반환() {
        // given
        Member member = Member.builder().id(1L).build();
        PageRequest pageRequest = PageRequest.of(0, 10);
        Bookmark bookmark = Bookmark.builder().id(1L).member(member).build();
        Bookmark bookmark2 = Bookmark.builder().id(2L).member(member).build();
        Page<Bookmark> expectedPage = new PageImpl<>(Arrays.asList(bookmark, bookmark2));

        //when
        when(bookmarkRepository.findAllByMemberOrderByCreatedAtDesc(member, pageRequest)).thenReturn(expectedPage);

        //then
        Page<Bookmark> result = bookmarkDataService.findAllByMember(member, 0);

        Assertions.assertEquals(expectedPage, result);
    }
}
