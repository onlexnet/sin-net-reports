package sinnet;

import java.util.concurrent.CompletableFuture;

import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.commandhandling.callbacks.FutureCallback;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Component;

public interface CommandEntry {
    CompletableFuture<CommandResultMessage<?>> send(Object command);
}

@Component
class CommandEntryImpl implements CommandEntry {

    private final CommandGateway commandGateway;
    CommandEntryImpl(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @Override
    public CompletableFuture<CommandResultMessage<?>> send(Object command) {
        var callback = new FutureCallback<Object, Object>();
        commandGateway.send(command, callback);
        return callback;
    }
}
