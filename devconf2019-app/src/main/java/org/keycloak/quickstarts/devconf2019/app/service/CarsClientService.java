package org.keycloak.quickstarts.devconf2019.app.service;

import java.util.Arrays;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.keycloak.quickstarts.devconf2019.service.CarsRepresentation;
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

    public CarsRepresentation getCars() {
        ResponseEntity<CarsRepresentation> response = template.getForEntity(endpoint, CarsRepresentation.class);
        return response.getBody();
    }
}
