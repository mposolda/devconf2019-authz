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

    private Object lock = new Object();

    public InMemoryCarsDB() {
        log.info("Reading cars DB");

        try {
            cars.add(new Car("yellow_van_dhl", "Yellow Van DHL", ImgUtil.readImage("IMG_20190118_082440.jpg")));

            cars.add(new Car("yellow_towing_vehicle", "Yellow Towing Vehicle", ImgUtil.readImage("IMG_20190118_083037.jpg")));
            cars.add(new Car("vw_transporter_green_bus", "VW Transporter - Green Bus", ImgUtil.readImage("IMG_20190118_083128.jpg")));
            cars.add(new Car("white_alpha_romeo", "White Alpha Romeo", ImgUtil.readImage("IMG_20190118_083223.jpg")));
            cars.add(new Car("green_cool_car", "Very Cool Green Car", ImgUtil.readImage("IMG_20190118_083253.jpg")));
            cars.add(new Car("red_mixer", "Red Mixer", ImgUtil.readImage("IMG_20190118_083333.jpg")));

            cars.add(new Car("red_train", "Red Train", ImgUtil.readImage("IMG_20190118_083404.jpg")));
            cars.add(new Car("red_ferrari", "Red Ferrari", ImgUtil.readImage("IMG_20190118_083457.jpg")));
            cars.add(new Car("hippies_vw_transporter", "VW Transporter - Hippies Bus", ImgUtil.readImage("IMG_20190118_083547.jpg")));

            cars.add(new Car("white_cistern", "White Cistern Truck", ImgUtil.readImage("IMG_20190118_083607.jpg")));
            cars.add(new Car("blue_bmw", "Blue BMW", ImgUtil.readImage("IMG_20190118_083638.jpg")));
            cars.add(new Car("snowplow", "Snowplow", ImgUtil.readImage("IMG_20190118_083734.jpg")));
            cars.add(new Car("blue_rescue_jeep", "Blue Rescue Jeep", ImgUtil.readImage("IMG_20190118_083831.jpg")));
            cars.add(new Car("black_carabinieri", "Black Carabinieri", ImgUtil.readImage("IMG_20190118_084023.jpg")));
            cars.add(new Car("yellow_truck_deere", "Yellow Truck - Deere", ImgUtil.readImage("IMG_20190118_084119.jpg")));

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


    // Return only the cars with non-null owner
    public Stream<Car> getCarsWithOwner() {
        return cars.stream()
                .filter((Car car) -> car.getOwner() != null);
    }


    public class Car {

        private final String carName;
        private final String carDescription;
        private final String carBase64Img;

        // Null when car is still "free". It is not very nice to track both ID and username here, but should be fine for the example purpose
        private OwnerRepresentation owner;

        public Car(String carName, String carDescription, String carBase64Img) {
            this.carName = carName;
            this.carDescription = carDescription;
            this.carBase64Img = carBase64Img;
        }


        public String getCarName() {
            return carName;
        }

        public String getCarBase64Img() {
            return carBase64Img;
        }

        public String getCarDescription() {
            return carDescription;
        }

        public OwnerRepresentation getOwner() {
            return owner;
        }

        public void setOwner(OwnerRepresentation owner) {
            this.owner = owner;
        }
    }
}
