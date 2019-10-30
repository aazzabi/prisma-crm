package Services;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;

import Entities.User;
import Entities.Vehicule;
import Entities.VehiculeMaintenance;
import Enums.RepairStatus;
import Enums.ServiceType;
import Interfaces.IVehiculeMtRemote;

@Stateless
public class VehiculeMaintenanceService implements IVehiculeMtRemote {

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
		em.merge(veh);
	}

	@Override
	public List<Vehicule> findAllVehicule() {
		Query query = em.createQuery(
				"SELECT new Vehicule(u.id,u.fuelType,u.location,u.location,u.odometer,u.plate,u.driver_id) "
						+ "FROM Vehicule u");
		return (List<Vehicule>) query.getResultList();
	}

	@Override
	public void deleteVehicule(int id) {
		em.remove(em.find(Vehicule.class, id));

	}

	@Override
	public int addMaintanceRequest(VehiculeMaintenance vehiculeMaintenance) {

		em.persist(vehiculeMaintenance);

		return vehiculeMaintenance.getId();
	}

	@Override
	public void traitMaintance(int id, RepairStatus r) {
		VehiculeMaintenance s = em.find(VehiculeMaintenance.class, id);
		s.setRepairStatus(r);
		em.merge(s);
	}

	@Override
	public Vehicule findMostMaintainedVehicule() {

		Query query =  em.createQuery(
				"SELECT v "
				+ "FROM VehiculeMaintenance As r , Vehicule as v "
				+ "Where r.vehicule = v  "
				+ "AND r.serviceType = :s "
				+ "ORDER BY Count(r.vehicule) DESC", Vehicule.class)
				.setParameter("s", ServiceType.Reparation);
		Vehicule ob = (Vehicule) query.getResultList().get(0);
		System.out.println("size " + ob.getVehiculeMaintenancesType(ServiceType.Reparation).size());
	   return ob;
	}

	@Override
	public List<VehiculeMaintenance> findMaintancebyVehicule(int id) {
		Vehicule s = em.find(Vehicule.class, id);

		return s.getVehiculeMaintenances();

	}

}

