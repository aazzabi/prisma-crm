package Services;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.Days;

import javax.ejb.Schedule;
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
import Utils.Emailer;
import Utils.Mailer;

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

		em.merge(vehiculeMaintenance);

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
	@Override
//	@Schedule(hour = "8", minute = "0", second = "0", persistent = false)
  //  @Schedule(second = "*/5", minute = "*", hour = "*", persistent = false)
	public List<VehiculeMaintenance> alertEntreiens() {
		List<VehiculeMaintenance> realEv = new ArrayList<>();
		List<VehiculeMaintenance> ev = em.createQuery("select ev from VehiculeMaintenance ev where ev.repairStatus = 2  ").getResultList();

		for (VehiculeMaintenance entretienVoiture : ev) {
			if (entretienVoiture.getMaintainceDate() != null) {
				int intervalle = 180;
				int days = Days.daysBetween(new DateTime(entretienVoiture.getMaintainceDate() ),new DateTime(new Date())).getDays();
				if (days >= intervalle) {
					realEv.add(entretienVoiture);
					System.out.println("daysssssssssssssssssssssssssssssss !" +days+" id : "+entretienVoiture.getId());
					
			Emailer.SendEmail(entretienVoiture.getVehicule().getDriver().getEmail(), "vehicle maintenance" ,"Hey, you have depassed the car maintenance deadline with : "+ days +"days");
				}
			}
			if (entretienVoiture.getOdometer() != 0) {
				float kilos = 10000;
				
				float KmLastEntretien = entretienVoiture.getOdometer()
						- entretienVoiture.getVehicule().getOdometer();

				if (KmLastEntretien >= kilos) {
					System.out.println("intervaleKilometres+++++++++++++++ !" +KmLastEntretien +" id : "+entretienVoiture.getId());
					realEv.add(entretienVoiture);
					Emailer.SendEmail(entretienVoiture.getVehicule().getDriver().getEmail(), "vehicle maintenance" ,"Hey, you have depassed the car maintenance Km: "+ KmLastEntretien);

				}
			}

		}
		return realEv;

	}

}

