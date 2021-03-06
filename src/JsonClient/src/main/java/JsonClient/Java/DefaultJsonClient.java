package JsonClient.Java;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.apache.http.client.ClientProtocolException;

import JsonClient.Java.httpstuff.DefaultSimpleHttpClient;
import JsonClient.Java.httpstuff.SimpleHttpClient;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * A default implementation of JsonClient. <br />
 * Basically takes the result of a call to an Api and constructs objects to
 * return using Gson.
 * 
 * @author Justin Nelson
 * 
 */
public class DefaultJsonClient implements JsonClient {

	private SimpleHttpClient client;

	/**
	 * Constructs a DefaultJsonClient. Uses a DefaultSimpleHttpClient for making
	 * requests.
	 */
	public DefaultJsonClient() {
		this(new DefaultSimpleHttpClient());
	}

	/**
	 * Creates a DefaultJsonClient with the given SimpleHttpClient for making
	 * requests.
	 * 
	 * @param httpClient
	 *            the client to pass requests to.
	 */
	public DefaultJsonClient(SimpleHttpClient httpClient) {
		// Null check to avoid passing the error further down the chain.
		if (httpClient == null)
			throw new NullPointerException(
					"The simple http client must not be null");
		client = httpClient;
	}

	@Override
	public Object makeGetRequest(String url, Class<?> clazz)
			throws ApiException {
		if (url == null || url.trim().length() == 0)
			throw new IllegalArgumentException(
					"The url must not be null or empty");
		if (clazz == null)
			throw new NullPointerException("The clazz must not be null");
		try {
			return finishMakeRequest(client.makeGetRequest(url), clazz);
		} catch (ClientProtocolException e) {
			throw new ApiException(e);
		} catch (IOException e) {
			throw new ApiException(e);
		}
	}

	@Override
	public Object makePostRequest(String url, Object content, Class<?> clazz)
			throws ApiException {
		if (url == null || url.trim().length() == 0)
			throw new IllegalArgumentException(
					"The url must not be null or empty");
		if (clazz == null)
			throw new NullPointerException("The clazz must not be null");
		Gson g = new Gson();
		String strContent = (content instanceof String) ? (String) content : g
				.toJson(content);
		try {
			return finishMakeRequest(client.makePostRequest(url, strContent),
					clazz);
		} catch (ClientProtocolException e) {
			throw new ApiException(e);
		} catch (IOException e) {
			throw new ApiException(e);
		}
	}

	/**
	 * Helper method for finishing a get or post request
	 */
	private Object finishMakeRequest(InputStream data, Class<?> clazz)
			throws ApiException {
		Gson g = new Gson();
		Scanner responseEntityData = new Scanner(data);
		StringBuilder strBldr = new StringBuilder();
		while (responseEntityData.hasNextLine()) {
			strBldr.append(responseEntityData.nextLine());
		}
		String fullData = strBldr.toString();
		checkException(fullData);
		try {
			Object result = g.fromJson(fullData, clazz);
			return result;
		} catch (JsonSyntaxException e) {
			throw new GsonParseException(e, fullData);
		}
	}

	/**
	 * Checks to see if the data begins with an error message. If it does, it
	 * simply throws an exception.
	 * 
	 * @param fullData
	 *            the data returned from an API call.
	 * @throws ApiException
	 */
	private void checkException(String fullData) throws ApiException {
		if (fullData.startsWith("{\"ErrorCode\"")) {
			throw new ApiException("API Request caused error: " + fullData);
		}
	}
}
