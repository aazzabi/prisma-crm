package Services;

import java.security.Permissions;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import Entities.Invoice;
import Entities.Product;
import Entities.RepairRequest;
import Entities.Store;
import Entities.Vehicule;
import Enums.RepairStatus;
import Enums.Role;
import Interfaces.IRepaiRequest;
import Utils.Sms;

@Stateless
public class RepairRequestService implements IRepaiRequest {

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
		s.setEndDate(reprequest.getCreatedDate());
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
		s.setEndDate(s.getEndDate());
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

}
