package com.vincent.domain.bookmark.service;

import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.domain.bookmark.entity.Bookmark;
import com.vincent.domain.bookmark.repository.BookmarkRepository;
import com.vincent.domain.member.entity.Member;
import com.vincent.domain.member.repository.MemberRepository;
import com.vincent.domain.socket.entity.Socket;
import com.vincent.domain.socket.repository.SocketRepository;
import com.vincent.exception.handler.ErrorHandler;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final MemberRepository memberRepository;
    private final SocketRepository socketRepository;

    @Transactional
    public BookmarkResult bookmark(Long socketId, Long memberId) {

        Member member = findMemberById(memberId);
        Socket socket = findSocketById(socketId);

        if (isBookmarkExists(socket, member)) {
            throw new ErrorHandler(ErrorStatus.BOOKMARK_ALREADY_EXIST);
        }

        Bookmark bookmark = saveBookmark(member, socket);

        return new BookmarkResult(bookmark.getId());
    }

  @Transactional
  public void deleteBookmark(Long socketId, Long memberId) {

    Member member = findMemberById(memberId);
    Socket socket = findSocketById(socketId);

    if (!isBookmarkExists(socket, member)) {
      {
        throw new ErrorHandler(ErrorStatus.BOOKMARK_ALREADY_DELETED);
      }
    }

    delete(member, socket);

  }

  public Page<Bookmark> findBookmarkList(Long memberId, Integer page) {

    Member member = findMemberById(memberId);

    return bookmarkRepository.findAllByMember(member, PageRequest.of(page, 10));

  }

    @Getter
    @AllArgsConstructor
    public static class BookmarkResult {

    private Long bookmarkId;
  }


  @Transactional
  public Bookmark saveBookmark(Member member, Socket socket) {
      return bookmarkRepository.save(Bookmark.builder().member(member).socket(socket).build());
    }




  public void delete(Member member, Socket socket) {
    bookmarkRepository.delete(Bookmark.builder()
        .member(member)
        .socket(socket)
        .build());
  }




    private Boolean isBookmarkExists(Socket socket, Member member) {
        return bookmarkRepository.existsBySocketAndMember(socket, member);
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
    }

    private Socket findSocketById(Long socketId) {
        return socketRepository.findById(socketId)
            .orElseThrow(() -> new ErrorHandler(ErrorStatus.SOCKET_NOT_FOUND));
    }


}
