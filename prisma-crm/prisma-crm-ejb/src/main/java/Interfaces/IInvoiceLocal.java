package Interfaces;

import java.util.List;

import Entities.Invoice;

public interface IInvoiceLocal {

	public Invoice createInvoice(int orderId);

	public List<Invoice> getClientInvoices(int clientId);
	
	public Invoice deleteInvoice(int orderId);
	
	public List<Invoice> fetchInvoices();
	


}
