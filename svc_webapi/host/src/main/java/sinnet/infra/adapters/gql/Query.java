package sinnet.infra.adapters.gql;

import java.util.UUID;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import sinnet.app.service.FileGenerationService;
import sinnet.domain.models.UserToken;
import sinnet.gql.model.FileDownloadResult;
import sinnet.web.AuthenticatedPrincipalResolver;

@Controller
@RequiredArgsConstructor
class Query {

  private final FileGenerationService fileGenerationService;
  private final AuthenticatedPrincipalResolver principalResolver;
  
  @QueryMapping("Projects")
  ProjectsQuery projects() {
    var primaryEmail = principalResolver.currentPrincipal().email();

    return new ProjectsQuery(primaryEmail);
  }

  @QueryMapping("Users")
  UsersQuery users(@Argument UUID projectId) {
    var primaryEmail = principalResolver.currentPrincipal().email();

    return new UsersQuery(projectId, primaryEmail);
  }

  @QueryMapping("Actions")
  ActionsQuery actions(@Argument String projectId) {
    var primaryEmail = principalResolver.currentPrincipal().email();

    return new ActionsQuery(projectId, primaryEmail);
  }

  @QueryMapping("Customers")
  CustomersQuery customers(@Argument UUID projectId) {
    var primaryEmail = principalResolver.currentPrincipal().email();

    var userToken = new UserToken(projectId, primaryEmail);
    return new CustomersQuery(userToken);
  }

  @QueryMapping("downloadFile")
  FileDownloadResult downloadFile(
      @Argument String projectId,
      @Argument int year,
      @Argument int month) {
    var primaryEmail = principalResolver.currentPrincipal().email();
    
    var base64Content = fileGenerationService.generateExcelAsBase64(projectId, year, month, primaryEmail);
    var fileName = fileGenerationService.generateLogicalFileName(year, month);
    
    return FileDownloadResult.builder()
        .fileName(fileName)
        .content(base64Content)
        .contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
        .build();
  }
}
