package sinnet;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import sinnet.events.ProjectCreated;

@Component
@RequiredArgsConstructor
final class TestTopicHandler implements TopicHandler {

    private final ObjectMapper mapper;
    private final ApplicationEventPublisher applicationEventPublisher;
    
    @Override
    public boolean canHandle(String topicName) {
        return true; // it is the only handler now
    }

    @Override
    public void handle(Object data) {
    }
    
}
