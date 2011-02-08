package CIAPI.Java;

import java.util.Map;

import CIAPI.Java.urlstuff.UrlHelper;

/**
 * High-level JSON API
 * 
 * @author justin nelson
 * 
 */
public class JsonApi {

	private JsonClient client;
	private String baseUrl;

	/**
	 * Create a new JsonApi given a base URL for the API and the JsonClient to
	 * use.
	 * 
	 * @param baseUrl
	 *            the base url for the API
	 * @param client
	 *            the JsonCLient to pass requests to.
	 */
	public JsonApi(String baseUrl, JsonClient client) {
		if (client == null)
			throw new NullPointerException("JspnClient must not be null");
		if (baseUrl == null || baseUrl.trim().length() == 0)
			throw new IllegalArgumentException("The base url must not be null or empty.");
		this.baseUrl = baseUrl;
		this.client = client;
	}

	/**
	 * Call an API's GET method
	 * 
	 * @param methodName
	 *            The name of the method. Use null or "" for no method name (use
	 *            the base URL)
	 * @param parameters
	 *            the parameters for this method
	 * @param returnType
	 *            the type that this method should return
	 * @return an object of type returnType. Can safely be cast.
	 * @throws ApiException
	 *             If an error occurred during the call.
	 */
	public Object callGetMethod(String methodName, Map<String, String> parameters, Class<?> returnType)
			throws ApiException {
		if (returnType == null)
			throw new NullPointerException("Return type must not be null");
		String url = new UrlHelper(baseUrl, methodName, parameters).toUrl();
		Object result = client.makeGetRequest(url, returnType);
		return result;
	}

	/**
	 * Call an API's GET method
	 * 
	 * @param methodName
	 *            The name of the method. Use null or "" for no method name (use
	 *            the base URL)
	 * @param parameters
	 *            the parameters for this method
	 * @param inputData
	 *            the data to pass into the request
	 * @param returnType
	 *            the type that this method should return
	 * @return an object of type returnType. Can safely be cast.
	 * @throws ApiException
	 *             If an error occurred during the call.
	 */
	public Object callPostMethod(String methodName, Map<String, String> parameters, Object inputData,
			Class<?> returnType) throws ApiException {
		if (returnType == null)
			throw new NullPointerException("Return type must not be null");
		String url = new UrlHelper(baseUrl, methodName, parameters).toUrl();
		return client.makePostRequest(url, inputData, returnType);
	}
}
