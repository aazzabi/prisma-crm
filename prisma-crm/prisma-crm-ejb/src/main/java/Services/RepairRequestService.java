package Services;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import Entities.Invoice;
import Entities.Product;
import Entities.RepairRequest;
import Enums.RepairStatus;
import Interfaces.IRepaiRequest;
import Utils.Sms;
import Utils.microsoftSentiments;

@Stateless
public class RepairRequestService implements IRepaiRequest {

	microsoftSentiments ms;
	@PersistenceContext
	EntityManager entityManager;

	@Override
	public RepairRequest createRepairRequest(RepairRequest repairRequest) {

		entityManager.persist(repairRequest);

		return repairRequest;
	}

	@Override
	public List<RepairRequest> getAllRepairRequest() {

		List<RepairRequest> RepairRequest = entityManager.createQuery("from RepairRequest", RepairRequest.class)
				.getResultList();
		return RepairRequest;

	}

	@Override
	public int updateRepairRequest(int id, RepairRequest reprequest) {
		RepairRequest s = entityManager.find(RepairRequest.class, id);
		s.setNotes(reprequest.getNotes());
		s.setStatusRep(reprequest.getStatusRep());
		if (reprequest.getStatusRep() == RepairStatus.Completed || reprequest.getStatusRep() == RepairStatus.Rejected ) {
		s.setEndDate(reprequest.getCreatedDate());
		}
		entityManager.merge(s);
		return s.getId();

	}

	@Override
	public boolean deleteRepairRequest(int id) {
		RepairRequest x = entityManager.find(RepairRequest.class, id);
		x.setInvoice(null);
		x.setClient(null);
		entityManager.merge(x);
		entityManager.remove(x);
		return true;
	}

	@Override
	public RepairRequest findRepairRequest(int id) {

		return entityManager.find(RepairRequest.class, id);
	}

	@Override
	public int RepairRequestStatus(int id, RepairRequest t) {
		String Link = "http://localhost:9080/prisma-crm-web/Repair/check/" + id;
		RepairRequest s = entityManager.find(RepairRequest.class, id);
		s.setNotes(s.getNotes());
		s.setStatusRep(t.getStatusRep());
		if(t.getStatusRep()==RepairStatus.Completed||t.getStatusRep()==RepairStatus.Rejected )
		{		 s.setEndDate(new Date());}
		entityManager.merge(s);
		String sms = Sms.SendSMS(s.getClient().getPhoneNumber(),
				"Hello , Your repair Request status has been changed to :" + t.getStatusRep().name()
						+ " - check it here : " + Link);

		return s.getId();

	}

	@Override
	public void RepairRequestTreatment(int id) {
		RepairRequest s = entityManager.find(RepairRequest.class, id);

	}

	@Override
	public Invoice findInvoiceById(int id) {
		return entityManager.find(Invoice.class, id);

	}

	@Override
	public Product findProductByinvoice(int idp, int idinv) {
		Invoice INVOICE = entityManager.find(Invoice.class, idinv);

		for (Product p : INVOICE.getProducts()) {
			if (p.getId() == idp)
				return p;
		}
		return null;
	}

	@Override
	public String findSentiment() throws JsonParseException, JsonMappingException, IOException {
		List<RepairRequest> listRep = getAllRepairRequest();
		Double SommeScore = 0.0;
		for (RepairRequest rep : listRep) {
			String Sentiment = microsoftSentiments.GetSentimentAnalytics(rep.getReview());

			Double score = microsoftSentiments.getSentiment(Sentiment);
			System.out.println("sentimeeeeeeeeent" + score);
			SommeScore = SommeScore + score;

		}
		SommeScore = SommeScore / listRep.size();
		if (SommeScore <= 0.25) {
			return "They are MAD - Sentiments Score :"+SommeScore;
		} else if (SommeScore <= 0.50) {
			return "They are Moderately mad- Sentiments Score :"+SommeScore;
		} else if (SommeScore <= 0.75) {
			return "They are not very mad but be careful - Sentiments Score :"+SommeScore;
		} else {
			return "They are not mad - Sentiments Score :"+SommeScore;
		}

	}

	@Override
	public String ReviewAdd(String r, int idRepReq) {
		RepairRequest s = entityManager.find(RepairRequest.class, idRepReq);
		if (s.getStatusRep() == RepairStatus.Completed || s.getStatusRep() == RepairStatus.Rejected) {
			s.setReview(r);
			entityManager.merge(s);
			return "Done";
		} else
			return "Repair Request not treated yet";

	}
}
