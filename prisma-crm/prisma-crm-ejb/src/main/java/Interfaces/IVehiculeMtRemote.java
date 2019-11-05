package Interfaces;

import java.util.List;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Remote;

import Entities.Agent;
import Entities.Vehicule;
import Entities.VehiculeMaintenance;
import Enums.RepairStatus;

@Remote
@LocalBean
public interface IVehiculeMtRemote {
	public int addVehicule(Vehicule vehicule) ;
	public Vehicule getVehiculeById(int id) ;
	public void updateVehicule(Vehicule veh) ;
	public List<Vehicule> findAllVehicule() ;
	public void deleteVehicule(int id);
	public int addMaintanceRequest(VehiculeMaintenance vehiculeMaintenance) ;
	public Vehicule findMostMaintainedVehicule() throws Exception;
	public List<VehiculeMaintenance> findMaintancebyVehicule(int id);
	public List<VehiculeMaintenance> alertEntreiens() throws Exception;
	void RejectMaintance(int id);
	void ApproveMaintance(int id);
	void deleteVehiculeMt(int id);

}
