package sinnet.gql.actions;

import java.time.LocalDate;
import java.util.UUID;

import sinnet.grpc.PropsBuilder;
import sinnet.grpc.timeentries.TimeEntryModel;
import sinnet.models.ActionValue;
import sinnet.models.Entity;
import sinnet.models.EntityId;

public interface Mapper extends sinnet.gql.common.Mapper {

    default LocalDate fromDto(sinnet.grpc.timeentries.LocalDate it) {
        if (it == null) return null;
        return LocalDate.of(it.getYear(), it.getMonth(), it.getDay());
    }

    default sinnet.grpc.timeentries.LocalDate.Builder toDto(LocalDate it) {
        if (it == null) return null;
        return sinnet.grpc.timeentries.LocalDate.newBuilder()
            .setYear(it.getYear())
            .setMonth(it.getMonthValue())
            .setDay(it.getDayOfMonth());
    }

    default TimeEntryModel toDto(Entity<ActionValue> it) {
        return PropsBuilder.build(TimeEntryModel.newBuilder())
            .set(it, o -> EntityId.of(o), this::toDto, b -> b::setEntityId)
            .set(it.getValue().getWhom(), UUID::toString, b -> b::setCustomerId)
            .set(it.getValue().getWho().getValue(), b -> b::setServicemanName)
            .set(it.getValue().getWhen(), this::toDto, b -> b::setWhenProvided)
            .set(it.getValue().getWhat(), b -> b::setDescription)
            .set(it.getValue().getHowLong().getValue(), b -> b::setDuration)
            .set(it.getValue().getHowFar().getValue(), b -> b::setDistance)
            .done().build();
    }

}
