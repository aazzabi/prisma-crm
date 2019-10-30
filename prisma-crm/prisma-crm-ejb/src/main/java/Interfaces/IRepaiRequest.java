package Interfaces;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Remote;

import Entities.RepairRequest;
import Enums.RepairStatus;
@Remote
@LocalBean
public interface IRepaiRequest {
	public RepairRequest createRepairRequest(RepairRequest repairRequest) ;
	public List<RepairRequest> getAllRepairRequest();
	public int updateRepairRequest(int id);
	public boolean deleteRepairRequest(int id);
	public RepairRequest findRepairRequest(int id);
	//Metier
	public void updateRepairRequestStatus(int id, RepairStatus status);
public void RepairRequestTreatment (int id );

}
