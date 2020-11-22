package sinnet;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/to-delete")
public class ToDeleteController {

    @GetMapping
    public String get() {
        return "OK2BG";
    }
}
