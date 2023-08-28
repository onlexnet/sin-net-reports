package sinnet.grpc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import sinnet.grpc.timeentries.TimeEntryModel;

/** GRPC methods related to Actions (aka Services aka Timeentries). */
public interface ActionsGrpcFacade {

  /** REturns list of actions for requested project, limited result from-to range. */
  public <T> List<T> search(UUID projectId, LocalDate from, LocalDate to, Function<TimeEntryModel, T> mapper);

}
