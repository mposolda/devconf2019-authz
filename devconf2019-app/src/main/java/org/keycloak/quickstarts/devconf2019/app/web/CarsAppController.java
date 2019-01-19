package org.keycloak.quickstarts.devconf2019.app.web;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import net.rossillo.spring.web.mvc.CacheControl;
import net.rossillo.spring.web.mvc.CachePolicy;
import org.jboss.logging.Logger;
import org.keycloak.common.util.Base64Url;
import org.keycloak.common.util.KeycloakUriBuilder;
import org.keycloak.constants.ServiceUrlConstants;
import org.keycloak.quickstarts.devconf2019.app.service.CarsClientService;
import org.keycloak.quickstarts.devconf2019.app.util.AppTokenUtil;
import org.keycloak.quickstarts.devconf2019.service.CarRepresentation;
import org.keycloak.quickstarts.devconf2019.service.InMemoryCarsDB;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpServerErrorException;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
@Controller
@CacheControl(policy = CachePolicy.NO_CACHE)
public class CarsAppController {

    private static final Logger log = Logger.getLogger(InMemoryCarsDB.class);


    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Autowired
    private CarsClientService carsClientService;

    private @Autowired
    HttpServletRequest request;

    private @Autowired
    HttpServletResponse response;

    // TODO: Obtain through the ApplicationContext?
    private static final String AUTH_SERVER_URL = "http://localhost:8180/auth";

    private static final String REALM_NAME = "cars";

    private static final String CLIENT_ID = "cars-app";

    @RequestMapping(value = "/app", method = RequestMethod.GET)
    public String showCarsPage(Principal principal, Model model) {
        model.addAttribute("cars", carsClientService.getCars());
        model.addAttribute("principal",  principal);

        String logoutUri = KeycloakUriBuilder.fromUri(AUTH_SERVER_URL).path(ServiceUrlConstants.TOKEN_SERVICE_LOGOUT_PATH)
                .queryParam("redirect_uri", "http://localhost:8080/app").build(REALM_NAME).toString();
        model.addAttribute("logout",  logoutUri);

        String accountUri = KeycloakUriBuilder.fromUri(AUTH_SERVER_URL).path(ServiceUrlConstants.ACCOUNT_SERVICE_PATH)
                .queryParam("referrer", CLIENT_ID).build(REALM_NAME).toString();
        model.addAttribute("accountUri", accountUri);

        AccessToken token = AppTokenUtil.getAccessToken(principal);
        model.addAttribute("token", token);

        return "cars";
    }


    @RequestMapping(value = "/app/create-car", method = RequestMethod.GET)
    public String createRandomCar(Principal principal, Model model) {
        try {
            CarRepresentation newCar = carsClientService.createCar();
            log.infof("Created new car %s for user %s", newCar.getId(), newCar.getOwner().getUsername());
        } catch (HttpServerErrorException ex) {
            log.error("Failed to create car for user " + principal.getName(), ex);
            model.addAttribute("app_error", "Failed to create new car");
        }

        // Just re-show the page
        return showCarsPage(principal, model);
    }


    @RequestMapping(value = "/app/details/{carId}", method = RequestMethod.GET)
    public String getCarDetails(Principal principal, Model model, @PathVariable String carId) {
        CarRepresentation detailedCar = carsClientService.getCarWithDetails(carId); // TODO: I don't need picture here. Improve...

        model.addAttribute("car", detailedCar);
        return "car-detail";
    }


    @RequestMapping(value = "/app/delete/{carId}", method = RequestMethod.GET)
    public String deleteCar(Principal principal, Model model, @PathVariable String carId) {
        carsClientService.deleteCar(carId);
        return showCarsPage(principal, model);
    }


    @RequestMapping(value = "/app/img/{carId}", method = RequestMethod.GET)
    @ResponseBody
    public void getCarImg(Principal principal, Model model, @PathVariable String carId) throws IOException {
        CarRepresentation detailedCar = carsClientService.getCarWithDetails(carId);
        String imgString = detailedCar.getBase64Img();

        response.setContentType("image/jpeg");
        ServletOutputStream outputStream = response.getOutputStream();

        byte[] decodedPicture = Base64Url.decode(imgString);
        outputStream.write(decodedPicture);

        outputStream.flush();
    }


    @RequestMapping(value = "/app/show-token", method = RequestMethod.GET)
    public String showToken(Principal principal, Model model) throws ServletException, IOException {
        AccessToken token = AppTokenUtil.getAccessToken(principal);

        model.addAttribute("token", token);

        String tokenString = mapper.writeValueAsString(token);
        model.addAttribute("tokenString", tokenString);

        return "token";
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
