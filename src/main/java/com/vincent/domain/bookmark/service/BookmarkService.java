package com.vincent.domain.bookmark.service;

import com.vincent.apiPayload.status.ErrorStatus;
import com.vincent.domain.bookmark.entity.Bookmark;
import com.vincent.domain.bookmark.repository.BookmarkRepository;
import com.vincent.domain.member.entity.Member;
import com.vincent.domain.member.repository.MemberRepository;
import com.vincent.domain.socket.entity.Socket;
import com.vincent.domain.socket.repository.SocketRepository;
import com.vincent.exception.handler.ErrorHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Book;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final MemberRepository memberRepository;
    private final SocketRepository socketRepository;

    public AdditionResult Addition(Long socketId, Long memberId) {

        Member member = findMemberById(memberId);
        Socket socket = findSocketById(socketId);

        if (isBookmarkExists(socket, member)){
            { throw new ErrorHandler(ErrorStatus.BOOKMARK_ALREADY_EXISTED);}
        }
        saveBookmark(member,socket);
        Bookmark bookmark = findBookmarkBySocketAndMember(socketId, memberId);
        Long v = bookmark.getId();

        return new AdditionResult(v);

    }

    @Getter
    @AllArgsConstructor
    public static class AdditionResult {

        private Long bookmarkId;
    }

    private void saveBookmark(Member member, Socket socket) {
        bookmarkRepository.save(Bookmark.builder()
                .member(member)
                .socket(socket)
                .build());
    }

    private Boolean isBookmarkExists(Socket socket, Member member) {
        return bookmarkRepository.existsBySocketAndMember(socket, member);
    }
    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow();
    }
    private Socket findSocketById(Long socketId) {
        return socketRepository.findById(socketId).orElseThrow();
    }

    private Bookmark findBookmarkBySocketAndMember(Long socketId, Long memberId) {
        return bookmarkRepository.findBySocketIdAndMemberId(socketId, memberId).orElseThrow();
    }


}
