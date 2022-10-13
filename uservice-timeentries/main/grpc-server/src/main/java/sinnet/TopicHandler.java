package sinnet;

public interface TopicHandler {
    
    boolean canHandle(String topicName);

    void handle(Object data);
}
