package org.keycloak.quickstarts.devconf2019.util;

import java.security.Principal;


import org.keycloak.KeycloakPrincipal;
import org.keycloak.representations.AccessToken;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class ServiceTokenUtil {

    public static AccessToken getAccessToken(Principal principal) {
        KeycloakPrincipal kcPrincipal = (KeycloakPrincipal) principal;
        return kcPrincipal.getKeycloakSecurityContext().getToken();
    }
}
