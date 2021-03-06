package CIAPI.Java.core.dto;

/**
 * !This is an auto generated model object!
 *
 * The response from the trade request
 */
public class ApiTradeOrderResponseDTO {

	/**
	 * No argument constructor 
	 */
	public ApiTradeOrderResponseDTO() {
	}
	
	private int Status;
	
	/**
	 * The status of the order (Pending, Accepted, Open, etc)
	 */
	public int getStatus() {
		return Status;
	}
	
	public void setStatus(int Status) {
		this.Status = Status;
	}
	private int StatusReason;
	
	/**
	 * The id corresponding to a more descriptive reason for the order status
	 */
	public int getStatusReason() {
		return StatusReason;
	}
	
	public void setStatusReason(int StatusReason) {
		this.StatusReason = StatusReason;
	}
	private int OrderId;
	
	/**
	 * The unique identifier associated to the order returned from the underlying trading system
	 */
	public int getOrderId() {
		return OrderId;
	}
	
	public void setOrderId(int OrderId) {
		this.OrderId = OrderId;
	}
	private ApiOrderResponseDTO[] Orders;
	
	/**
	 * List of orders with their associated response
	 */
	public ApiOrderResponseDTO[] getOrders() {
		return Orders;
	}
	
	public void setOrders(ApiOrderResponseDTO[] Orders) {
		this.Orders = Orders;
	}
	private ApiQuoteResponseDTO Quote;
	
	/**
	 * Quote response
	 */
	public ApiQuoteResponseDTO getQuote() {
		return Quote;
	}
	
	public void setQuote(ApiQuoteResponseDTO Quote) {
		this.Quote = Quote;
	}
}

