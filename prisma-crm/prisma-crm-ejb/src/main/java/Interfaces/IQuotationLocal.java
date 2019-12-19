package Interfaces;

import javax.ejb.Local;

import Entities.Product;
import Entities.QuotationRequest;
@Local
public interface IQuotationLocal {

	public String createQuotationRequest(QuotationRequest request);

	public Product addProductToQuotationRequest(Product product, int requestId,int quantity);

	public Product deleteProductFromQuotationRequest(Product product, int requestId);

	public boolean processQuotationRequest(QuotationRequest request, int agentId,boolean response);

	public String deleteQuotationRequest(QuotationRequest request);

}
