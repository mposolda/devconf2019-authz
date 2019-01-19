package org.keycloak.quickstarts.devconf2019.app.service;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.keycloak.quickstarts.devconf2019.service.CarRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
@Component
public class CarsClientService {

    @Autowired
    private KeycloakRestTemplate template;

    @NotNull
    @Value("${cars.service.url}")
    private String endpoint;

    public Map<String, List<CarRepresentation>> getCars() {
        ResponseEntity<Map> response = template.getForEntity(endpoint, Map.class);
        return response.getBody();
    }


    public CarRepresentation createCar() {
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

}
