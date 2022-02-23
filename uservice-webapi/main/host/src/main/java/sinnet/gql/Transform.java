package sinnet.gql;

import java.util.function.BiFunction;

import org.slf4j.Logger;

import io.vavr.Function1;
import lombok.experimental.UtilityClass;
import sinnet.gql.exceptions.InternalException;
import sinnet.grpc.roles.GetReply;
import sinnet.grpc.roles.GetReply.Role;

@UtilityClass
public class Transform {
    public static <T> BiFunction<GetReply, Throwable, T> secured(Logger log, Function1<GetReply, T> handler) {
        return (it, ex) -> {
            if (ex != null) {
                log.error("Internal error", ex);
                throw new InternalException();
            }
            if (it.getRole() == Role.NONE) {
                log.error("Permission denied");
                throw new InternalException();
            }
            return handler.apply(it);
        };
    }

    public static <T1, T2> BiFunction<T1, Throwable, T2> logged(Logger log, Function1<T1, T2> handler) {
        return (it, ex) -> {
            if (ex != null) {
                log.error("Internal error", ex);
                throw new InternalException();
            }
            return handler.apply(it);
        };
    }
}
