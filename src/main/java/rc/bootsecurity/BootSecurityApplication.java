package rc.bootsecurity;

import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BootSecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootSecurityApplication.class, args);
    }
    
    @Bean
    public ServletWebServerFactory servletContainer() {
    	//enable ssl
    	TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
    		protected void postProcessContext(org.apache.catalina.Context context) {
    			SecurityConstraint securityConstraint = new SecurityConstraint();
    			securityConstraint.setUserConstraint("CONFIDENTIAL");
    			SecurityCollection collection = new SecurityCollection();
    			collection.addPattern("/");
    			securityConstraint.addCollection(collection);
    			context.addConstraint(securityConstraint);
    		}
    	};
    	
    	//add http to https redirect 
    	tomcat.addAdditionalTomcatConnectors(httpToHttpsRedirectConnector());
    	return tomcat;
    }

	private Connector httpToHttpsRedirectConnector() {

		Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
		connector.setScheme("http");
		connector.setPort(8082);
		connector.setSecure(false);
		connector.setRedirectPort(8443);
		return connector;
	}
    
    
    
}
