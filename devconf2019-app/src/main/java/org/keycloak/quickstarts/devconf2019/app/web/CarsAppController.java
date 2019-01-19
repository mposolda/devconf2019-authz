package org.keycloak.quickstarts.devconf2019.app.web;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.rossillo.spring.web.mvc.CacheControl;
import net.rossillo.spring.web.mvc.CachePolicy;
import org.jboss.logging.Logger;
import org.keycloak.common.util.KeycloakUriBuilder;
import org.keycloak.constants.ServiceUrlConstants;
import org.keycloak.quickstarts.devconf2019.app.service.CarsClientService;
import org.keycloak.quickstarts.devconf2019.service.CarRepresentation;
import org.keycloak.quickstarts.devconf2019.service.InMemoryCarsDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.HttpServerErrorException;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
@Controller
@CacheControl(policy = CachePolicy.NO_CACHE)
public class CarsAppController {

    private static final Logger log = Logger.getLogger(InMemoryCarsDB.class);

    @Autowired
    private CarsClientService carsClientService;

    private @Autowired
    HttpServletRequest request;

    private @Autowired
    HttpServletResponse response;

    @RequestMapping(value = "/app", method = RequestMethod.GET)
    public String showCarsPage(Principal principal, Model model) {
        model.addAttribute("cars", carsClientService.getCars());
        model.addAttribute("principal",  principal);
        String logoutUri = KeycloakUriBuilder.fromUri("http://localhost:8180/auth").path(ServiceUrlConstants.TOKEN_SERVICE_LOGOUT_PATH)
                .queryParam("redirect_uri", "http://localhost:8080/app").build("cars").toString();
        model.addAttribute("logout",  logoutUri);
        return "cars";
    }


    @RequestMapping(value = "/app/create-car", method = RequestMethod.GET)
    public String createRandomCar(Principal principal, Model model) {
        try {
            CarRepresentation newCar = carsClientService.createCar();
            log.infof("Created new car %s for user %s", newCar.getName(), newCar.getOwner().getOwnerUsername());
        } catch (HttpServerErrorException ex) {
            log.error("Failed to create car for user " + principal.getName(), ex);
            model.addAttribute("app_error", "Failed to create new car");
        }

        // Just re-show the page
        return showCarsPage(principal, model);
    }


    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String handleLogoutt() throws ServletException, IOException {
        request.logout();

        response.sendRedirect("/app");
        return "foo";
    }


    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String landing() throws ServletException, IOException {
        response.sendRedirect("/app");
        return "foo";
    }
}
