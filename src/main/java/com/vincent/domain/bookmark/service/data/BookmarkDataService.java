package com.vincent.domain.bookmark.service.data;

import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.domain.bookmark.entity.Bookmark;
import com.vincent.domain.bookmark.repository.BookmarkRepository;
import com.vincent.domain.member.entity.Member;
import com.vincent.domain.socket.entity.Socket;
import com.vincent.exception.handler.ErrorHandler;
import java.util.Optional;
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

    public Boolean existsBySocketAndMember(Socket socket, Member member) {
        return bookmarkRepository.existsBySocketAndMember(socket, member);
    }

    public void isBookmarkExists(Socket socket, Member member) {
        Boolean exists = bookmarkRepository.existsBySocketAndMember(socket, member);
        if (exists) {
            throw new ErrorHandler(ErrorStatus.BOOKMARK_ALREADY_EXIST);
        }
    }

    public void isBookmarkDeleted(Socket socket, Member member) {
        Boolean exists = bookmarkRepository.existsBySocketAndMember(socket, member);
        if (!exists) {
            throw new ErrorHandler(ErrorStatus.BOOKMARK_ALREADY_DELETED);
        }

    }

    public Bookmark findByMemberAndSocket(Member member, Socket socket) {

        return bookmarkRepository.findByMemberAndSocket(member, socket).orElseThrow(
            () -> new ErrorHandler(ErrorStatus.BOOKMARK_NOT_FOUND));
    }

    public Page<Bookmark> findAllByMember(Member member, Integer page) {
        return bookmarkRepository.findAllByMemberOrderByCreatedAtDesc(member,
            PageRequest.of(page, 10));
    }

}
