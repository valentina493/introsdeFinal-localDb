package adaptersservices.quotes.rest;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("sdelab/resources")
public class QuotesAdapterServiceApplicationConfig extends ResourceConfig {
	public QuotesAdapterServiceApplicationConfig() {
		packages("adaptersservices.quotes"); // Jersey will load all the resources under this package
	}
}