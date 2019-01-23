package org.keycloak.quickstarts.devconf2019.app.config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.methods.HttpUriRequest;
import org.jboss.logging.Logger;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.client.KeycloakClientRequestFactory;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;
import org.keycloak.jose.jws.JWSInput;
import org.keycloak.jose.jws.JWSInputException;
import org.keycloak.quickstarts.devconf2019.service.InMemoryCarsDB;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.idm.authorization.AuthorizationRequest;
import org.keycloak.representations.idm.authorization.AuthorizationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AuthzClientRequestFactory extends KeycloakClientRequestFactory {

    private static final Logger log = Logger.getLogger(AuthzClientRequestFactory.class);

    @Autowired
    public RptStore rptStore;

    @Autowired
    public HttpServletRequest servletRequest;

    @Override
    protected void postProcessHttpRequest(HttpUriRequest request) {
        KeycloakSecurityContext context = this.getKeycloakSecurityContext();

        // TODO: Ideally should do it all automatically by some provided adapter/utility
        String currentRpt = rptStore.getRpt(context);
        if (currentRpt == null) {
            // Fallback to access token
            currentRpt = context.getTokenString();
        } else {
            AccessToken parsedRpt = rptStore.getParsedRpt(context);
            if (!parsedRpt.isActive(10)) {
                // Just delete RPT and use accessToken instead. TODO: Will be good to have some "built-in" way to refresh RPT for clients
                log.info("Deleting expired RPT. Will need to obtain new when needed");
                rptStore.deleteCurrentRpt(servletRequest);
                currentRpt = context.getTokenString();
            }
        }

        request.setHeader(AUTHORIZATION_HEADER, "Bearer " + currentRpt);
    }


    @Override
    protected KeycloakSecurityContext getKeycloakSecurityContext() {
        return super.getKeycloakSecurityContext();
    }
}
