package Interfaces;

import java.util.List;

import Entities.Invoice;

public interface IInvoiceLocal {

	public Invoice createInvoice(int orderId);

	public List<Invoice> getClientInvoices(int orderId);
	
	public Invoice deleteInvoice(int orderId);
	
	public List<Invoice> fetchInvoices();
	
	public List<Invoice> searchForInvoices(String criteria,String value);
	
	public boolean sendInvoiceToClient(int invoiceId);
	
	public String getCurrencyCurrentValue(String base,String trgt,int invoiceId);
	
	public String getInvoiceProductRows(int invoice);
	
	
	


}
