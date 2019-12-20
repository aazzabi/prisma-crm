package Services;

import java.util.Date;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import Entities.Agent;
import Entities.Client;
import Entities.Product;
import Entities.Quotation;
import Entities.QuotationRequest;
import Entities.QuotationRequestProductRow;
import Interfaces.IQuotationLocal;
import Interfaces.IQuotationRemote;

@LocalBean
@Stateless
public class QuotationService implements IQuotationLocal, IQuotationRemote {
	@PersistenceContext(unitName = "prisma-crm-ejb")
	EntityManager manager;

	// Client task
	@Override
	public String createQuotationRequest(QuotationRequest request) {
		if (request != null) {
			if (request.getSenderType().equals("internaute")) {
				manager.persist(request);
				return "Demande de devis envoyés avec succès";
			} else {
				if (request.getSenderType().equals("client")) {
					Client client = manager.find(Client.class, request.getClient().getId());
					if (client != null) {
						manager.persist(request);
						return "Demande de devis envoyée avec succès";
					} else {
						return null;
					}
				} else
					return null;
			}
		} else
			return null;
	}
	
	//Client task
	@Override
	public Product addProductToQuotationRequest(Product product, int requestId, int quantity) {
		QuotationRequest request = manager.find(QuotationRequest.class, requestId);
		if ((request != null) && (product != null) && (product.getStock() >= quantity)) {
			QuotationRequestProductRow row = new QuotationRequestProductRow();
			row.setOriginaUnitlPrice(product.getPrice());
			row.setQuantity(quantity);
			row.setProduct(product);
			row.setQuotationRequest(request);
			row.setFinalPrice(quantity * product.getPrice());
			manager.persist(row);
			manager.flush();
			return product;
		} else
			return null;
	}

	
	//Client task
	@Override
	public Product deleteProductFromQuotationRequest(Product product, int requestId) {
		QuotationRequest request = manager.find(QuotationRequest.class, requestId);
		if ((request != null) && (product != null)) {
			@SuppressWarnings("unused")
			QuotationRequestProductRow row = (QuotationRequestProductRow) manager.createQuery(
					"SELECT Q FROM QuotationRequestProductRow Q WHERE Q.product=:product and Q.quotationRequest=:request")
					.setParameter("product", product).setParameter("request", request).getSingleResult();
			if (row != null) {
				manager.persist(row);
				manager.flush();
			}
		}
		return null;
	}

	// Admin task
	@Override
	public boolean processQuotationRequest(QuotationRequest request, int agentId, boolean response) {
		Agent agent = manager.find(Agent.class, agentId);
		if ((agent != null) && (request != null)) {
			request.setProcessedBy(agent);
			request.setProcessed(true);
			if (response) {
				Quotation quotation = new Quotation();
				quotation.setValid(true);
				Date date = new Date();
				quotation.setCreatedAt(new java.sql.Date(date.getTime()));
				// Envoi du mail
				manager.persist(quotation);
			}
			manager.merge(request);
			manager.flush();
			return true;
		}

		else
			return false;
	}

	//Client task
	@Override
	public String deleteQuotationRequest(QuotationRequest request) {
		if ((request != null) && (!request.isProcessed())) {
			request.setQuotation(null);
			manager.remove(request);
			manager.flush();
			return "Demande de devis est supprimé avec succès";
		}

		else
			return "Impossible de supprimer la demande du devis";
	}

}
