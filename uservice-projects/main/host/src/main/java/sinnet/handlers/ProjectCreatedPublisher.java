package sinnet.handlers;

import java.io.ByteArrayOutputStream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;

import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import io.dapr.client.domain.PublishEventRequest;
import lombok.SneakyThrows;
import reactor.core.Disposable.Composite;
import reactor.core.Disposables;
import sinnet.project.events.ProjectCreatedEvent;

@ApplicationScoped
class ProjectCreatedPublisher {

  Composite selfDisposable = Disposables.composite();
  DaprClient client;
  
  @PostConstruct
  void init() {
    client = new DaprClientBuilder()
        .build();
  }

  @SneakyThrows
  void on(@Observes ProjectCreatedEvent event) {
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
