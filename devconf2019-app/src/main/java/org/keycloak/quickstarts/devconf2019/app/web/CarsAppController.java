package org.keycloak.quickstarts.devconf2019.app.web;

import java.security.Principal;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import net.rossillo.spring.web.mvc.CacheControl;
import net.rossillo.spring.web.mvc.CachePolicy;
import org.keycloak.common.util.KeycloakUriBuilder;
import org.keycloak.constants.ServiceUrlConstants;
import org.keycloak.quickstarts.devconf2019.app.service.CarsClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
@Controller
@CacheControl(policy = CachePolicy.NO_CACHE)
public class CarsAppController {

    @Autowired
    private CarsClientService carsClientService;

    private @Autowired
    HttpServletRequest request;

    @RequestMapping(value = "/app", method = RequestMethod.GET)
    public String handleCustomersRequest(Principal principal, Model model) {
        model.addAttribute("cars", carsClientService.getCars());
        model.addAttribute("principal",  principal);
        String logoutUri = KeycloakUriBuilder.fromUri("http://localhost:8180/auth").path(ServiceUrlConstants.TOKEN_SERVICE_LOGOUT_PATH)
                .queryParam("redirect_uri", "http://localhost:8080/app").build("cars").toString();
        model.addAttribute("logout",  logoutUri);
        return "cars";
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String handleLogoutt() throws ServletException {
        request.logout();
        return "landing";
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String landing() throws ServletException {
        return "landing";
    }
}
