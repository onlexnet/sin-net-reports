package sinnet

import scala.collection.JavaConversions._

import scala.language.implicitConversions
import javax.inject.Singleton;
import sinnet.reports.ReportsGrpc
import io.grpc.stub.StreamObserver
import sinnet.reports.{ReportRequest => ReportRequestDTO, ReportRequests => ReportRequestsDTO, Response}
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

@Singleton
class ReportService extends ReportsGrpc.ReportsImplBase {

  implicit def def4(x: sinnet.reports.Date) =
    LocalDate.of(x.getYear(), x.getMonth(), x.getDayOfTheMonth())
  implicit def def3(x: sinnet.reports.ActivityDetails) =
    ActivityDetails(x.getDescription(), x.getWho(), x.getWhen(), new TimeInMins(x.getHowLongInMins()), new DistanceInKms(x.getHowFarInKms()))
  implicit def def2(x: sinnet.reports.CustomerDetails) =
    CustomerDetails(x.getCustomerName(), x.getCustomerCity(), x.getCustomerAddress())
  implicit def def1(x: sinnet.reports.ReportRequest) =
    ReportRequest(x.getCustomer(), x.getDetailsList().toSeq.map(def3 _))

  override def produce(request: ReportRequestDTO, responseObserver: StreamObserver[Response]): Unit = {
    val model = ReportModel(request)
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

      for ((item, index) <- request.getItemsList.zip(Stream from 1)) {
        val model = ReportModel(item)
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
