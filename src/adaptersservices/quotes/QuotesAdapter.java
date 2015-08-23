package adaptersservices.quotes;

import java.io.IOException;
import java.io.Serializable;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.glassfish.jersey.client.ClientConfig;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Stateless
@LocalBean
@Path("/adapter/")
public class QuotesAdapter {

	final XPath xpath = XPathFactory.newInstance().newXPath();
	final ObjectMapper mapper = new ObjectMapper();
	final public WebTarget quotesWebTarget;
	private final String quotesURI = "http://localhost:9090/motivationalquotes/sport/";

	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	final DocumentBuilder builder;
	Document document;

	public QuotesAdapter() throws ParserConfigurationException {
		builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		quotesWebTarget = ClientBuilder.newClient(new ClientConfig()).target(UriBuilder.fromUri(quotesURI).build());

	}
	
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/getRandom")
	public Quote getRandomQuote () throws SAXException, IOException {
		Response response = quotesWebTarget.path("quote").request().accept(MediaType.APPLICATION_JSON).get(Response.class);
		String body = response.readEntity(String.class);
		JsonNode tree = mapper.readTree(body);
		
		Quote q = new Quote();
		q._id = tree.path("id").asText();
		q.author = tree.path("author").asText();
		q.text = tree.path("sentence").asText();
		
		return q;
		
	}
	
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement
	private class Quote implements Serializable {

		private static final long serialVersionUID = -4832603990807349792L;

		@XmlElement
		private String _id;
		@XmlElement
		private String author;
		@XmlElement
		private String text;

	}
}
