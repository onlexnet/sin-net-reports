package sinnet.handlers;

import java.io.ByteArrayOutputStream;

import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import io.dapr.client.domain.PublishEventRequest;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.SneakyThrows;
import reactor.core.Disposable.Composite;
import reactor.core.Disposables;
import sinnet.project.events.ProjectCreatedEvent;

@Component
class ProjectCreatedPublisher {

  Composite selfDisposable = Disposables.composite();
  DaprClient client;
  
  @PostConstruct
  void init() {
    client = new DaprClientBuilder()
        .build();
  }

  @SneakyThrows
  @EventListener
  public void on(ProjectCreatedEvent event) {
    var wr = new SpecificDatumWriter<>(ProjectCreatedEvent.class);
    var stream = new ByteArrayOutputStream();
    var encoder = EncoderFactory.get().jsonEncoder(ProjectCreatedEvent.SCHEMA$, stream);
    wr.write(event, encoder);
    encoder.flush();
    var data = stream.toByteArray();

    var request = new PublishEventRequest("pubsub", "MYTOPICNAME", data);
    client.publishEvent(request).subscribe();
  }

  @PreDestroy
  public void close() {
    selfDisposable.dispose();
  }
}
