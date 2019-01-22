package org.keycloak.quickstarts.devconf2019.service;

import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.keycloak.KeycloakSecurityContext;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.ClientAuthorizationContext;
import org.keycloak.authorization.client.resource.ProtectionResource;
import org.keycloak.representations.idm.authorization.ResourceOwnerRepresentation;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
@Component
public class CarsAuthzService {

    static final String SCOPE_CREATE = "car:create";
    static final String SCOPE_VIEW = "car:view";
    static final String SCOPE_VIEW_DETAIL = "car:view-detail";
    static final String SCOPE_DELETE = "car:delete";

    private @Autowired
    HttpServletRequest request;

    public void createProtectedResource(InMemoryCarsDB.Car car) {
        try {
            HashSet<ScopeRepresentation> scopes = new HashSet<ScopeRepresentation>();

            scopes.add(new ScopeRepresentation(SCOPE_VIEW));
            scopes.add(new ScopeRepresentation(SCOPE_VIEW_DETAIL));
            scopes.add(new ScopeRepresentation(SCOPE_DELETE));

            ResourceRepresentation carResource = new ResourceRepresentation(car.getName(), scopes, "/cars/" + car.getId(), "http://cars.com/car");

            ResourceOwnerRepresentation resourceOwner = new ResourceOwnerRepresentation();
            resourceOwner.setId(car.getOwner().getId());
            resourceOwner.setName(car.getOwner().getUsername());
            carResource.setOwner(resourceOwner);
            carResource.setOwnerManagedAccess(true);

            ResourceRepresentation response = getAuthzClient().protection().resource().create(carResource);

            car.setExternalId(response.getId());
        } catch (Exception e) {
            throw new RuntimeException("Could not register protected resource.", e);
        }
    }


    public void deleteProtectedResource(InMemoryCarsDB.Car car) {
        String uri = "/cars/" + car.getId();

        try {
            ProtectionResource protection = getAuthzClient().protection();
            List<ResourceRepresentation> search = protection.resource().findByUri(uri);

            if (search.isEmpty()) {
                throw new RuntimeException("Could not find protected resource with URI [" + uri + "]");
            }

            protection.resource().delete(search.get(0).getId());
        } catch (Exception e) {
            throw new RuntimeException("Could not search protected resource.", e);
        }
    }


    private AuthzClient getAuthzClient() {
        return getAuthorizationContext().getClient();
    }

    private ClientAuthorizationContext getAuthorizationContext() {
        return ClientAuthorizationContext.class.cast(getKeycloakSecurityContext().getAuthorizationContext());
    }

    private KeycloakSecurityContext getKeycloakSecurityContext() {
        return KeycloakSecurityContext.class.cast(request.getAttribute(KeycloakSecurityContext.class.getName()));
    }
}
