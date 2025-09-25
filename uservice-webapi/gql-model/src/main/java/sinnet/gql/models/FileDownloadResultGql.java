package sinnet.gql.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Model representing a file download result for GraphQL queries. */
@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class FileDownloadResultGql {
  /** The logical filename of the file. */
  private String fileName;
  
  /** The base64 encoded content of the file. */
  private String content;
  
  /** The MIME type of the file. */
  private String contentType;
}