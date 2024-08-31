package com.vincent.domain.feedback.service.data;

import com.vincent.domain.feedback.entity.Feedback;
import com.vincent.domain.feedback.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedbackDataService {
    private final FeedbackRepository feedbackRepository;

    public Feedback save (Feedback feedback) {
        return feedbackRepository.save(feedback);
    }
}
