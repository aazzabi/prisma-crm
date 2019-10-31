package Interfaces;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Remote;

import Entities.Vehicule;

@Remote
@LocalBean
public interface IResourcesRemote {
	public int addVehicule(Vehicule vehicule) ;
	public Vehicule getVehiculeById(int id) ;
	public void updateVehicule(Vehicule veh) ;
	public List<Vehicule> findAllVehicule() ;
	public void deleteVehicule(int id);

}
