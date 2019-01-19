package org.keycloak.quickstarts.devconf2019.service;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class CarRepresentation {

    public CarRepresentation() {
    }

    public CarRepresentation(String id, String name, String base64Img, OwnerRepresentation owner) {
        this.id = id;
        this.name = name;
        this.base64Img = base64Img;
        this.owner = owner;
    }


    private String id;

    private String name;

    private String base64Img; // May be null when we're sending list of the cars

    private OwnerRepresentation owner;

    // TODO: resourceId?


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
