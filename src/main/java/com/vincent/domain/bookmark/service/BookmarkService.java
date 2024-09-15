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

    @Transactional
    public Long bookmark(Long socketId, Long memberId) {
        Member member = memberDataService.findById(memberId);
        Socket socket = socketDataService.findById(socketId);
        bookmarkDataService.isBookmarkExists(socket, member);
        Bookmark bookmark = bookmarkDataService.save(
            Bookmark.builder().member(member).socket(socket).build());
        return bookmark.getId();
    }

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
}
