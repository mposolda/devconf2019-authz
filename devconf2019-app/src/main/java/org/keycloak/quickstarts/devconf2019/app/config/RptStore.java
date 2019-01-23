package org.keycloak.quickstarts.devconf2019.app.config;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jboss.logging.Logger;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.authorization.client.AuthorizationDeniedException;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;
import org.keycloak.authorization.client.util.HttpResponseException;
import org.keycloak.jose.jws.JWSInput;
import org.keycloak.jose.jws.JWSInputException;
import org.keycloak.quickstarts.devconf2019.app.service.ObjectMapperProvider;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.idm.authorization.AuthorizationRequest;
import org.keycloak.representations.idm.authorization.AuthorizationResponse;
import org.keycloak.representations.idm.authorization.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
@Component
public class RptStore {

    private static final Logger log = Logger.getLogger(RptStore.class);

    // Will be better to have separate factory service...
    private AuthzClient authzClient;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private ObjectMapperProvider mapperProvider;


    // Can return null. Use accessToken in that case
    protected String getRpt(KeycloakSecurityContext context) {
        RptInfo rpt = getCurrentRpt(request, context);
        return rpt==null ? null : rpt.getRpt();
    }


    public AccessToken getParsedRpt(KeycloakSecurityContext context) {
        RptInfo rpt = getCurrentRpt(request, context);
        return rpt==null ? null : rpt.getParsedRpt();
    }


    /**
     * Can throw HandledException in case that request was successfully submitted through the UMA. We don't need to retry re-sending UMA ticket
     * in this case
     *
     * @param umaTicket
     * @param ctx
     */
    public void sendRptRequest(String umaTicket, KeycloakSecurityContext ctx) throws UMAErrorHandler.HandledException {
        RptInfo currentRpt = getCurrentRpt(request, ctx);
        RptInfoResponse rptInfoResponse = sendRptRequest(currentRpt, ctx.getTokenString(), umaTicket);

        // Save as session attribute now
        if (rptInfoResponse.getRptInfo() != null) {
            request.getSession().setAttribute("rpt", rptInfoResponse.getRptInfo());
        } else {
            throw new UMAErrorHandler.HandledException(true);
        }
    }


    private RptInfo getCurrentRpt(HttpServletRequest request, KeycloakSecurityContext context) {
        // Check if we need to reload RPT
        return (RptInfo) request.getSession().getAttribute("rpt");
    }


    private RptInfoResponse sendRptRequest(RptInfo oldRptInfo, String tokenString, String umaTicket) {
        AuthorizationRequest authzReq = new AuthorizationRequest();

        if (oldRptInfo != null) {
            authzReq.setRpt(oldRptInfo.getRpt());
        }
        if (umaTicket != null) {
            authzReq.setTicket(umaTicket);
        }

        log.infof("Sending request to exchange UMA ticket for RPT. Uma ticket is: %s", umaTicket);

        AuthzClient authzClient = getAuthzClient();
        try {
            AuthorizationResponse response = authzClient.authorization(tokenString).authorize(authzReq);
            String rpt = response.getToken();
            log.infof("Obtained RPT successfully: %s", rpt);

            try {
                AccessToken parsedRpt = new JWSInput(rpt).readJsonContent(AccessToken.class);
                RptInfo newRptInfo = new RptInfo(rpt, parsedRpt);
                return new RptInfoResponse(newRptInfo, false);
            } catch (JWSInputException ioe) {
                throw new RuntimeException(ioe);
            }
        } catch (AuthorizationDeniedException ex) {
            if (ex.getMessage().contains("{")) {
                String json = ex.getMessage().substring(ex.getMessage().indexOf("{"));
                try {
                    Map map = mapperProvider.getMapper().readValue(json, Map.class);
                    String errorDesc = (String) map.get("error_description");
                    if (errorDesc != null && errorDesc.equals("request_submitted")) {
                        log.info("UMA request submitted to the resource owner. Need to wait for the approval");
                        return new RptInfoResponse(null, true);
                    }
                } catch (IOException ioe) {
                    log.error("Unexpected ioe", ioe);
                }
            }

            log.errorf(ex, "Failed to obtain permissions for ticket: %s", umaTicket);
            return new RptInfoResponse(null, false);
        }
    }


    private AuthzClient getAuthzClient() {
        if (authzClient == null) {
            synchronized (this) {
                if (authzClient == null) {
                    // TODO: Configuration of the client needs to be better!
                    Map<String, Object> clientSecret = new HashMap<>();
                    clientSecret.put("secret", "password");
                    Configuration cfg = new Configuration("http://localhost:8180/auth", "cars","cars-app", clientSecret, null);
                    authzClient = AuthzClient.create(cfg);
                }
            }
        }

        return authzClient;
    }

    public boolean hasPermission(AccessToken rpt, String resourceName, String scopeName) {
        if (rpt==null || rpt.getAuthorization() == null) {
            return false;
        }

        AccessToken.Authorization authorization = rpt.getAuthorization();

        for (Permission permission : authorization.getPermissions()) {
            if (resourceName.equalsIgnoreCase(permission.getResourceName()) || resourceName.equalsIgnoreCase(permission.getResourceId())) {
                if (scopeName == null) {
                    return true;
                }

                if (permission.getScopes().contains(scopeName)) {
                    return true;
                }
            }
        }

        return false;
    }


    public static class RptInfo implements Serializable {

        private String rpt;
        private AccessToken parsedRpt;

        public RptInfo() {
        }

        public RptInfo(String rpt, AccessToken parsedRpt) {
            this.rpt = rpt;
            this.parsedRpt = parsedRpt;
        }

        public String getRpt() {
            return rpt;
        }

        public void setRpt(String rpt) {
            this.rpt = rpt;
        }

        public AccessToken getParsedRpt() {
            return parsedRpt;
        }

        public void setParsedRpt(AccessToken parsedRpt) {
            this.parsedRpt = parsedRpt;
        }
    }


    static class RptInfoResponse {

        private final RptInfo rptInfo;
        private final boolean requestSubmitted;

        public RptInfoResponse(RptInfo rptInfo, boolean requestSubmitted) {
            this.rptInfo = rptInfo;
            this.requestSubmitted = requestSubmitted;
        }


        public RptInfo getRptInfo() {
            return rptInfo;
        }

        public boolean isRequestSubmitted() {
            return requestSubmitted;
        }
    }

}
