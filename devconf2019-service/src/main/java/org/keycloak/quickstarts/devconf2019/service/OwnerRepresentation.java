package org.keycloak.quickstarts.devconf2019.service;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class OwnerRepresentation {

    private String id;
    private String username;

    public OwnerRepresentation() {
    }


    public OwnerRepresentation(String id, String username) {
        this.id = id;
        this.username = username;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public int hashCode() {
        return id.hashCode() * 13 + username.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;

        if (!(obj instanceof OwnerRepresentation)) {
            return false;
        }

        OwnerRepresentation that = (OwnerRepresentation) obj;
        return that.id.equals(id) && that.username.equals(username);
    }

    @Override
    public String toString() {
        return "OwnerRepresentation{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
