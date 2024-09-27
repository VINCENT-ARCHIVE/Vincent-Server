package com.vincent.domain.feedback.service.data;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vincent.domain.feedback.entity.Feedback;
import com.vincent.domain.feedback.repository.FeedbackRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FeedbackDataServiceTest {

    @Mock
    private FeedbackRepository feedbackRepository;

    @InjectMocks
    private FeedbackDataService feedbackDataService;

    @Test
    void 저장() {
        //given
        Feedback feedback = Feedback.builder().id(1L).build();

        //when
        when(feedbackRepository.save(feedback)).thenReturn(feedback);

        //then
        Feedback result = feedbackDataService.save(feedback);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(result, feedback);
        verify(feedbackRepository, times(1)).save(feedback);
    }
}
