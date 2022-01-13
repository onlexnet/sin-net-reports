package sinnet.report3

import scala.collection.JavaConversions._
import DtoDomainMapper._
import scala.language.implicitConversions
import javax.inject.Singleton;
import sinnet.report3.grpc.{ReportsGrpc, ReportRequest => ReportRequestDTO}
import io.grpc.stub.StreamObserver
import sinnet.reports.grpc.FileResponse
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
class Report3Service extends ReportsGrpc.ReportsImplBase {

  override def produce(request: ReportRequestDTO, responseObserver: StreamObserver[FileResponse]): Unit = {
    val result = produce(request)
    responseObserver.onNext(result)
    responseObserver.onCompleted()
  }

  private def produce(request: ReportRequestDTO): FileResponse = {
    // TODO close resource on exit
    val baos = new ByteArrayOutputStream()
    val model = ReportResult(request)
    val report = model.content
    baos.write(report)
    baos.close()

    val binaryData = baos.toByteArray()
    val dtoData = ByteString.copyFrom(binaryData)
    FileResponse.newBuilder()
      .setFileName("raport-3.pdf")
      .setData(dtoData)
      .build()
  }
}
