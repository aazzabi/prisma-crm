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
import Utils.JavaMailUtil;
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
	public void deleteVehiculeMt(int id) {
		em.remove(em.find(VehiculeMaintenance.class, id));

	}
	@Override
	public int addMaintanceRequest(VehiculeMaintenance vehiculeMaintenance) {
		if (vehiculeMaintenance.getOdometer() >= vehiculeMaintenance.getVehicule().getOdometer()) {
			em.merge(vehiculeMaintenance);
		}
		return vehiculeMaintenance.getId();
	}

	@Override
	public void ApproveMaintance(int id) {
		VehiculeMaintenance s = em.find(VehiculeMaintenance.class, id);
		s.setMaintainceDate(new Date());
		s.setRepairStatus(RepairStatus.Completed);
		s.getVehicule().setOdometer(s.getOdometer());
		em.merge(s.getVehicule());
		em.merge(s);
	}

	@Override
	public void RejectMaintance(int id) {
		VehiculeMaintenance s = em.find(VehiculeMaintenance.class, id);

		s.setRepairStatus(RepairStatus.Rejected);
		em.merge(s);
	}

	@Override
	public Vehicule findMostMaintainedVehicule() throws Exception {

		Query query = em
				.createQuery("SELECT v " + "FROM VehiculeMaintenance As r , Vehicule as v " + "Where r.vehicule = v  "
						+ "AND r.serviceType = :s " + "group by v ORDER BY Count(v) DESC", Vehicule.class)
				.setParameter("s", ServiceType.Reparation);
		Vehicule ob = (Vehicule) query.getResultList().get(0);
		System.out.println("size " + ob.getVehiculeMaintenancesType(ServiceType.Reparation).size());
		JavaMailUtil.sendMail(ob.getDriver().getEmail(), "Most Repared Vehicule",
		"Hey, your vehicule is the most repared one (like "+ob.getVehiculeMaintenancesType(ServiceType.Reparation).size()+" times in this period) so please be cautious for the road safety");

		return ob;
	}

	@Override
	public List<VehiculeMaintenance> findMaintancebyVehicule(int id) {
		Vehicule s = em.find(Vehicule.class, id);

		return s.getVehiculeMaintenances();

	}

	@Override
	// @Schedule(hour = "8", minute = "0", second = "0", persistent = false)
	// @Schedule(second = "*", minute = "*/5", hour = "*", persistent = false)
	public List<VehiculeMaintenance> alertEntreiens() throws Exception {
		List<VehiculeMaintenance> realEv = new ArrayList<>();
		List<VehiculeMaintenance> ev = em.createQuery(
				"select ev from VehiculeMaintenance ev where ev.repairStatus = 0 ORDER BY ev.maintainceDate DESC")
				.getResultList();
		List<VehiculeMaintenance> evparVeh = new ArrayList<VehiculeMaintenance>();
		for (VehiculeMaintenance main : ev) {
			boolean exist =false;
			for (VehiculeMaintenance mainex : evparVeh) {
				if (main.getVehicule().getId() == mainex.getVehicule().getId()) 
				{
					exist=true;
				}

			}	
			if (!exist)
				evparVeh.add(main);


		}

		for (VehiculeMaintenance entretienVoiture : evparVeh) {
			if (entretienVoiture.getMaintainceDate() != null) {
				int intervalle = 180;
				int days = Days
						.daysBetween(new DateTime(entretienVoiture.getMaintainceDate()), new DateTime(new Date()))
						.getDays();
				if (days >= intervalle) {
					realEv.add(entretienVoiture);
					System.out.println(
							"daysssssssssssssssssssssssssssssss !" + days + " id : " + entretienVoiture.getId());

//					JavaMailUtil.sendMail(entretienVoiture.getVehicule().getDriver().getEmail(), "vehicle maintenance",
//							"Hey, you have depassed the car maintenance deadline with : " + days + "days");
				}
			}
			if (entretienVoiture.getOdometer() != 0) {
				float kilos = 10000;

				float KmLastEntretien = entretienVoiture.getVehicule().getOdometer() - entretienVoiture.getOdometer();

				if (KmLastEntretien >= kilos) {
					System.out.println("intervaleKilometres+++++++++++++++ !" + KmLastEntretien + " id : "
							+ entretienVoiture.getId());
					realEv.add(entretienVoiture);
					// JavaMailUtil.sendMail(entretienVoiture.getVehicule().getDriver().getEmail(),
					// "vehicle maintenance",
					// "Hey, you have depassed the car maintenance Km: " + KmLastEntretien);

				}
			}

		}
		return realEv;

	}

}
