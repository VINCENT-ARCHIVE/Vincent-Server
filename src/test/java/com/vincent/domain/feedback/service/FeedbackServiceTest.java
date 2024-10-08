package com.vincent.domain.feedback.service;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vincent.domain.feedback.entity.Feedback;
import com.vincent.domain.feedback.service.data.FeedbackDataService;
import com.vincent.domain.member.entity.Member;
import com.vincent.domain.member.service.data.MemberDataService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FeedbackServiceTest {

    @InjectMocks
    private FeedbackService feedbackService;

    @Mock
    private MemberDataService memberDataService;

    @Mock
    private FeedbackDataService feedbackDataService;

    @Test
    public void 피드백생성성공() {
        //given
        Long memberId = 1L;
        String contents = "test";
        Member member = Member.builder().id(1L).build();
        Feedback feedback = Feedback.builder()
            .id(1L)
            .content(contents)
            .member(member)
            .build();

        //when
        when(memberDataService.findById(memberId)).thenReturn(member);
        when(feedbackDataService.save(any(Feedback.class))).thenReturn(feedback);

        //then
        feedbackService.addFeedback(contents, memberId);

        verify(feedbackDataService, times(1)).save(any(Feedback.class));
        verify(memberDataService, times(1)).findById(memberId);

    }

}
