package com.eccos.nadzorniservis.utils;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.eccos.nadzorniservis.configuration.AppConfig;

@Configuration
public class HttpsPortTomcat {
    
    @Autowired
    AppConfig appConfig;
    
    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                securityConstraint.addCollection(collection);
                context.addConstraint(securityConstraint);
            }
        };
        tomcat.addAdditionalTomcatConnectors(redirectConnector());
        return tomcat;
    }
    
    private Connector redirectConnector() {

        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL); //"org.apache.coyote.http11.Http11NioProtocol"
        connector.setScheme(appConfig.getServerHttpScheme());
        connector.setPort(appConfig.getServerHttpPort());
        connector.setSecure(false);
        connector.setRedirectPort(appConfig.getServerHttpsPort());
        return connector;
    }

}
