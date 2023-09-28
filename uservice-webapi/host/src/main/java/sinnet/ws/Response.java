package sinnet.ws;

import org.springframework.http.ResponseEntity;

import lombok.experimental.UtilityClass;

@UtilityClass
class Response {
  
  static ResponseEntity<byte[]> asResponseEntity(byte[] data, String fileName) {
    return ResponseEntity.ok()
        .header("Cache-Control", "no-cache, no-store, must-revalidate")
        .header("Content-Disposition", "inline; filename=" + fileName)
        .header("Expires", "0")
        .header("Content-Length", Integer.toString(data.length))
        .header("Content-Type", "application/zip")
        .body(data);

    // var headers = new HttpHeaders();
    // headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
    // headers.add("Content-Disposition", "inline; filename=report-3.pdf");
    // headers.add("Expires", "0");
    // headers.add(HttpHeaders.CONTENT_LENGTH, Integer.toString(result.length));
    // headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM.toString());

  }
}
