package org.keycloak.quickstarts.devconf2019.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.idm.authorization.PermissionTicketRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.keycloak.quickstarts.devconf2019.service.CarsAuthzService.SCOPE_DELETE;
import static org.keycloak.quickstarts.devconf2019.service.CarsAuthzService.SCOPE_VIEW;
import static org.keycloak.quickstarts.devconf2019.service.CarsAuthzService.SCOPE_VIEW_DETAIL;

/**
 * Service, which among other things, also do authz integration (CRUD resources and permissions etc)
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
@Component
public class CarsService {

    @Autowired
    private InMemoryCarsDB db;

    @Autowired
    private CarsAuthzService carsAuthzService;

    public CarRepresentation generateCarForUser(String userId, String username) {
        // Create in DB
        InMemoryCarsDB.Car car = db.giveRandomCarToUser(userId, username);

        // Create authz resource
        carsAuthzService.createProtectedResource(car);

        return new CarRepresentation(car.getId(), car.getName(), null, car.getExternalId(), car.getOwner());
    }


    /**
     * Return all the cars, which current user is able to see. If "username" is null, we aim to return all the cars of all the users
     *
     * @param username
     * @return Map with all the cars user is able to see. Key is ownerUsername, Values are all cars of this owner. Image is not set in the CarRepresentation instances.
     */
    public Map<String, List<CarRepresentation>> getCars(String username) {
        Map<String, List<CarRepresentation>> carsRep = new HashMap<>();

        db.getCarsWithOwner().forEach((InMemoryCarsDB.Car car) -> {
            String ownerUsername = car.getOwner().getUsername();

            List<CarRepresentation> ownerCars;
            if (!carsRep.containsKey(ownerUsername)) {
                carsRep.put(ownerUsername, new ArrayList<>());
            }

            carsRep.get(ownerUsername).add(new CarRepresentation(car.getId(), car.getName(), null,
                    car.getExternalId(), car.getOwner()));
        });

        List<PermissionTicketRepresentation> sharedPermissions = carsAuthzService.getCarsPermissions();
        for (PermissionTicketRepresentation ticket : sharedPermissions) {
            String ownerUsername = ticket.getOwnerName();
            String resourceId = ticket.getResource();

            for (CarRepresentation carRep : carsRep.get(ownerUsername)) {
                if (carRep.getExternalId().equals(resourceId)) {
                    carRep.addScope(ticket.getScopeName());
                }
            }
        }

        for (Map.Entry<String, List<CarRepresentation>> userCars : carsRep.entrySet()) {
            if (username == null || username.equals(userCars.getKey())) {
                for (CarRepresentation car : userCars.getValue()) {
                    car.addScope(SCOPE_VIEW);
                    car.addScope(SCOPE_VIEW_DETAIL);
                    car.addScope(SCOPE_DELETE);
                }
            }

            Set<CarRepresentation> toRemove = new HashSet<>();
            for (CarRepresentation car : userCars.getValue()) {
                if (car.getScopes() == null || car.getScopes().isEmpty()) {
                    toRemove.add(car);
                }
            }

            for (CarRepresentation toRemove2 : toRemove) {
                userCars.getValue().remove(toRemove2);
            }
        }

        Set<String> usernamesToRemove = new HashSet<>();
        for (Map.Entry<String, List<CarRepresentation>> userCars : carsRep.entrySet()) {
            if (userCars.getValue().isEmpty()) {
                usernamesToRemove.add(userCars.getKey());
            }
        }
        for (String toRem : usernamesToRemove) {
            carsRep.remove(toRem);
        }

        return carsRep;
    }


    public CarRepresentation getCarById(String carId) {
        InMemoryCarsDB.Car car = db.getCarById(carId);
        return new CarRepresentation(car.getId(), car.getName(), car.getBase64Img(), car.getExternalId(), car.getOwner());
    }


    public void deleteCarById(String carId) {
        InMemoryCarsDB.Car car = db.deleteCarById(carId);

        // Delete authz resource
        carsAuthzService.deleteProtectedResource(car);
    }

}
