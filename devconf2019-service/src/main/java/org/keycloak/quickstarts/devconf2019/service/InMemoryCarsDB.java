package org.keycloak.quickstarts.devconf2019.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import org.jboss.logging.Logger;
import org.keycloak.quickstarts.devconf2019.util.ImgUtil;
import org.springframework.stereotype.Component;

/**
 * Just in-memory DB. Doesn't do any authz checks
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
@Component
public class InMemoryCarsDB {

    private static final Logger log = Logger.getLogger(InMemoryCarsDB.class);

    private final List<Car> cars = new ArrayList<>();

    private final Object lock = new Object();

    public InMemoryCarsDB() {
        log.info("Reading cars DB");

        try {
            cars.add(new Car("1", "Yellow Van DHL", ImgUtil.readImage("IMG_20190118_082440.jpg")));

            cars.add(new Car("2", "Yellow Towing Vehicle", ImgUtil.readImage("IMG_20190118_083037.jpg")));
            cars.add(new Car("3", "VW Transporter - Green Bus", ImgUtil.readImage("IMG_20190118_083128.jpg")));
            cars.add(new Car("4", "White Alpha Romeo", ImgUtil.readImage("IMG_20190118_083223.jpg")));
            cars.add(new Car("5", "Very Cool Green Car", ImgUtil.readImage("IMG_20190118_083253.jpg")));
            cars.add(new Car("6", "Red Mixer", ImgUtil.readImage("IMG_20190118_083333.jpg")));

            cars.add(new Car("7", "Red Train", ImgUtil.readImage("IMG_20190118_083404.jpg")));
            cars.add(new Car("8", "Red Ferrari", ImgUtil.readImage("IMG_20190118_083457.jpg")));
            cars.add(new Car("9", "VW Transporter - Hippies Bus", ImgUtil.readImage("IMG_20190118_083547.jpg")));

            cars.add(new Car("10", "White Cistern Truck", ImgUtil.readImage("IMG_20190118_083607.jpg")));
            cars.add(new Car("11", "Blue BMW", ImgUtil.readImage("IMG_20190118_083638.jpg")));
            cars.add(new Car("12", "Snowplow", ImgUtil.readImage("IMG_20190118_083734.jpg")));
            cars.add(new Car("13", "Blue Rescue Jeep", ImgUtil.readImage("IMG_20190118_083831.jpg")));
            cars.add(new Car("14", "Black Carabinieri", ImgUtil.readImage("IMG_20190118_084023.jpg")));
            cars.add(new Car("15", "Yellow Truck - Deere", ImgUtil.readImage("IMG_20190118_084119.jpg")));
        } catch (IOException ioe) {
            throw new RuntimeException("Error when initializing cars", ioe);
        }

        log.infof("Successfully read %d cars", cars.size());
    }


    public Car giveRandomCarToUser(String userId, String username) {
        Random r = new Random();

        synchronized(lock) {
            for (int i=0 ; i<1000 ; i++) {
                int carIndex = r.nextInt(cars.size());
                Car car = cars.get(carIndex);

                if (car.getOwner() == null) {
                    car.setOwner(new OwnerRepresentation(userId, username));
                    return car;
                }
            }

            throw new IllegalStateException("Wasn't able to give the car to user " + username + ". Maybe all cars are occupied");
        }

    }


    public Car getCarById(String carId) {
        // TODO: Better handle case when car doesn't exists...
        return cars.stream().filter((Car car) -> car.getId().equals(carId)).findFirst().get();
    }


    public Car deleteCarById(String carId) {
        // TODO: Better handle case when car doesn't exists...
        Car car = getCarById(carId);

        // This means just deleting owner
        synchronized(lock) {
            car.setOwner(null);
            car.setExternalId(null);
        }

        return car;
    }


    // Return only the cars with non-null owner
    public Stream<Car> getCarsWithOwner() {
        return cars.stream()
                .filter((Car car) -> car.getOwner() != null);
    }


    public class Car {

        private final String id;
        private final String name;
        private final String base64Img;

        // Null when car is still "free". It is not very nice to track both ID and username here, but should be fine for the example purpose
        private OwnerRepresentation owner;
        private String externalId;

        public Car(String id, String name, String base64Img) {
            this.id = id;
            this.name = name;
            this.base64Img = base64Img;
        }


        public String getId() {
            return id;
        }

        public String getBase64Img() {
            return base64Img;
        }

        public String getName() {
            return name;
        }

        public OwnerRepresentation getOwner() {
            return owner;
        }

        public void setOwner(OwnerRepresentation owner) {
            this.owner = owner;
        }

        public String getExternalId() {
            return externalId;
        }

        public void setExternalId(String externalId) {
            this.externalId = externalId;
        }
    }
}
