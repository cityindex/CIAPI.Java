package CIAPI.Java.examples.ciapi;

import CIAPI.Java.ApiException;
import CIAPI.Java.JsonApi;
import CIAPI.Java.cachestuff.Cache;
import CIAPI.Java.cachestuff.CachedJsonClient;
import CIAPI.Java.examples.ciapi.dto.ApiActiveStopLimitOrderDTO;
import CIAPI.Java.examples.ciapi.dto.ApiOpenPositionDTO;
import CIAPI.Java.examples.ciapi.dto.ApiStopLimitOrderHistoryDTO;
import CIAPI.Java.examples.ciapi.dto.ApiTradeHistoryDTO;
import CIAPI.Java.examples.ciapi.dto.ApiTradeOrderResponseDTO;
import CIAPI.Java.examples.ciapi.dto.CancelOrderRequestDTO;
import CIAPI.Java.examples.ciapi.dto.CreateSessionResponseDTO;
import CIAPI.Java.examples.ciapi.dto.GetMarketInformationResponseDTO;
import CIAPI.Java.examples.ciapi.dto.GetNewsDetailResponseDTO;
import CIAPI.Java.examples.ciapi.dto.GetPriceBarResponseDTO;
import CIAPI.Java.examples.ciapi.dto.GetPriceTickResponseDTO;
import CIAPI.Java.examples.ciapi.dto.ListActiveStopLimitOrderResponseDTO;
import CIAPI.Java.examples.ciapi.dto.ListCfdMarketsResponseDTO;
import CIAPI.Java.examples.ciapi.dto.ListNewsHeadlinesResponseDTO;
import CIAPI.Java.examples.ciapi.dto.ListOpenPositionsResponseDTO;
import CIAPI.Java.examples.ciapi.dto.ListOrdersResponseDTO;
import CIAPI.Java.examples.ciapi.dto.ListSpreadMarketsResponseDTO;
import CIAPI.Java.examples.ciapi.dto.ListStopLimitOrderHistoryResponseDTO;
import CIAPI.Java.examples.ciapi.dto.ListTradeHistoryResponseDTO;
import CIAPI.Java.examples.ciapi.dto.LogOnRequestDTO;
import CIAPI.Java.examples.ciapi.dto.MarketDTO;
import CIAPI.Java.examples.ciapi.dto.NewStopLimitOrderRequestDTO;
import CIAPI.Java.examples.ciapi.dto.NewTradeOrderRequestDTO;
import CIAPI.Java.examples.ciapi.dto.NewsDTO;
import CIAPI.Java.examples.ciapi.dto.NewsDetailDTO;
import CIAPI.Java.examples.ciapi.dto.PriceBarDTO;
import CIAPI.Java.examples.ciapi.dto.PriceTickDTO;
import CIAPI.Java.examples.ciapi.dto.SessionDeletionResponseDTO;
import CIAPI.Java.examples.ciapi.impl.ServiceMethodsImpl;
import CIAPI.Java.httpstuff.DefaultHttpRequestItemFactory;
import CIAPI.Java.throttle.RequestQueue;
import CIAPI.Java.throttle.ThrottledHttpClient;

/**
 * API for connecting to the City Index Trading RESTful API. All requests are
 * made synchronously.
 * 
 * @author Justin Nelson
 */
public class SyncApi {
	private JsonApi api;

	private String username;
	private String password;
	private String sessionId;

	/**
	 * The session ID for the currently open session
	 * 
	 * @return a String representing the current Session Id. If the Id is null
	 *         or empty, then we are currently logged off
	 */
	public String getSessionId() {
		return sessionId;
	}

	private boolean keepAlive;
	private ServiceMethods methods;

	protected SyncApi(String baseUrl, Cache<CachedJsonClient.Pair<String, Class<?>>, Object> cache, RequestQueue queue) {
		methods = new ServiceMethodsImpl();
		api = new JsonApi(baseUrl, new CachedJsonClient(cache, new ThrottledHttpClient(
				new DefaultHttpRequestItemFactory(), queue)));
	}

	/**
	 * Logs this instance on to the CIAPI. Multiple calls of logOn will possibly
	 * change the Session Id, but the class should function as normal.
	 * 
	 * @param username
	 *            The name of the user to authenticate as
	 * @param password
	 *            The password for the given user Id
	 * @param keepAlive
	 *            if true, the client will auto-renew the session token if it
	 *            expires.
	 * @throws ApiException
	 */
	public void logIn(String username, String password, boolean keepAlive) throws ApiException {
		LogOnRequestDTO logOn = new LogOnRequestDTO();
		logOn.setPassword(password);
		logOn.setUserName(username);
		CreateSessionResponseDTO response = methods.CreateSession(logOn, api);
		sessionId = response.getSession();
		this.keepAlive = keepAlive;
		this.username = username;
		if (keepAlive) {
			// need to save the password if we are going to re-use it to
			// authenticate with
			this.password = password;
		}
		api.addConstantParameter("UserName", username);
		api.addConstantParameter("Session", sessionId);
	}

	/**
	 * Removes the user's session token from the server. Also, sets the client
	 * to not keep-alive. If the log out fails, the token will naturally expire.
	 * 
	 * @return whether or not the log out was successful
	 * @throws ApiException
	 */
	public boolean logOff() throws ApiException {
		keepAlive = false;
		SessionDeletionResponseDTO response = methods.DeleteSession(username, sessionId, api);
		if (response.getLoggedOut()) {
			api.clearConstantParams();
			sessionId = null;
			return true;
		} else {
			return false;
		}
	}

	public PriceBarDTO[] getPriceBars(String marketId, String interval, int span, String priceBars) throws ApiException {
		GetPriceBarResponseDTO response = methods.GetPriceBars(marketId, interval, span, priceBars, api);
		return response.getPriceBars();
	}

	public PriceTickDTO[] getPriceTicks(String marketId, String priceTicks) throws ApiException {
		GetPriceTickResponseDTO response = methods.GetPriceTicks(marketId, priceTicks, api);
		return response.getPriceTicks();
	}

	public GetMarketInformationResponseDTO getMarketInformation(String marketId) throws ApiException {
		GetMarketInformationResponseDTO response = methods.GetMarketInformation(marketId, api);
		return response;
	}

	public NewsDTO[] listNewsHeadlines(String category, int maxResults) throws ApiException {
		ListNewsHeadlinesResponseDTO response = methods.ListNewsHeadlines(category, maxResults, api);
		return response.getHeadlines();
	}

	public NewsDetailDTO getNewsDetail(String storyId) throws ApiException {
		GetNewsDetailResponseDTO response = methods.GetNewsDetail(storyId, api);
		return response.getNewsDetail();
	}

	public MarketDTO[] listCfdMarkets(String searchByMarketName, String searchByMarketCode, int clientAccountId,
			int maxResults) throws ApiException {
		ListCfdMarketsResponseDTO response = methods.ListCfdMarkets(searchByMarketName, searchByMarketCode,
				clientAccountId, maxResults, api);
		return response.getMarkets();
	}

	public MarketDTO[] listSpreadMarkets(String searchByMarketName, String searchByMarketCode, int clientAccountId,
			int maxResults) throws ApiException {
		ListSpreadMarketsResponseDTO response = methods.ListSpreadMarkets(searchByMarketName, searchByMarketCode,
				clientAccountId, maxResults, api);
		return response.getMarkets();
	}

	public ApiTradeOrderResponseDTO order(int MarketId, String Direction, double Quantity, double BidPrice,
			double OfferPrice, String AuditId, int TradingAccountId, String Applicability, String ExpiryDateTimeUTC)
			throws ApiException {
		NewStopLimitOrderRequestDTO data = new NewStopLimitOrderRequestDTO();
		data.setMarketId(MarketId);
		data.setDirection(Direction);
		data.setQuantity(Quantity);
		data.setBidPrice(BidPrice);
		data.setOfferPrice(OfferPrice);
		data.setAuditId(AuditId);
		data.setTradingAccountId(TradingAccountId);
		data.setApplicability(Applicability);
		data.setExpiryDateTimeUTC(ExpiryDateTimeUTC);
		ApiTradeOrderResponseDTO response = methods.Order(data, api);
		return response;
	}

	public ApiTradeOrderResponseDTO cancelOrder(int orderId) throws ApiException {
		CancelOrderRequestDTO data = new CancelOrderRequestDTO();
		ApiTradeOrderResponseDTO response = methods.CancelOrder(data, api);
		return response;
	}

	public ListOrdersResponseDTO listOrders(int tradingAccountId, boolean openOrders, boolean acceptedOrders)
			throws ApiException {
		ListOrdersResponseDTO response = methods.ListOrders(tradingAccountId, openOrders, acceptedOrders, api);
		return response;
	}

	public ApiOpenPositionDTO[] listOpenPositions(int tradingAccountId) throws ApiException {
		ListOpenPositionsResponseDTO response = methods.ListOpenPositions(tradingAccountId, api);
		return response.getOpenPositions();
	}

	public ApiActiveStopLimitOrderDTO[] listActiveStopLimitOrders(int tradingAccountId) throws ApiException {
		ListActiveStopLimitOrderResponseDTO response = methods.ListActiveStopLimitOrders(tradingAccountId, api);
		return response.getActiveStopLimitOrders();
	}

	public ApiTradeHistoryDTO[] listTradeHistory(int tradingAccountId, int maxResults) throws ApiException {
		ListTradeHistoryResponseDTO response = methods.ListTradeHistory(tradingAccountId, maxResults, api);
		return response.getTradeHistory();
	}

	public ApiStopLimitOrderHistoryDTO[] listStopLimitOrderHistory(int tradingAccountId, int maxResults)
			throws ApiException {
		ListStopLimitOrderHistoryResponseDTO response = methods.ListStopLimitOrderHistory(tradingAccountId, maxResults,
				api);
		return response.getStopLimitOrderHistory();
	}

	public ApiTradeOrderResponseDTO trade(int MarketId, String Direction, double Quantity, double BidPrice,
			double OfferPrice, String AuditId, int TradingAccountId) throws ApiException {
		NewTradeOrderRequestDTO data = new NewTradeOrderRequestDTO();
		data.setMarketId(MarketId);
		data.setDirection(Direction);
		data.setQuantity(Quantity);
		data.setBidPrice(BidPrice);
		data.setOfferPrice(OfferPrice);
		data.setAuditId(AuditId);
		data.setTradingAccountId(TradingAccountId);
		ApiTradeOrderResponseDTO response = methods.Trade(data, api);
		return response;
	}

	private void keepAlive() throws ApiException {
		if (keepAlive) {
			logIn(username, password, keepAlive);
		}
	}
}
