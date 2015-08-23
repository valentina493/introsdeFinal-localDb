package localdbservices.soap.endpoint;

import javax.xml.ws.Endpoint;

import localdbservices.soap.EntitiesImpl;

public class EntitiesPublisher {

	public static String SERVER_URL = "http://localhost";
	public static String PORT = "9004";
	public static String BASE_URL = "/ws/virtuallifecoach/entities";

	public static String getEndpointURL() {
		return SERVER_URL + ":" + PORT + BASE_URL;
	}

	public static void main(String[] args) {
		String endpointUrl = getEndpointURL();
		System.out.println("Starting VirtualLifeCoach entities Service...");
		Endpoint.publish(endpointUrl, new EntitiesImpl());
		System.out.println("--> Published at = " + endpointUrl);
	}

}
