package sinnet.grpc.reports.report1;

import java.io.ByteArrayOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.stereotype.Component;

import com.google.protobuf.ByteString;

import io.vavr.collection.Iterator;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import sinnet.grpc.mapping.RpcQueryHandler;
import sinnet.report1.grpc.ReportRequests;
import sinnet.reports.grpc.Response;

@Component
@RequiredArgsConstructor
class Report1QueryPack implements RpcQueryHandler<ReportRequests, Response> {
  
  @Override
  @SneakyThrows
  public Response apply(ReportRequests request) {

    var baos = new ByteArrayOutputStream();
    var zos = new ZipOutputStream(baos);

    Iterator.ofAll(request.getItemsList())
        .zipWithIndex()
        .toJavaStream()
        .forEach(arg -> {
          var itemDto = arg._1;
          var item = Models.Mapper.map(itemDto);
          var index = arg._2 + 1;
          var model = ReportResults.apply(item);
          val report = model.content();
          val fileName = String.format("%03d-%s.pdf", index, item.customer().customerName());
          val normalizedFileName = fileName.replace("/", "_");
          val entry = new ZipEntry(normalizedFileName);
          Try.run(() -> {
            zos.putNextEntry(entry);
            zos.write(report);
            zos.closeEntry();
          });
        });
    
    zos.close();
    baos.close();

    val binaryData = baos.toByteArray();
    val dtoData = ByteString.copyFrom(binaryData);
    return Response
      .newBuilder()
      .setData(dtoData)
      .build();

      
  }

}
