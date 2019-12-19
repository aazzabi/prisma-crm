package Services;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import Entities.CartProductRow;
import Entities.Product;
import Entities.TemporaryInvoice;
import Enums.OrderType;
import Interfaces.ITemporaryInvoiceLocal;

@LocalBean
public class TemporaryInvoiceService implements ITemporaryInvoiceLocal {
	@PersistenceContext(unitName = "prisma-crm-ejb")
	EntityManager manager;

	@Override
	public TemporaryInvoice createTemporaryInvoice(TemporaryInvoice invoice) {
		if ((invoice != null) && (invoice.getOrder().getOrderNature() == OrderType.LocalPaymentMethod)) {
			manager.persist(invoice);
			sendTemporaryInvoiceAsAnEmailToClient(invoice);
			return invoice;
		} else
			return null;
	}

	@Override
	public List<TemporaryInvoice> groupTemporaryInvoicesByCriteria() {

		return null;
	}

	@Override
	public TemporaryInvoice getMostValuableTemporaryInvoice() {

		return null;
	}

	@Override
	public List<Product> getTemporaryInvoiceProducts(TemporaryInvoice invoice) {
		if (invoice != null) {
			List<Product> products = new ArrayList<>();
			if ((invoice.getOrder() != null) && ((invoice.getOrder().getCart().getCartRows().size() > 0))) {
				for (CartProductRow row : invoice.getOrder().getCart().getCartRows()) {
					products.add(row.getProduct());
				}
				return products;
			}
			else return null;
		}
		else return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TemporaryInvoice> fetchInvoices() {
		
		return (List<TemporaryInvoice>)manager.createQuery("SELECT R FROM TemporaryInvoice R").getResultList();
	}

	// This one will be purged in a cron job class
	public boolean deleteInvoicesAfterDeadLine() {
		return false;
	}

	// This function will send mail to the client / surfer
	public boolean sendTemporaryInvoiceAsAnEmailToClient(TemporaryInvoice invoice) {
		return false;
	}

}
