package org.keycloak.quickstarts.devconf2019.app.config;

import java.io.IOException;
import java.util.List;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class UMAErrorHandler extends DefaultResponseErrorHandler {


    private final AuthzClientRequestFactory authzFactory;
    private final RptStore rptStore;

    public UMAErrorHandler(AuthzClientRequestFactory authzFactory, RptStore rptStore) {
        this.authzFactory = authzFactory;
        this.rptStore = rptStore;
    }


    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        if (response.getRawStatusCode() == 401) {
            List<String> list = response.getHeaders().get("WWW-Authenticate");
            if (!list.isEmpty()) {
                String umaInfo = list.get(0);
                String[] chunks = umaInfo.split(",");
                for (String chunk : chunks) {
                    String[] spl = chunk.split("=");
                    if (spl.length == 2 && spl[0].trim().equals("ticket")) {
                        String ticket = spl[1].trim();
                        ticket = ticket.substring(1);
                        ticket = ticket.substring(0, ticket.length() - 1);

                        // TODO: should be improved to not obtain keycloakSecurityContext from authzFactory...
                        rptStore.sendRptRequest(ticket, authzFactory.getKeycloakSecurityContext());

                        // We successfully obtained UMA ticket. Throw handled exception now, so that the particular call to backend service can
                        // be retried with successfully obtained permissions
                        throw new HandledException(response);
                    }
                }

                return;
            }
        }

        superHandleError(response);
    }


    public void superHandleError(ClientHttpResponse response) throws IOException {
        super.handleError(response);
    }


    public static class HandledException extends RuntimeException {

        private final ClientHttpResponse response;

        public HandledException(ClientHttpResponse response) {
            this.response = response;
        }

        public ClientHttpResponse getResponse() {
            return response;
        }
    }
}
