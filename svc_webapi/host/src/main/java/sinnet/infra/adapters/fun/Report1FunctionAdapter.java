package sinnet.infra.adapters.fun;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.jspecify.annotations.Nullable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import lombok.RequiredArgsConstructor;
import sinnet.app.ports.out.Report1FunctionOutPort;
import sinnet.report1.grpc.ReportRequests;

@Component
@RequiredArgsConstructor
class Report1FunctionAdapter implements Report1FunctionOutPort {

  private static final String SHARED_SECRET_HEADER = "X-Report1-Secret";

  private final RestClient.Builder restClientBuilder;
  private final Report1FunctionProperties properties;

  @Override
  public ReportLink producePack(ReportRequests request) {
    var requestBody = mapRequest(request);
    var client = restClientBuilder
        .baseUrl(properties.baseUrl())
        .build();

    var response = client
        .post()
        .uri(properties.zipPath())
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .header(SHARED_SECRET_HEADER, properties.sharedSecret())
        .body(requestBody)
        .retrieve()
        .body(ReportLinkDto.class);

    return Objects.requireNonNull(response, "fun_report1 returned an empty body")
        .toReportLink();
  }

  private static ReportRequestsDto mapRequest(ReportRequests request) {
    var items = request.getItemsList().stream()
        .map(it -> {
          var customer = it.getCustomer();
          var activities = it.getDetailsList().stream()
              .map(detail -> {
                var when = detail.hasWhen()
                    ? new ActivityDateDto(
                        detail.getWhen().getYear(),
                        detail.getWhen().getMonth(),
                        detail.getWhen().getDayOfTheMonth())
                    : null;
                return new ActivityDetailsDto(
                    detail.getDescription(),
                    detail.getWho(),
                    when,
                    detail.getHowLongInMins(),
                    detail.getHowFarInKms());
              })
              .toList();

          return new ReportRequestDto(
              new CustomerDetailsDto(
                  customer.getCustomerId(),
                  customer.getCustomerName(),
                  customer.getCustomerCity(),
                  customer.getCustomerAddress()),
              activities);
        })
        .toList();

    return new ReportRequestsDto(items);
  }

  private record ReportRequestsDto(List<ReportRequestDto> items) {
  }

  private record ReportRequestDto(CustomerDetailsDto customer, List<ActivityDetailsDto> activities) {
  }

  private record CustomerDetailsDto(
      @JsonProperty("customer_id") String customerId,
      @JsonProperty("customer_name") String customerName,
      @JsonProperty("customer_city") String customerCity,
      @JsonProperty("customer_address") String customerAddress) {
  }

  private record ActivityDetailsDto(
      String description,
      String who,
      @Nullable ActivityDateDto when,
      @JsonProperty("how_long_in_mins") int howLongInMins,
      @JsonProperty("how_far_in_kms") int howFarInKms) {
  }

  private record ActivityDateDto(int year, int month, int day) {
  }

  /** Mirrors fun_report1's ReportLinkResponse JSON body ({@code url}, {@code expires_at}). */
  private record ReportLinkDto(String url, @JsonProperty("expires_at") Instant expiresAt) {
    ReportLink toReportLink() {
      return new ReportLink(url, expiresAt);
    }
  }
}
