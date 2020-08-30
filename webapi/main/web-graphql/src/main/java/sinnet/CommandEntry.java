package sinnet;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.commandhandling.GenericCommandMessage;
import org.axonframework.commandhandling.callbacks.FutureCallback;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Component;

public interface CommandEntry {
    CompletableFuture<CommandResultMessage<?>> send(Object command, UUID correlactionId);
}

@Component
class CommandEntryImpl implements CommandEntry {

    private final CommandGateway commandGateway;
    CommandEntryImpl(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @Override
    public CompletableFuture<CommandResultMessage<?>> send(Object command, UUID correlactionId) {
        var callback = new FutureCallback<Object, Object>();
        var msg = new GenericCommandMessage<>(command, Collections.singletonMap("correlactionId", correlactionId));
        commandGateway.send(msg, callback);
        return callback;
    }
}
