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
			manager.persist(invoice);
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
	public float getCurrencyCurrentValue(String base,String trgt,float ammount)
	{
		javax.ws.rs.client.Client client = ClientBuilder.newClient();
		WebTarget target = client.target(CURRENCY_API_URL).path(Float.toString(ammount)).path(base).path(trgt);
		Response response = target.request().header("Authorization", CURRENCY_API_KEY).get();
		
		Map<String,Object> responseStr =(Map<String,Object>) response.readEntity(Map.class);
		client.close();
		Double amount=(Double)responseStr.get("amount");
		return amount.floatValue();
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

}
