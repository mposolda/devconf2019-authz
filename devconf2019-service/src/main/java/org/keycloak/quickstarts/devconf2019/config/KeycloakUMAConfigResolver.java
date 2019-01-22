package org.keycloak.quickstarts.devconf2019.config;

import java.lang.reflect.Field;

import org.keycloak.adapters.springboot.KeycloakBaseSpringBootConfiguration;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.representations.adapters.config.PolicyEnforcerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * It seems it is not possible to set policyEnforcerConfig.userManagedAccess through application.properties...
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
@Component
public class KeycloakUMAConfigResolver extends KeycloakSpringBootConfigResolver {

    @Autowired
    private KeycloakBaseSpringBootConfiguration cfg;

    public KeycloakUMAConfigResolver() throws Exception {
        Field f = KeycloakSpringBootConfigResolver.class.getDeclaredField("adapterConfig");
        f.setAccessible(true);
        KeycloakSpringBootProperties properties = (KeycloakSpringBootProperties) f.get(null);

        properties.getPolicyEnforcerConfig().setUserManagedAccess(new PolicyEnforcerConfig.UserManagedAccessConfig());
    }


}
