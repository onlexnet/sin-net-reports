package sinnet;

import java.security.Principal;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/** Fixme. */
@RestController
@RequestMapping("/debug")
public class HelloController {

    /**
     * Fixme.
     * @param principal fixme
     * @return fixme
     */
    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    @ResponseBody
    public String helloWorld(@RequestParam(defaultValue = "no-name") String name) {
        return "debug please";
    }
  }
