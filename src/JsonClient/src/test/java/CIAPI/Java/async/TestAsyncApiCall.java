package CIAPI.Java.async;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import CIAPI.Java.httpstuff.FakeSimpleHttpClient;
import JsonClient.Java.DefaultJsonClient;
import JsonClient.Java.JsonClient;
import JsonClient.Java.async.AsyncApiCall;
import JsonClient.Java.async.AsyncJsonApi;
import JsonClient.Java.async.CallBack;
import examples.stackexchange.SitesWrapper;

public class TestAsyncApiCall {
	private JsonClient client;

	@Before
	public void setUp() throws Exception {
		client = new DefaultJsonClient(new FakeSimpleHttpClient());
	}

	@After
	public void tearDown() throws Exception {
		client = null;
	}

	@Test
	public void testSimpleGetRequest() {
		AsyncJsonApi api = new AsyncJsonApi("files/test/testStatsResponse.json", client);
		api.addUniversalCallBack(new CallBack() {
			@Override
			public void handleException(Exception e) {
			}

			@Override
			public void doCallBack(Object result, String baseUrl, String methodName) {
			}
		});
		AsyncApiCall call = api.createNewCall();
		call.addCallCompleteListener(new CallBack() {
			@Override
			public void handleException(Exception e) {
				fail("No exception should have been thrown.");
			}

			@Override
			public void doCallBack(Object result, String baseUrl, String methodName) {
				assertTrue("Callback was called", true);
			}
		});
		try {
			call.callGetMethod("", null, SitesWrapper.class).get();
		} catch (InterruptedException e1) {
			fail("InterruptedException was thrown during the get method.");
		} catch (ExecutionException e1) {
			fail("ExecutionException was thrown during the get method: " + e1.getMessage());
		}
	}

	@Test
	public void testSimplePostRequest() {
		AsyncJsonApi api = new AsyncJsonApi("files/test/testStatsResponse.json", client);
		AsyncApiCall call = api.createNewCall();
		call.addCallCompleteListener(new CallBack() {
			@Override
			public void handleException(Exception e) {
				fail("No exception should have been thrown.");
			}

			@Override
			public void doCallBack(Object result, String baseUrl, String methodName) {
				assertTrue("Callback was called", true);
			}
		});
		try {
			call.callPostMethod("",null, null, SitesWrapper.class).get();
		} catch (InterruptedException e1) {
			fail("InterruptedException was thrown during the get method.");
		} catch (ExecutionException e1) {
			fail("ExecutionException was thrown during the get method: " + e1.getMessage());
		}
	}

	@Test
	public void testFailRequest() {
		AsyncJsonApi api = new AsyncJsonApi("files/testStatsResponse.json", client);
		AsyncApiCall call = api.createNewCall();
		call.addCallCompleteListener(new CallBack() {
			@Override
			public void handleException(Exception e) {
				// This shoud lhappen
			}

			@Override
			public void doCallBack(Object result, String baseUrl, String methodName) {
				fail("Exception expected");
			}
		});
		try {
			Future<Object> result = call.callPostMethod("", null, null, SitesWrapper.class);
			while (!result.isDone()) {
				Thread.sleep(10);
			}
		} catch (InterruptedException e1) {
			fail("InterruptedException was thrown during the get method.");
		}
	}
}
