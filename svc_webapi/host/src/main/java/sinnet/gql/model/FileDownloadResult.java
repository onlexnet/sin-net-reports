package sinnet.gql.model;

import lombok.Builder;

/**
 * Model representing a file download result for GraphQL.
 * This is a data structure containing the file information.
 */
@Builder
public record FileDownloadResult(String fileName, String content, String contentType) {
}