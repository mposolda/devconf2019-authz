package org.keycloak.quickstarts.devconf2019;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
@SpringBootApplication
public class CarsServiceApp {

    private static Log logger = LogFactory.getLog(CarsServiceApp.class);

    @Bean
    protected ServletContextListener listener() {
        return new ServletContextListener() {

            @Override
            public void contextInitialized(ServletContextEvent sce) {
                logger.info("ServletContext initialized");
            }

            @Override
            public void contextDestroyed(ServletContextEvent sce) {
                logger.info("ServletContext destroyed");
            }

        };
    }


    public static void main(String[] args) throws Exception {
        SpringApplication.run(CarsServiceApp.class, args);
    }
}
