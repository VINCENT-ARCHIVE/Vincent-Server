package com.vincent.domain.feedback.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.vincent.domain.feedback.TestFeedbackRepository;
import com.vincent.domain.feedback.entity.Feedback;
import com.vincent.domain.feedback.repository.FeedbackRepository;
import com.vincent.domain.feedback.service.data.FeedbackDataService;
import com.vincent.domain.member.TestMemberRepository;
import com.vincent.domain.member.entity.Member;
import com.vincent.domain.member.service.data.MemberDataService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



public class FeedbackServiceTest {

    private FeedbackRepository feedbackRepository;
    private FeedbackService feedbackService;
    private MemberDataService memberDataService;
    private FeedbackDataService feedbackDataService;

    @BeforeEach
    void setUp() {
        feedbackRepository = new TestFeedbackRepository();
        feedbackDataService = new FeedbackDataService(feedbackRepository);
        memberDataService = new MemberDataService(new TestMemberRepository());
        feedbackService = new FeedbackService(feedbackDataService, memberDataService);
    }

    @Test
    public void 피드백생성성공() {
        //given
        String contents = "test";
        Member member = Member.builder().id(1L).build();
        memberDataService.save(member);

        //when
        feedbackService.addFeedback(contents, member.getId());

        //then
        List<Feedback> feedbacks = feedbackRepository.findAll();
        assertFalse(feedbacks.isEmpty(), "피드백 리스트는 비어있지 않아야 합니다.");
        Optional<Feedback> savedFeedback = feedbackRepository.findAll().stream()
            .filter(f -> f.getContent().equals(contents) && f.getMember().getId().equals(member.getId()))
            .findFirst();

        assertTrue(savedFeedback.isPresent(), "피드백이 저장되어야 합니다.");
        assertEquals(contents, savedFeedback.get().getContent());
        assertEquals(member.getId(), savedFeedback.get().getMember().getId());
    }

}
