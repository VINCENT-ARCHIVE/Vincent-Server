package com.vincent.domain.bookmark.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vincent.apipayload.status.ErrorStatus;
import com.vincent.domain.feedback.entity.Feedback;
import com.vincent.domain.feedback.repository.FeedbackRepository;
import com.vincent.domain.feedback.service.FeedbackService;
import com.vincent.domain.member.entity.Member;
import com.vincent.domain.member.repository.MemberRepository;
import com.vincent.exception.handler.ErrorHandler;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class FeedbackServiceTest {

  @InjectMocks
  private FeedbackService feedbackService;

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private FeedbackRepository feedbackRepository;

  @Mock
  private Member member;

  @Mock
  private Feedback feedback;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);

  }

  Long memberId = 1L;
  String contents = "test contents";

  @Test
  public void 피드백생성성공() {


    when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

    feedbackService.addFeedback(contents, memberId);

    verify(feedbackRepository, times(1)).save(any(Feedback.class));
  }

  @Test
  public void 피드백생성실패_멤버없음() {

    when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

    ErrorHandler thrown = assertThrows(ErrorHandler.class, () -> {
      feedbackService.addFeedback(contents, memberId);
    });

    assertEquals(ErrorStatus.MEMBER_NOT_FOUND, thrown.getCode());
    verify(feedbackRepository, never()).save(any(Feedback.class));
  }

}
