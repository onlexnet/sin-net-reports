package sinnet.ws;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling requests to the root endpoint.
 */
@RestController
@RequestMapping("/")
public class RootController {

  /**
   * Handles GET requests to the root endpoint and returns HTTP 204 No Content.
   *
   * @return ResponseEntity with HTTP 204 status
   */
  @GetMapping
  public ResponseEntity<Void> root() {
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}