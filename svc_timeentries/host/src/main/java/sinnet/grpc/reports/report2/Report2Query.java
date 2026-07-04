package sinnet.grpc.reports.report2;

import java.io.ByteArrayOutputStream;

import org.springframework.stereotype.Component;

import com.google.protobuf.ByteString;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import sinnet.grpc.mapping.RpcQueryHandler;
import sinnet.report2.grpc.ReportRequest;
import sinnet.reports.grpc.Response;

/** Exposes gRPC endpoints to allow produce PDF report based on requested data. */
@Component
@RequiredArgsConstructor
class Report2Query implements RpcQueryHandler<ReportRequest, Response> {

  private final DtoDomainMapper mapper;

  @Override
  @SneakyThrows
  public Response apply(ReportRequest request) {
    var domainRequest = mapper.fromDto(request);
    var baos = new ByteArrayOutputStream();
    try (baos) {
      var model = ReportResults.apply(domainRequest);
      var report = model.content();
      baos.write(report);
    }

    val binaryData = baos.toByteArray();
    val dtoData = ByteString.copyFrom(binaryData);
    return Response.newBuilder()
      .setData(dtoData)
      .build();
  }

}
