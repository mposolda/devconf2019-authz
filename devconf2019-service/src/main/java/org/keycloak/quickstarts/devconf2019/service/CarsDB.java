package org.keycloak.quickstarts.devconf2019.service;

import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.Logger;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
@Component
public class CarsDB {

    private static final Logger log = Logger.getLogger(CarsDB.class);

    private final List<Car> cars = new ArrayList<>();

    public CarsDB() {
        log.info("Reading cars DB");

        // TODO: read cars from files and assign them some static names
        
        log.infof("Successfully read %d cars", cars.size());
    }

    public class Car {

        private final String carName;
        private final String carBase64Img;
        private boolean free; // is car free, or is it already owned by someone?


        public Car(String carName, String carBase64Img) {
            this.carName = carName;
            this.carBase64Img = carBase64Img;
            this.free = true;
        }


        public String getCarName() {
            return carName;
        }

        public String getCarBase64Img() {
            return carBase64Img;
        }

        public boolean isFree() {
            return free;
        }

        public void setFree(boolean free) {
            this.free = free;
        }
    }
}
