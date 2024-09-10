package com.vincent.domain.bookmark.service;

import com.vincent.domain.bookmark.entity.Bookmark;
import com.vincent.domain.bookmark.service.data.BookmarkDataService;
import com.vincent.domain.member.entity.Member;
import com.vincent.domain.member.service.data.MemberDataService;
import com.vincent.domain.socket.entity.Socket;
import com.vincent.domain.socket.service.data.SocketDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkService {

    private final MemberDataService memberDataService;
    private final SocketDataService socketDataService;
    private final BookmarkDataService bookmarkDataService;

//    @Transactional
//    public BookmarkResult bookmark(Long socketId, Long memberId) {
//
//        Member member = findMemberById(memberId);
//        Socket socket = findSocketById(socketId);
//
//        if (isBookmarkExists(socket, member)) {
//            throw new ErrorHandler(ErrorStatus.BOOKMARK_ALREADY_EXIST);
//        }
//
//        Bookmark bookmark = saveBookmark(member, socket);
//
//        return new BookmarkResult(bookmark.getId());
//    }

    @Transactional
    public Long bookmark(Long socketId, Long memberId) {
        Member member = memberDataService.findById(memberId);
        Socket socket = socketDataService.findById(socketId);
        bookmarkDataService.isBookmarkExists(socket, member);
        Bookmark bookmark = bookmarkDataService.save(
            Bookmark.builder().member(member).socket(socket).build());
        return bookmark.getId();
    }

//    @Transactional
//    public void deleteBookmark(Long socketId, Long memberId) {
//
//        Member member = findMemberById(memberId);
//        Socket socket = findSocketById(socketId);
//
//        if (!isBookmarkExists(socket, member)) {
//            throw new ErrorHandler(ErrorStatus.BOOKMARK_ALREADY_DELETED);
//        }
//
//        delete(member, socket);
//
//    }

    @Transactional
    public void deleteBookmark(Long socketId, Long memberId) {
        Member member = memberDataService.findById(memberId);
        Socket socket = socketDataService.findById(socketId);
        bookmarkDataService.isBookmarkDeleted(socket, member);
        bookmarkDataService.delete(Bookmark.builder().member(member).socket(socket).build());
    }

    public Page<Bookmark> findBookmarkList(Long memberId, Integer page) {
        Member member = memberDataService.findById(memberId);
        return bookmarkDataService.findAllByMember(member, page);
    }

    public Boolean getBookmarkExist(Long socketId, Long memberId) {
        Member member = memberDataService.findById(memberId);
        Socket socket = socketDataService.findById(socketId);
        return bookmarkDataService.existsBySocketAndMember(socket, member);
    }

//    public Page<Bookmark> findBookmarkList(Long memberId, Integer page) {
//
//        Member member = findMemberById(memberId);
//
//        return bookmarkRepository.findAllByMemberOrderByCreatedAtDesc(member,
//            PageRequest.of(page, 10));
//
//    }

//    public Boolean getBookmarkExist(Long socketId, Long memberId) {
//
//        Socket socket = findSocketById(socketId);
//        Member member = findMemberById(memberId);
//
//        return isBookmarkExists(socket, member);
//    }
//
//    @Getter
//    @AllArgsConstructor
//    public static class BookmarkResult {
//
//        private Long bookmarkId;
//    }
//
//
//    @Transactional
//    public Bookmark saveBookmark(Member member, Socket socket) {
//        return bookmarkRepository.save(Bookmark.builder().member(member).socket(socket).build());
//    }
//
//
//    public void delete(Member member, Socket socket) {
//        bookmarkRepository.delete(Bookmark.builder().member(member).socket(socket).build());
//    }
//
//
//    private Boolean isBookmarkExists(Socket socket, Member member) {
//        return bookmarkRepository.existsBySocketAndMember(socket, member);
//    }
//
//    private Member findMemberById(Long memberId) {
//        return memberRepository.findById(memberId)
//            .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
//    }
//
//    private Socket findSocketById(Long socketId) {
//        return socketRepository.findById(socketId)
//            .orElseThrow(() -> new ErrorHandler(ErrorStatus.SOCKET_NOT_FOUND));
//    }


}
