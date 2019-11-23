package Utils;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class microsoftSentiments {

	public static String GetSentimentAnalytics(String comment) {
		HttpClient httpclient = HttpClients.createDefault();

		try {
			URIBuilder builder = new URIBuilder(
					"https://westcentralus.api.cognitive.microsoft.com/text/analytics/v2.0/sentiment");

			// builder.setParameter("numberOfLanguagesToDetect", "1");

			URI uri = builder.build();
			HttpPost request = new HttpPost(uri);
			request.setHeader("Content-Type", "application/json");
			request.setHeader("Ocp-Apim-Subscription-Key", "2ddbe8d6fca5490f8164b3574526f6a6");

			// Request body
			StringEntity reqEntity = new StringEntity(
					"{'documents':[{'language':'en','id':'1','text':'" + comment + "'}]}");
			request.setEntity(reqEntity);

			HttpResponse response = httpclient.execute(request);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				// System.out.println(EntityUtils.toString(entity));
				return EntityUtils.toString(entity);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage() + "hereeeeeee ");
			return null;

		}
		return null;
	}

	public static double getSentiment(final String json) throws JsonParseException, JsonMappingException, IOException {
		double madValue = 0;
		if (json != null) {

			String p = microsoftSentiments.prettify(json);
			JSONObject tomJsonObject = new JSONObject(p);
			JSONArray documents = tomJsonObject.getJSONArray("documents");

			for (int i = 0; i < documents.length(); i++) {
				JSONObject o = documents.getJSONObject(i);

				Double cont = documents.getJSONObject(i).optDouble("score");

				System.out.println("scoreeeeeeeeeeee" + cont);
				madValue = cont;
			}

		

		}
		return madValue;

	}

	public static String prettify(String json_text) {
		JsonParser parser = new JsonParser();
		JsonObject json = parser.parse(json_text).getAsJsonObject();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.toJson(json);
	}
}
