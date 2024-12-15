package com.vincent.domain.feedback.service.data;

import com.vincent.domain.feedback.TestFeedbackRepository;
import com.vincent.domain.feedback.entity.Feedback;
import com.vincent.domain.feedback.repository.FeedbackRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FeedbackDataServiceTest {

    private FeedbackRepository feedbackRepository;
    private FeedbackDataService feedbackDataService;

    @BeforeEach
    void setUp() {
        feedbackRepository = new TestFeedbackRepository();
        feedbackDataService = new FeedbackDataService(feedbackRepository);
    }

    @Test
    void 저장() {
        //given
        Feedback feedback = Feedback.builder().id(1L).build();

        //when
        Feedback result = feedbackDataService.save(feedback);

        //then
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result, feedback);
    }
}
