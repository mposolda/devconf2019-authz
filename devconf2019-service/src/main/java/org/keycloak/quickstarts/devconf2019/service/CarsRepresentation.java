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

        public CarRepresentation() {
        }

        public CarRepresentation(String carName, String carDescription, String carBase64Img, OwnerRepresentation owner) {
            this.carName = carName;
            this.carDescription = carDescription;
            this.carBase64Img = carBase64Img;
            this.owner = owner;
        }


        private String carName;

        private String carDescription;

        private String carBase64Img;

        private OwnerRepresentation owner;

        // TODO: resourceId?


        public String getCarName() {
            return carName;
        }

        public void setCarName(String carName) {
            this.carName = carName;
        }

        public String getCarDescription() {
            return carDescription;
        }

        public void setCarDescription(String carDescription) {
            this.carDescription = carDescription;
        }

        public String getCarBase64Img() {
            return carBase64Img;
        }

        public void setCarBase64Img(String carBase64Img) {
            this.carBase64Img = carBase64Img;
        }

        public OwnerRepresentation getOwner() {
            return owner;
        }

        public void setOwner(OwnerRepresentation owner) {
            this.owner = owner;
        }

        @JsonIgnore
        public boolean isImgVisible() {
            return carBase64Img != null;
        }
    }
}
