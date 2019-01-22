package org.keycloak.quickstarts.devconf2019.app.service;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.validation.constraints.NotNull;

import org.jboss.logging.Logger;
import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.keycloak.quickstarts.devconf2019.app.config.AuthzClientRequestFactory;
import org.keycloak.quickstarts.devconf2019.app.config.RptStore;
import org.keycloak.quickstarts.devconf2019.app.config.UMAErrorHandler;
import org.keycloak.quickstarts.devconf2019.app.util.AppTokenUtil;
import org.keycloak.quickstarts.devconf2019.service.CarRepresentation;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
@Component
public class CarsClientService {

    private static final Logger log = Logger.getLogger(CarsClientService.class);

    @Autowired
    private KeycloakRestTemplate template;

    @Autowired
    public RptStore rptStore;

    @NotNull
    @Value("${cars.service.url}")
    private String endpoint;

    // A bit of workaround. As we want to see if the "Create car" button should be displayed or not, we need to to obtain RPT first
    public boolean isCreateCarAllowed(Principal principal) {
        AccessToken rpt = rptStore.getParsedRpt(AppTokenUtil.getKeycloakSecurityContext(principal));
        // TODO: improve?
        if (rpt == null || !rptStore.hasPermission(rpt, "Car Resource", "car:create")) {
            log.info("Obtainin initial RPT");
            try {
                createCarUnchecked();
            } catch (UMAErrorHandler.HandledException he) {
                // Just ignoring. We have the "RPT" now
            }

            rpt = rptStore.getParsedRpt(AppTokenUtil.getKeycloakSecurityContext(principal));
            // TODO: Probably other exception is here in case that I am not allowed to create a car?
        }

        return rptStore.hasPermission(rpt, "Car Resource", "car:create");
    }


    public Map<String, List<CarRepresentation>> getCars() {
        ResponseEntity<Map> response = template.getForEntity(endpoint, Map.class);
        return response.getBody();
    }


    public CarRepresentation createCar() {
        return Retry.callWithRetry(() -> {

            return createCarUnchecked();

        }, template);
    }


    private CarRepresentation createCarUnchecked() {
        String createEndpoint = endpoint + "/create";
        ResponseEntity<CarRepresentation> newCar = template.postForEntity(createEndpoint, null, CarRepresentation.class);
        return newCar.getBody();
    }


    public CarRepresentation getCarWithDetails(String carId) {
        String detailsEndpoint = endpoint + "/details/" + carId;
        ResponseEntity<CarRepresentation> newCar = template.getForEntity(detailsEndpoint, CarRepresentation.class);
        return newCar.getBody();
    }


    public void deleteCar(String carId) {
        String deleteEndpoint = endpoint + "/delete/" + carId;
        template.delete(deleteEndpoint);
    }


    public static class Retry {

        public static <V> V callWithRetry(Callable<V> callable, KeycloakRestTemplate template) {
            int remaining = 2;
            while (remaining > 0) {
                try {
                    return callable.call();
                } catch (UMAErrorHandler.HandledException ex) {
                    // retry the call if possible
                    if (remaining > 0) {
                        log.infof("Retrying operation. Remaining retries: %d", remaining);
                        remaining--;
                    }
                    if (remaining == 0) {
                        log.error("No more retrying available. Will need to rethrow the error");

                        try {
                            ((UMAErrorHandler) template.getErrorHandler()).superHandleError(ex.getResponse());
                        } catch (IOException ioex) {
                            throw new RuntimeException(ioex);
                        }
                    }
                } catch (RestClientException rce) {
                    throw rce;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            return null;
        }

    }

}
