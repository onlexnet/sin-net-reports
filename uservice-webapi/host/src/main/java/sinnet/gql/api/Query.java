package sinnet.gql.api;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import sinnet.gql.model.FileDownloadResult;
import sinnet.gql.service.FileGenerationService;
import sinnet.grpc.common.UserToken;
import sinnet.web.AuthenticationToken;

@Controller
@RequiredArgsConstructor
class Query {

  private final FileGenerationService fileGenerationService;
  
  @QueryMapping("Projects")
  ProjectsQuery projects() {
    var authentication = (AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
    var primaryEmail = authentication.getPrincipal();

    return ProjectsQuery.of(primaryEmail);
  }

  @QueryMapping("Users")
  UsersQuery users(@Argument String projectId) {
    var authentication = (AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
    var primaryEmail = authentication.getPrincipal();

    return new UsersQuery(projectId, primaryEmail);
  }

  @QueryMapping("Actions")
  ActionsQuery actions(@Argument String projectId) {
    var authentication = (AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
    var primaryEmail = authentication.getPrincipal();

    return new ActionsQuery(projectId, primaryEmail);
  }

  @QueryMapping("Customers")
  CustomersQuery customers(@Argument String projectId) {
    var authentication = (AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
    var primaryEmail = authentication.getPrincipal();

    var userToken = UserToken.newBuilder()
        .setProjectId(projectId)
        .setRequestorEmail(primaryEmail)
        .build();
    return new CustomersQuery(userToken);
  }

  @QueryMapping("downloadFile")
  FileDownloadResult downloadFile(
      @Argument String projectId,
      @Argument int year,
      @Argument int month) {
    String base64Content = fileGenerationService.generateEmptyExcelAsBase64(projectId, year, month);
    String fileName = fileGenerationService.generateLogicalFileName(year, month);
    
    return FileDownloadResult.builder()
        .fileName(fileName)
        .content(base64Content)
        .contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
        .build();
  }
}
