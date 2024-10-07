package com.vincent.domain.feedback.service;

import com.vincent.domain.feedback.entity.Feedback;
import com.vincent.domain.feedback.service.data.FeedbackDataService;
import com.vincent.domain.member.entity.Member;
import com.vincent.domain.member.service.data.MemberDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackDataService feedbackDataService;
    private final MemberDataService memberDataService;

    @Transactional
    public void addFeedback(String contents, Long memberId) {

        Member member = memberDataService.findById(memberId);

        Feedback feedback = Feedback.builder()
            .content(contents)
            .member(member)
            .build();

        feedbackDataService.save(feedback);
    }

}
