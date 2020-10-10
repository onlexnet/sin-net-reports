package sinnet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/debug")
@Profile("dev")
public class DebugController {

    @Autowired
    private AuthenticationController authenticationController;

    @GetMapping(path = "/user/{email}")
    public void setUser(@PathVariable("email") String email) {
        authenticationController.setEmail(email);
    }
}
