package org.keycloak.quickstarts.devconf2019.service;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class CarRepresentation {

    public CarRepresentation() {
    }

    public CarRepresentation(String name, String description, String base64Img, OwnerRepresentation owner) {
        this.name = name;
        this.description = description;
        this.base64Img = base64Img;
        this.owner = owner;
    }


    private String name;

    private String description;

    private String base64Img; // May be null when we're sending list of the cars

    private OwnerRepresentation owner;

    // TODO: resourceId?


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBase64Img() {
        return base64Img;
    }

    public void setBase64Img(String base64Img) {
        this.base64Img = base64Img;
    }

    public OwnerRepresentation getOwner() {
        return owner;
    }

    public void setOwner(OwnerRepresentation owner) {
        this.owner = owner;
    }

    @JsonIgnore
    public boolean isImgVisible() {
        return base64Img != null;
    }

}
