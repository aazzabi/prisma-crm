package Interfaces;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Remote;

import Entities.RepairRequest;
import Enums.RepairStatus;
@Remote
@LocalBean
public interface IRepaiRequest {
	public int createRepairRequest(RepairRequest repairRequest) ;
	public List<RepairRequest> getAllRepairRequest();
	public boolean updateRepairRequest(RepairRequest repairRequest);
	public boolean deleteRepairRequest(int id);
	public RepairRequest findRepairRequest(int id);
	//Metier
	public void updateRepairRequestStatus(int id, RepairStatus status);

}
