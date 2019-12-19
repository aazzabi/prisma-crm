package Services;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import Entities.Agent;
import Entities.Claim;
import Entities.Vehicule;
import Interfaces.IResourcesRemote;

@Stateless
public class ResourcesService implements IResourcesRemote {
	
	@PersistenceContext
	EntityManager em;
	
	
	@Override
	public int addVehicule(Vehicule vehicule) {
	
		em.persist(vehicule);
		return vehicule.getId();
	}
	@Override
	public Vehicule getVehiculeById(int id) {
		return em.find(Vehicule.class, id);
	}
	@Override
	public void updateVehicule(Vehicule veh) {
		Vehicule p = em.find(Vehicule.class, veh.getId());
		if (veh.getFuelType() != null) {
			p.setFuelType(veh.getFuelType());
		}
		if (veh.getOdometer()!=0) {
			p.setOdometer(veh.getOdometer());
		}
		if (veh.getPlate() != null) {
			p.setPlate(veh.getPlate());
		}
	}
	
	@Override
	public List<Vehicule> findAllVehicule() {
		TypedQuery<Vehicule> query = em.createQuery("SELECT c from Vehicule c", Vehicule.class);
		List<Vehicule> cf = query.getResultList();
		return cf;

	}

	@Override
	public void deleteVehicule(int id)
	{
		em.remove(em.find(Vehicule.class, id));

	}
	@Override
	public Vehicule assignDriverToVehicule(int Driver_id, int Vehicule_id) {
		
		Vehicule v= em.find(Vehicule.class, Vehicule_id);
		Agent driver= em.find(Agent.class, Driver_id);

		v.setDriver(driver);
		em.merge(v);
		em.flush();
		return v;
		
	}

}
