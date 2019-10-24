package Services;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import Entities.Product;
import Entities.RepairRequest;
import Entities.Vehicule;
import Enums.RepairStatus;
import Interfaces.IRepaiRequest;


@Stateless
public class RepairRequestService implements IRepaiRequest{

	
	
	@PersistenceContext
	EntityManager entityManager;
	@Override
	public int createRepairRequest(RepairRequest repairRequest) {
		try {
			entityManager.persist(repairRequest);
			repairRequest.getId();
		} catch (Exception e) {
			System.err.println("Failed to Add");
			return 0;
		}
		return repairRequest.getId();
	}

	@Override
	public List<RepairRequest> getAllRepairRequest() {

		List<RepairRequest> RepairRequest = entityManager.createQuery("from RepairRequest", RepairRequest.class).getResultList();
		return RepairRequest;
		
	}

	@Override
	public boolean updateRepairRequest(RepairRequest repairRequest) {
		try {
			entityManager.merge(repairRequest);
			return true;
		} catch (Exception e) {
			System.err.println("Failed to updatee");
			return false;
		}
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

}
