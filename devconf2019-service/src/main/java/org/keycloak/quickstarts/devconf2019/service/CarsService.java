package org.keycloak.quickstarts.devconf2019.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
@Component
public class CarsService {

    @Autowired
    private InMemoryCarsDB db;

    public void generateCarForUser(String userId, String username) {
        InMemoryCarsDB.Car car = db.giveRandomCarToUser(userId, username);

        // TODO: check what needs to be done here...
    }



    public CarsRepresentation getCars(String userId) {
        Map<String, List<CarsRepresentation.CarRepresentation>> carsRep = new HashMap<>();

        db.getCarsWithOwner().forEach((InMemoryCarsDB.Car car) -> {
            String ownerUsername = car.getOwner().getOwnerUsername();

            List<CarsRepresentation.CarRepresentation> ownerCars;
            if (!carsRep.containsKey(ownerUsername)) {
                carsRep.put(ownerUsername, new ArrayList<>());
            }

            carsRep.get(ownerUsername).add(new CarsRepresentation.CarRepresentation(car.getCarName(), car.getCarDescription(), car.getCarBase64Img(),
                    car.getOwner()));
        });

        CarsRepresentation result = new CarsRepresentation();
        result.setCars(carsRep);
        return result;
    }



}
