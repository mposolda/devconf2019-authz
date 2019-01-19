package org.keycloak.quickstarts.devconf2019.web;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.quickstarts.devconf2019.service.CarRepresentation;
import org.keycloak.quickstarts.devconf2019.service.CarsService;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.IDToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
@RestController
public class CarsServiceController {

    @Autowired
    private CarsService carsService;

    private @Autowired
    HttpServletRequest request;

    @GetMapping(value = "/cars", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, List<CarRepresentation>> getCars(Principal principal) {
        return carsService.getCars(null);
    }


    // Create (generate) new car for authenticated user. Then return the newly created car
    @PostMapping(value = "/cars/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public CarRepresentation generateCar(Principal principal) {
        AccessToken token = getBearerToken(principal);
        return carsService.generateCarForUser(token.getId(), token.getPreferredUsername());
    }


    @GetMapping(value = "/cars/details/{carId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CarRepresentation getCarDetails(Principal principal, @PathVariable String carId) {
        return carsService.getCarById(carId);
    }


    @DeleteMapping(value = "/cars/delete/{carId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteCar(Principal principal, @PathVariable String carId) {
        carsService.deleteCarById(carId);
    }


    private AccessToken getBearerToken(Principal principal) {
        KeycloakPrincipal kcPrincipal = (KeycloakPrincipal) principal;
        return kcPrincipal.getKeycloakSecurityContext().getToken();
    }

}


