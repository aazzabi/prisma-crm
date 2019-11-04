package Services;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import Entities.User;
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
		em.merge(veh);}
	@Override
	public List<Vehicule> findAllVehicule() {
		Query query = em.createQuery(
				"SELECT new Vehicule(u.id,u.fuelType,u.location,u.location,u.odometer,u.plate,u.driver_id) "
						+ "FROM Vehicule u");
		return (List<Vehicule>) query.getResultList();
	}
	@Override
	public void deleteVehicule(int id)
	{
		em.remove(em.find(Vehicule.class, id));

	}
	@Override
	public Vehicule assignDriverToVehicule(int Driver_id, int Vehicule_id) {
		
		Vehicule v= em.find(Vehicule.class, Vehicule_id);
		User driver= em.find(User.class, Driver_id);

		v.setDriver(driver);
		em.merge(v);
		em.flush();
		return v;
		
	}

}
