/*
*
* 이 클래스는 https://snh2413.tistory.com/47 에서 가져왔습니다.
*
*/
package org.kish;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class ContainerConfig implements WebMvcConfigurer {
    @Value("${ajp.port}")
    int ajpPort;

    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        tomcat.addAdditionalTomcatConnectors(createAjpConnector());
        return tomcat;
    }

    private Connector createAjpConnector() {
        Connector ajpConnector = new Connector("AJP/1.3");
        ajpConnector.setPort(ajpPort);
        ajpConnector.setSecure(true);
        ajpConnector.setAllowTrace(false);
        ajpConnector.setScheme("https");
        return ajpConnector;
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/resource/**")
                .addResourceLocations("file:" + KishServer.RESOURCE_PATH.getAbsolutePath() + File.separator);
    }
}