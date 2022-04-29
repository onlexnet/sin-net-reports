package sinnet;

public interface EntityRoot<TState, TCommand> {
    void init(TState initial);
    void on(TCommand cmd);
}
