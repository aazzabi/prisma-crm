package Services;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import Entities.Product;
import Entities.RepairRequest;
import Entities.Store;
import Entities.Vehicule;
import Enums.RepairStatus;
import Interfaces.IRepaiRequest;


@Stateless
public class RepairRequestService implements IRepaiRequest{

	
	
	@PersistenceContext
	EntityManager entityManager;
	@Override
	public RepairRequest createRepairRequest(RepairRequest repairRequest) {
		try {
			entityManager.persist(repairRequest);
			repairRequest.getId();
		} catch (Exception e) {
			System.err.println("Failed to Add");
		}
		return repairRequest;
	}

	@Override
	public List<RepairRequest> getAllRepairRequest() {

		List<RepairRequest> RepairRequest = entityManager.createQuery("from RepairRequest", RepairRequest.class).getResultList();
		return RepairRequest;
		
	}

	@Override
	public int updateRepairRequest(int id) {
		RepairRequest s = entityManager.find(RepairRequest.class,id);
		s.setStatusRep(s.getStatusRep());
			return s.getId();
		
	}

	@Override
	public boolean deleteRepairRequest(int id) {
		entityManager.remove(entityManager.find(RepairRequest.class, id));
		return true;
	}

	@Override
	public RepairRequest findRepairRequest(int id) {

		entityManager.find(RepairRequest.class, id);

		
		return null;
	}

	@Override
	public void updateRepairRequestStatus(int id,RepairStatus status) {

		RepairRequest t = findRepairRequest(id);
		
		t.setStatusRep(status);
		entityManager.merge(t);

	}

	@Override
	public void RepairRequestTreatment(int id) {
		RepairRequest s = entityManager.find(RepairRequest.class,id);
		
		
	}
	public Date getDateNow() {
		SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String pattern = "yyyy-MM-dd HH:mm:ss.SSS";

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, new Locale("fr", "FR"));

		String date = simpleDateFormat.format(new Date());
		System.out.println("DATE " + date);

		java.sql.Timestamp sqlTimestamp = java.sql.Timestamp.valueOf(date);
		java.util.Date improperUtilDate = sqlTimestamp;

		return improperUtilDate;
	}
}
