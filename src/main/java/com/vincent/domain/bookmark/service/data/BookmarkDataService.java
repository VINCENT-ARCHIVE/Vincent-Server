package com.vincent.domain.bookmark.service.data;

import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.domain.bookmark.entity.Bookmark;
import com.vincent.domain.bookmark.repository.BookmarkRepository;
import com.vincent.domain.member.entity.Member;
import com.vincent.domain.socket.entity.Socket;
import com.vincent.exception.handler.ErrorHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookmarkDataService {

    private final BookmarkRepository bookmarkRepository;

    public void delete(Bookmark bookmark) {
        bookmarkRepository.delete(bookmark);
    }

    public Bookmark save(Bookmark bookmark) {
        return bookmarkRepository.save(bookmark);
    }

    public void isBookmarkExists(Socket socket, Member member) {
        Boolean exists = bookmarkRepository.existsBySocketAndMember(socket, member);
        if (exists) {
            throw new ErrorHandler(ErrorStatus.BOOKMARK_ALREADY_EXIST);
        }
    }

    public Page<Bookmark> findAllByMember(Member member, Integer page) {
        return bookmarkRepository.findAllByMemberOrderByCreatedAtDesc(member,
            PageRequest.of(page, 10));
    }

}
