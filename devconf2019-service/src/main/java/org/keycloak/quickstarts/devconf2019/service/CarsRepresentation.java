package org.keycloak.quickstarts.devconf2019.service;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class CarsRepresentation {

    // Key is ownerUsername, Value are cars of that owner
    private Map<String, List<CarRepresentation>> cars;

    public Map<String, List<CarRepresentation>> getCars() {
        return cars;
    }

    public void setCars(Map<String, List<CarRepresentation>> cars) {
        this.cars = cars;
    }


    public static class CarRepresentation {

        private String carName;

        private String carBase64Img;

        private String ownerId;

        private String ownerUsername;

        // TODO: resourceId?


        public String getCarName() {
            return carName;
        }

        public void setCarName(String carName) {
            this.carName = carName;
        }

        public String getCarBase64Img() {
            return carBase64Img;
        }

        public void setCarBase64Img(String carBase64Img) {
            this.carBase64Img = carBase64Img;
        }

        public String getOwnerId() {
            return ownerId;
        }

        public void setOwnerId(String ownerId) {
            this.ownerId = ownerId;
        }

        public String getOwnerUsername() {
            return ownerUsername;
        }

        public void setOwnerUsername(String ownerUsername) {
            this.ownerUsername = ownerUsername;
        }

        @JsonIgnore
        public boolean isImgVisible() {
            return carBase64Img != null;
        }
    }
}
