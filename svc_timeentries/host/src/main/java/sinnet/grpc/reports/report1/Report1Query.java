package sinnet.grpc.reports.report1;

import org.springframework.stereotype.Component;

import com.google.protobuf.ByteString;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import sinnet.grpc.mapping.RpcQueryHandler;
import sinnet.grpc.reports.report1.Models.Mapper;
import sinnet.report1.grpc.ReportRequest;
import sinnet.reports.grpc.Response;

@Component
@RequiredArgsConstructor
class Report1Query implements RpcQueryHandler<ReportRequest, Response> {
  
  @Override
  @SneakyThrows
  public Response apply(ReportRequest request) {
    var requestModel = Mapper.map(request);
    var model = ReportResults.apply(requestModel);
    var binaryData = model.content();
    var dtoData = ByteString.copyFrom(binaryData);
    var response = Response
        .newBuilder()
        .setData(dtoData)
        .build();
    return response;
  }

}
