package sinnet.report1

import scala.collection.JavaConversions._

import scala.language.implicitConversions
import javax.inject.Singleton;
import io.grpc.stub.StreamObserver
import sinnet.report1.grpc.{
  ReportRequest => ReportRequestDTO,
  ReportRequests => ReportRequestsDTO,
  ReportsGrpc}
import sinnet.reports.grpc.Response
import scala.util.Try

import java.time.LocalDate
import resource._

import com.lowagie.text.Document
import com.lowagie.text.Font
import com.lowagie.text.Paragraph
import com.lowagie.text.alignment.HorizontalAlignment
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter
import java.io.ByteArrayOutputStream
import com.google.protobuf.ByteString
import io.grpc.Deadline
import io.grpc.Context
import java.util.zip.ZipOutputStream

import scala.collection.JavaConverters._
import java.util.UUID
import java.util.zip.ZipEntry
import com.lowagie.text.pdf.PdfTable
import java.time.format.DateTimeFormatter
import io.quarkus.grpc.GrpcService

/** Exposes gRPC endpoints to allow produce PDF report based on requested data. */
@GrpcService
class Report1Service extends ReportsGrpc.ReportsImplBase {

  override def produce(request: ReportRequestDTO, responseObserver: StreamObserver[Response]): Unit = {
    var requestModel = Mapper(request)
    val model = ReportResult(requestModel)
    val binaryData = model.content
    val dtoData = ByteString.copyFrom(binaryData)
    var response = Response
      .newBuilder()
      .setData(dtoData)
      .build()

    responseObserver.onNext(response)
    responseObserver.onCompleted()
  }

  override def producePack(request: ReportRequestsDTO, responseObserver: StreamObserver[Response]): Unit = {
    for (
      baos <- managed(new ByteArrayOutputStream());
      zos <- managed(new ZipOutputStream(baos))
    ) {

      for ((itemDto, index) <- request.getItemsList().asScala.zip(Stream from 1)) {
        val item = Mapper(itemDto)
        var model = ReportResult(item)
        val report = model.content
        val entry = new ZipEntry(
          s"$index-${item.customer.customerName}.pdf"
        )
        zos.putNextEntry(entry)
        zos.write(report)
        zos.closeEntry()
      }

      zos.close()

      val binaryData = baos.toByteArray()
      val dtoData = ByteString.copyFrom(binaryData)
      var response = Response
        .newBuilder()
        .setData(dtoData)
        .build()

      responseObserver.onNext(response)
      responseObserver.onCompleted()
    }
  }
}