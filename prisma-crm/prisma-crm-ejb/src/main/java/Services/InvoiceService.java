package Services;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import Entities.CartProductRow;
import Entities.ClientOrder;
import Entities.Invoice;
import Interfaces.IInvoiceLocal;

@Stateless
@LocalBean
public class InvoiceService implements IInvoiceLocal {
	
	@PersistenceContext(unitName = "prisma-crm-ejb")
	EntityManager manager;
	private final String CURRENCY_API_KEY="Bearer U3eKuEacN1u2YVRloiw_IvADYNSsEEn_ZnrFG7B-OcgCU80FhIRTG";
	private final String CURRENCY_API_URL="https://currency.labstack.com/api/v1/convert";
	

	@Override
	public Invoice createInvoice(int orderId) {
		ClientOrder order=manager.find(ClientOrder.class, orderId);
		if (order!=null)
		{
			Invoice invoice = new Invoice();
			java.util.Date date=new java.util.Date();
			invoice.setCreatedAt(new Timestamp(date.getTime()));
			invoice.setOrder(order);
			order.setInvoice(invoice);
			manager.persist(invoice);
			manager.merge(order);
			manager.flush();
			return invoice;			
		}
		else
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Invoice> getClientInvoices(int orderId) {
		ClientOrder order=manager.find(ClientOrder.class,orderId);
		if (order!=null)
		{
			return manager.createQuery("SELECT I FROM Invoice I WHERE Invoice.orderInvoice=:order")
						  .setParameter("order", order)
						  .getResultList();
		}
		return null;
	}

	@Override
	public Invoice deleteInvoice(int orderId) {
		ClientOrder order=manager.find(ClientOrder.class, orderId);
		if (order!=null)
		{
			if (order.isValid())
			{
				manager.remove(order.getInvoice());
				manager.flush();
				return order.getInvoice();
			}
			else return null;
		}
		else return null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String getCurrencyCurrentValue(String base,String trgt,int invoiceId)
	{
		Invoice invoice=manager.find(Invoice.class, invoiceId);
		if (invoice!=null)
		{
		javax.ws.rs.client.Client client = ClientBuilder.newClient();
		WebTarget target = client.target(CURRENCY_API_URL).path(Float.toString(invoice.getOrder().getTotale())).path(base).path(trgt);
		Response response = target.request().header("Authorization", CURRENCY_API_KEY).get();
		
		Map<String,Object> responseStr =(Map<String,Object>) response.readEntity(Map.class);
		client.close();
		Double amount=(Double)responseStr.get("amount");
		return "Totale En "+base+" : "+invoice.getOrder().getTotale()
			+"\nTotale En "+trgt+" : "+amount;
		}
		else return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Invoice> fetchInvoices() {
	
		return manager.createQuery("SELECT I FROM Invoice I")
				      .getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Invoice> searchForInvoices(String criteria, String value) {

		return manager.createQuery("select i from Invoice i where i."+criteria+"=:value")
					  .setParameter("value", value)
					  .getResultList();
	}

	@Override
	public boolean sendInvoiceToClient(int invoiceId) {
		
		return false;
	}

	@Override
	public String getInvoiceProductRows(int invoice) {
		Invoice INVOICE=manager.find(Invoice.class,invoice);
		String result="";
		if (INVOICE!=null)
		{
			ClientOrder order=(ClientOrder)manager.createQuery("SELECT O FROM ClientOrder O WHERE O.invoice=:invoice")
									 .setParameter("invoice", INVOICE)
									 .getSingleResult();
			if ((order!=null) && (order.getCart().getCartRows().size()>0))
			{
				for (CartProductRow row:order.getCart().getCartRows())
				{
					result+="--------------------------------------------------\n"
							+"Product reference : "+row.getProduct().getName()+
						  "\nProduct desired quantity : "+row.getQuantity()+"\n"
						 +"\nProduct original price   : "+row.getOriginalPrice()
						+"\nTotal line price		  : "+row.getFinalPrice();
					if (row.getUsedFidelityPoints()==0)
					{
					result+="\nReduction is being applied in this row with the use of "+row.getUsedFidelityPoints()+"\n\n";	
					}
					else result+="\nReduction is not being applied \n\n";	
				}
				return result;
			}
			else return null;
		}
		else return null;
	}

}
