package sinnet.grpc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import sinnet.grpc.timeentries.TimeEntryModel;

/** GRPC methods related to Actions (aka Services aka Timeentries). */
public interface ActionsGrpcFacade {

  /** REturns list of actions for requested project, limited result from-to range. */
  List<TimeEntryModel> searchInternal(UUID projectId, LocalDate from, LocalDate to);

  /** Returns list of actions for requested project, limited result from-to range. */
  default List<String> search(UUID projectId, LocalDate from, LocalDate to) {
    return searchInternal(projectId, from, to).stream().map(ActionsGrpcFacade::map).toList();
  }

  static String map(TimeEntryModel model) {
    return model.getDescription();
  }

}
