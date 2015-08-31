package adaptersservices.yummly;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
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
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
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
public class YummlyAdapter {

	private final int pageSize = 30;
	private final String appId = "67bb462e";// name for header: X-Yummly-App-ID
	private final String appIdKeyword = "X-Yummly-App-ID";
	private final String appKey = "4e96ca370249ec08b66bcf4433312361";// name for header: X-Yummly-App-Key
	private final String appKeyKeyword = "X-Yummly-App-Key";
	final String yummlyRecipeSite = "http://www.yummly.com/recipe/";

	final XPath xpath = XPathFactory.newInstance().newXPath();
	final ObjectMapper mapper = new ObjectMapper();
	final public WebTarget yummlyWebTarget;
	private final String yummlyURI = "https://api.yummly.com/v1/api/";

	@Context
	UriInfo uriInfo;
	@Context
	Request request;
	final DocumentBuilder builder;
	Document document;

	public YummlyAdapter() throws ParserConfigurationException {
		builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		yummlyWebTarget = ClientBuilder.newClient(new ClientConfig()).target(UriBuilder.fromUri(yummlyURI).build());

	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/get/{recipeId}")
	public YummlyRecipe getRecipe(@PathParam("recipeId") String id) throws SAXException, IOException {
		Response response = yummlyWebTarget.path("recipe/" + id).request().header(appIdKeyword, appId)
				.header(appKeyKeyword, appKey).accept(MediaType.APPLICATION_JSON).get(Response.class);
		if (response.getStatus() != 200) {
			throw new WebApplicationException(response.getStatus());
		}
		String body = response.readEntity(String.class);
		JsonNode tree = mapper.readTree(body);
		YummlyRecipe recipe = new YummlyRecipe();
		recipe.set_id(tree.path("id").asText());
		recipe.setRecipeTitle(tree.path("name").asText());
		recipe.setRecipeUrl(tree.path("attribution").path("url").asText());
		recipe.setImageUrl(tree.path("images").get(0).path("hostedMediumUrl").asText());

		Iterator<JsonNode> it = tree.path("nutritionEstimates").elements();
		JsonNode node;
		while (it.hasNext()) {
			node = it.next();
			if (node.path("attribute").asText().contentEquals("ENERC_KCAL")) {
				recipe.setCalories(node.path("value").asInt());
				break;
			}
		}

		return recipe;

	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/getRandom")
	public YummlyRecipe getRandomRecipe(@QueryParam("recipeKeyWord") String keyword, @QueryParam("minCal") int minCal,
			@QueryParam("maxCal") int maxCal, @QueryParam("course") String allowedCourse)
					throws SAXException, IOException {

		WebTarget provisoryWebTarget = yummlyWebTarget.path("recipes").queryParam("maxResult", pageSize);

		if (keyword != null) {
			provisoryWebTarget = provisoryWebTarget.queryParam("q", keyword);
		}

		if (minCal != 0) {
			provisoryWebTarget = provisoryWebTarget.queryParam("nutrition.ENERC_KCAL.min", minCal);
		}
		if (maxCal != 0) {
			provisoryWebTarget = provisoryWebTarget.queryParam("nutrition.ENERC_KCAL.max", maxCal);
		}
		if (allowedCourse != null) {
			provisoryWebTarget = provisoryWebTarget.queryParam("allowedCourse[]", allowedCourse);
		}
		Response response = provisoryWebTarget.request().header(appIdKeyword, appId).header(appKeyKeyword, appKey)
				.accept(MediaType.APPLICATION_JSON).get(Response.class);
		
		if (response.getStatus() != 200) {
			throw new WebApplicationException(response.getStatus());
		}
		
		String body = response.readEntity(String.class);
		JsonNode tree = mapper.readTree(body);

		int numberOfResults = tree.path("totalMatchCount").asInt();
		if (numberOfResults == 0) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}

		int randomRecipe = new Random().nextInt(numberOfResults);

		response = provisoryWebTarget.queryParam("start", randomRecipe).request().header(appIdKeyword, appId)
				.header(appKeyKeyword, appKey).accept(MediaType.APPLICATION_JSON).get(Response.class);

		body = response.readEntity(String.class);
		tree = mapper.readTree(body);

		Iterator<JsonNode> matches = tree.path("matches").elements();

		JsonNode r = null;
		if (matches.hasNext()) {
			r = matches.next();
		} else {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}

		YummlyRecipe recipe = new YummlyRecipe();
		recipe.set_id(r.path("id").asText());
		recipe.setImageUrl(r.path("smallImageUrls").get(0).asText());
		recipe.setRecipeTitle(r.path("recipeName").asText());
		recipe.setRecipeUrl(yummlyRecipeSite + recipe.get_id());
		return recipe;

	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/search")
	public List<YummlyRecipe> searchRecipe(@QueryParam("recipeKeyWord") String keyword,
			@QueryParam("minCal") int minCal, @QueryParam("maxCal") int maxCal,
			@QueryParam("course") String allowedCourse) throws SAXException, IOException {
		
		WebTarget provisoryWebTarget = yummlyWebTarget.path("recipes").queryParam("maxResult", pageSize);

		if (keyword != null) {
			provisoryWebTarget = provisoryWebTarget.queryParam("q", keyword);
		}

		if (minCal != 0) {
			provisoryWebTarget = provisoryWebTarget.queryParam("nutrition.ENERC_KCAL.min", minCal);
		}
		if (maxCal != 0) {
			provisoryWebTarget = provisoryWebTarget.queryParam("nutrition.ENERC_KCAL.max", maxCal);
		}
		if (allowedCourse != null) {
			provisoryWebTarget = provisoryWebTarget.queryParam("allowedCourse[]", allowedCourse);
		}
		Response response = provisoryWebTarget.request().header(appIdKeyword, appId).header(appKeyKeyword, appKey)
				.accept(MediaType.APPLICATION_JSON).get(Response.class);
		
		if (response.getStatus() != 200) {
			throw new WebApplicationException(response.getStatus());
		}
		
		String body = response.readEntity(String.class);
		JsonNode tree = mapper.readTree(body);
		
		Iterator<JsonNode> it = tree.path("matches").elements();
		List<YummlyRecipe> listOfFoundRecipes = new ArrayList<YummlyAdapter.YummlyRecipe>();
		JsonNode node;
		while (it.hasNext()) {
			node = it.next();
			YummlyRecipe r = new YummlyRecipe();
			r.setImageUrl(node.path("smallImageUrls").get(0).asText());
			r.setRecipeTitle(node.path("recipeName").asText());
			r.set_id(node.path("id").asText());
			r.setRecipeUrl(yummlyRecipeSite + r.get_id());
			listOfFoundRecipes.add(r);
		}

		return listOfFoundRecipes;

	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement
	@XmlType(propOrder = { "_id", "recipeTitle", "recipeUrl", "minvalue", "imageUrl" })
	private class YummlyRecipe implements Serializable {

		private static final long serialVersionUID = -4832603990807349792L;

		@XmlElement
		private String _id;
		@XmlElement
		private String imageUrl;
		@XmlElement
		private String recipeUrl;
		@XmlElement
		private String recipeTitle;
		@XmlTransient
		private int calories;

		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}

		public void setRecipeUrl(String recipeUrl) {
			this.recipeUrl = recipeUrl;
		}

		public void setRecipeTitle(String recipeTitle) {
			this.recipeTitle = recipeTitle;
		}

		public String get_id() {
			return _id;
		}

		public void set_id(String _id) {
			this._id = _id;
		}

		public void setCalories(int calories) {
			this.calories = calories;
		}
	}
}
