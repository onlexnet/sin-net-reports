package sinnet

import javax.inject.Singleton;
import sinnet.reports.ReportsGrpc
import io.grpc.stub.StreamObserver
import sinnet.reports.{ReportRequest, Response}
import scala.util.Try

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
import sinnet.reports.ReportRequests
import java.util.zip.ZipOutputStream

import scala.collection.JavaConverters._
import java.util.UUID
import java.util.zip.ZipEntry

@Singleton
class ReportService extends ReportsGrpc.ReportsImplBase {
  override def produce(request: ReportRequest, responseObserver: StreamObserver[Response]): Unit = {
    val binaryData = produceReport(request)
    val dtoData = ByteString.copyFrom(binaryData)
    var response = Response.newBuilder()
        .setData(dtoData)
        .build()

    responseObserver.onNext(response)
    responseObserver.onCompleted()
  }

  def produceReport(request: ReportRequest): Array[Byte] = {
    var customer = request.getCustomer()
    var customerName = Option(customer.getCustomerName()).getOrElse("Brak przypisanego kontrahenta")
    var customerCity = Option(customer.getCustomerCity()).getOrElse("")
    var customerAddress = Option(customer.getCustomerAddress()).getOrElse("")

    val fontSize = 10
    val baseFont = new Font(Font.TIMES_ROMAN, fontSize, Font.NORMAL)

    val header = s"$customerName $customerCity $customerAddress"
    val headParam = new Paragraph(header, baseFont)

    val d = managed(new ByteArrayOutputStream()) map {
      os => 
        val document = new Document()
        val pdfWriter = PdfWriter.getInstance(document, os)
        document.open()
        document.add(headParam);
        document.add(new Paragraph("-"))

        // We have to invoke close method so that content of the document is written
        // to os and can be obtained as the result of the whole operation
        document.close()

        os.toByteArray()
    }

    d.opt match {
      case Some(value) => value
      case None => Array.emptyByteArray
    }
    
    
  }

  override def producePack(request: ReportRequests, responseObserver: StreamObserver[Response]): Unit = {
    for (
      baos <- managed(new ByteArrayOutputStream());
      zos <- managed(new ZipOutputStream(baos))) {

      for ( 
        item <- request.getItemsList().asScala;
        (report, index) <- produceReport(item).zip(Stream from 1) ) {
        val entry = new ZipEntry( s"$index-${ item.getCustomer.getCustomerName() }.pdf")
        zos.putNextEntry(entry)
        zos.write(report)
        zos.closeEntry()
      }
      
      val binaryData = baos.toByteArray()
      val dtoData = ByteString.copyFrom(binaryData)
      var response = Response.newBuilder()
          .setData(dtoData)
          .build()

      responseObserver.onNext(response)
      responseObserver.onCompleted()
    }
  }
}
