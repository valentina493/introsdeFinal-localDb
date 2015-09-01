package adaptersservices.quotes.rest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public class QuotesAdapterServiceApp {
	private static final URI BASE_URI = URI.create("http://localhost:8001/quotes/");

	public static void main(String[] args) throws IllegalArgumentException, IOException, URISyntaxException {
		System.out.println("Starting quotes adapter service HTTP server...");
		JdkHttpServerFactory.createHttpServer(BASE_URI, createApp());
		System.out.println("Server started on " + BASE_URI + "\n[kill the process to exit]");
	}

	public static ResourceConfig createApp() {
		System.out.println("Starting REST quotes adapter services...");
		return new QuotesAdapterServiceApplicationConfig();
	}
}
