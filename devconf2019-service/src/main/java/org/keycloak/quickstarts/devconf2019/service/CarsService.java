package org.keycloak.quickstarts.devconf2019.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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


    // Key is ownerUsername, Values are all cars of this owner. Image is not set
    public Map<String, List<CarRepresentation>> getCars(String userId) {
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
