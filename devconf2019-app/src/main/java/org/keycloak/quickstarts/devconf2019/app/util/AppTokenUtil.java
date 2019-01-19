package org.keycloak.quickstarts.devconf2019.app.util;

import java.security.Principal;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.representations.AccessToken;
import org.springframework.security.core.Authentication;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class AppTokenUtil {

    public static AccessToken getAccessToken(Principal principal) {
        Authentication auth = (Authentication) principal;
        KeycloakPrincipal kcPrincipal = (KeycloakPrincipal) auth.getPrincipal();;
        return kcPrincipal.getKeycloakSecurityContext().getToken();
    }

}
