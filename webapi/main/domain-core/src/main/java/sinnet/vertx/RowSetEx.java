package sinnet.vertx;

import java.util.LinkedList;

import io.vavr.collection.List;
import io.vertx.sqlclient.RowSet;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RowSetEx {
    public static <R> List<R> flat(RowSet<R> rows) {
        var result = new LinkedList<R>();
        while (rows != null) {
            rows.iterator().forEachRemaining(result::add);
            rows = rows.next();
        }
        return List.ofAll(result);
    }
}
