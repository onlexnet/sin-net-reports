package sinnet

import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions
import sinnet.reports.{ReportRequest => ReportRequestDTO, ActivityDetails => ActivityDetailsDTO}

class MapperTests {
  
  @Test
  def shouldConvertWhenEmptyActivityIsProvided(): Unit = {
    var minModel = Mapper(ReportRequestDTO.newBuilder().addDetails(ActivityDetailsDTO.newBuilder().build()).build())
  }
}