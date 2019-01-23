package org.keycloak.quickstarts.devconf2019.app.web;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

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
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.common.util.Base64Url;
import org.keycloak.common.util.KeycloakUriBuilder;
import org.keycloak.constants.ServiceUrlConstants;
import org.keycloak.quickstarts.devconf2019.app.config.AppConfig;
import org.keycloak.quickstarts.devconf2019.app.config.AuthzClientRequestFactory;
import org.keycloak.quickstarts.devconf2019.app.config.RptStore;
import org.keycloak.quickstarts.devconf2019.app.service.CarsClientService;
import org.keycloak.quickstarts.devconf2019.app.service.ObjectMapperProvider;
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

    @Autowired
    private ObjectMapperProvider mapperProvider;

    @Autowired
    private CarsClientService carsClientService;

    private @Autowired
    HttpServletRequest request;

    private @Autowired
    HttpServletResponse response;

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private RptStore rptStore;


    @RequestMapping(value = "/app", method = RequestMethod.GET)
    public String showCarsPage(Principal principal, Model model) {
        boolean isCreateCarAllowed = carsClientService.isCreateCarAllowed(principal);
        model.addAttribute("create_car_allowed", isCreateCarAllowed);

        Map<String, List<CarRepresentation>> cars = carsClientService.getCars();
        model.addAttribute("cars", cars);
        model.addAttribute("principal",  principal);

        String logoutUri = KeycloakUriBuilder.fromUri(appConfig.getAuthServerUrl()).path(ServiceUrlConstants.TOKEN_SERVICE_LOGOUT_PATH)
                .queryParam("redirect_uri", "http://localhost:8080/app").build(appConfig.getRealmName()).toString();
        model.addAttribute("logout",  logoutUri);

        String accountUri = KeycloakUriBuilder.fromUri(appConfig.getAuthServerUrl()).path(ServiceUrlConstants.ACCOUNT_SERVICE_PATH)
                .queryParam("referrer", appConfig.getClientId()).build(appConfig.getRealmName()).toString();
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
        } catch (RuntimeException ex) {
            log.error("Failed to create car for user " + principal.getName(), ex);
            model.addAttribute("app_error", "Failed to create new car");
        }

        // Just re-show the page
        return showCarsPage(principal, model);
    }


    @RequestMapping(value = "/app/details/{carId}", method = RequestMethod.GET)
    public String getCarDetails(Principal principal, Model model, @PathVariable String carId) {
        CarsClientService.ClientCallResponse<CarRepresentation> response = carsClientService.getCarWithDetails(carId);

        String reqSubmitted = handleRequestSubmitted(response, principal, model);
        if (reqSubmitted != null) return reqSubmitted;

        CarRepresentation detailedCar = response.getResult();
        model.addAttribute("car", detailedCar);
        return "car-detail";

    }


    private String handleRequestSubmitted(CarsClientService.ClientCallResponse response, Principal principal, Model model) {
        if (response.isRequestSubmitted()) {
            model.addAttribute("app_error", "Submitted request to the car owner to grant you a permission");

            // Just re-show the page
            return showCarsPage(principal, model);
        } else {
            return null;
        }
    }


    @RequestMapping(value = "/app/delete/{carId}", method = RequestMethod.GET)
    public String deleteCar(Principal principal, Model model, @PathVariable String carId) {
        CarsClientService.ClientCallResponse<Void> clientResponse = carsClientService.deleteCar(carId);

        String reqSubmitted = handleRequestSubmitted(clientResponse, principal, model);
        if (reqSubmitted != null) return reqSubmitted;

        return showCarsPage(principal, model);
    }


    @RequestMapping(value = "/app/img/{carId}", method = RequestMethod.GET)
    @ResponseBody
    public void getCarImg(Principal principal, Model model, @PathVariable String carId) throws IOException {
        CarsClientService.ClientCallResponse<CarRepresentation> clientResponse = carsClientService.getCarWithDetails(carId);

        String reqSubmitted = handleRequestSubmitted(clientResponse, principal, model);
        // Shoudln't happen
        if (reqSubmitted != null) return;



        CarRepresentation detailedCar = clientResponse.getResult();
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

        String tokenString = mapperProvider.getMapper().writeValueAsString(token);
        model.addAttribute("tokenString", tokenString);

        return "token";
    }


    @RequestMapping(value = "/app/show-rpt", method = RequestMethod.GET)
    public String showRpt(Principal principal, Model model) throws ServletException, IOException {
        KeycloakSecurityContext securityCtx = AppTokenUtil.getKeycloakSecurityContext(principal);
        AccessToken rptToken = rptStore.getParsedRpt(securityCtx);

        model.addAttribute("token", rptToken);

        String tokenString = mapperProvider.getMapper().writeValueAsString(rptToken);
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
