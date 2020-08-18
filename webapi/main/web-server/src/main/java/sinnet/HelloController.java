package sinnet;

import java.security.Principal;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.minidev.json.JSONArray;

/** Fixme. */
@RestController
public class HelloController {

    /**
     * Fixme.
     * @param principal fixme
     * @return fixme
     */
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    @ResponseBody
    public String helloWorld(final Principal principal) {
        var a = (OAuth2AuthenticationToken) principal;
        var p = (DefaultOidcUser) a.getPrincipal();
        var claimsUntyped = p.getIdToken().<JSONArray>getClaim("roles");
        for (var key : claimsUntyped) {
            var k = (String) key;
            System.out.println(k);
        }

        return a.getName();
    }

    /**
     * Fixme.
     * @param principal fixme
     * @return fixme
     */
    @RequestMapping(value = "/roles", method = RequestMethod.GET)
    @ResponseBody
    public String helloWorld2(final Principal principal) {
        return principal.getName();
    }

}
