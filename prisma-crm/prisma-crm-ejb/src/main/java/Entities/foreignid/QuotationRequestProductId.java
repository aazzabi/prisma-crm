package Entities.foreignid;

import java.io.Serializable;
 
public class QuotationRequestProductId implements Serializable{
	private static final long serialVersionUID = 1L;
	private int product;
	private int quotationRequest;
	public int getProduct() {
		return product;
	}
	public void setProduct(int product) {
		this.product = product;
	}
	public int getQuotationRequest() {
		return quotationRequest;
	}
	public void setQuotationRequest(int quotationRequest) {
		this.quotationRequest = quotationRequest;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + product;
		result = prime * result + quotationRequest;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		QuotationRequestProductId other = (QuotationRequestProductId) obj;
		if (product != other.product)
			return false;
		if (quotationRequest != other.quotationRequest)
			return false;
		return true;
	}
	
	
}
