package adaptersservices.yummly.rest;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("sdelab/resources")
public class YummlyAdapterServiceApplicationConfig extends ResourceConfig {
	public YummlyAdapterServiceApplicationConfig() {
		packages("adaptersservices.yummly"); // Jersey will load all the resources under this package
	}
}