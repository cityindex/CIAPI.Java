package CIAPI.Java.pricewidget.model;

import java.util.ArrayList;
import java.util.List;

import CIAPI.Java.core.ServiceMethods;
import CIAPI.Java.core.dto.AccountInformationResponseDTO;
import CIAPI.Java.core.dto.ApiMarketDTO;
import CIAPI.Java.core.dto.ListCfdMarketsResponseDTO;
import CIAPI.Java.core.impl.ServiceMethodsImpl;
import JsonClient.Java.ApiException;
import android.content.Context;

public class StockSearch implements IStockSearch {
	private Context context;
	private ServiceMethods methods = new ServiceMethodsImpl();

	private int clientAccountId;

	public StockSearch(Context context) {
		this.context = context;
		try {
			AccountInformationResponseDTO accountinfo = methods.GetClientAndTradingAccount(LogOnStatus.api());
			clientAccountId = accountinfo.getClientAccountId();
		} catch (ApiException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<IStock> searchForName(String name, int maxResults) {
		try {
			List<IStock> ret = new ArrayList<IStock>();
			ListCfdMarketsResponseDTO market = methods.ListCfdMarkets(name, null, clientAccountId, maxResults,
					LogOnStatus.api());
			ApiMarketDTO[] mkts = market.getMarkets();
			for (ApiMarketDTO dto : mkts) {
				ret.add(new RealStock(dto.getMarketId(), context));
			}
		} catch (ApiException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public IStock getFromRICCode(String ric) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStock getById(int id) {
		try {
			return new RealStock(id, context);
		} catch (ApiException e) {
			e.printStackTrace();
			throw new Error("Shit, get by id failed.");
		}
	}
}
