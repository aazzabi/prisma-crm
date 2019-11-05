package Entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.jws.soap.SOAPBinding.Use;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import Enums.FuelType;
import Enums.ServiceType;
@Entity
public class Vehicule implements Serializable {
	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private int id;
	private String plate;
	private int odometer;
	private FuelType fuelType;
	@OneToOne
	private User driver;
	private float location;
	@JsonIgnore
	@OneToMany(mappedBy="vehicule")
	private List<VehiculeMaintenance> vehiculeMaintenances;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getOdometer() {
		return odometer;
	}
	public void setOdometer(int odometer) {
		this.odometer = odometer;
	}
	public FuelType getFuelType() {
		return fuelType;
	}
	public void setFuelType(FuelType fuelType) {
		this.fuelType = fuelType;
	}
	public User getDriver() {
		return driver;
	}
	public void setDriver(User driver) {
		this.driver = driver;
	}
	public float getLocation() {
		return location;
	}
	public void setLocation(float location) {
		this.location = location;
	}

	public Vehicule(int id, int odometer, FuelType fuelType, User driver, float location) {
		super();
		this.id = id;
		this.odometer = odometer;
		this.fuelType = fuelType;
		this.driver = driver;
		this.location = location;
	}
	
	public Vehicule(int id, String plate, int odometer, FuelType fuelType, float location) {
		super();
		this.id = id;
		this.plate = plate;
		this.odometer = odometer;
		this.fuelType = fuelType;
		this.location = location;
	}
	@Override
	public String toString() {
		return "Vehicule [id=" + id + ", plate=" + plate + ", odometer=" + odometer + ", fuelType=" + fuelType
				+ ", driver=" + driver + ", location=" + location + "]";
	}
	public Vehicule() {
		super();
	}
	public String getPlate() {
		return plate;
	}
	public void setPlate(String plate) {
		this.plate = plate;
	}
	public List<VehiculeMaintenance> getVehiculeMaintenances() {
		return vehiculeMaintenances;
	}
	public void setVehiculeMaintenances(List<VehiculeMaintenance> vehiculeMaintenances) {
		this.vehiculeMaintenances = vehiculeMaintenances;
	}
	
	public List<VehiculeMaintenance> getVehiculeMaintenancesType(ServiceType t) { 
		List<VehiculeMaintenance> list = new ArrayList<VehiculeMaintenance>();
		for (VehiculeMaintenance vm : vehiculeMaintenances) {
			if (vm.getServiceType() == t) {
				list.add(vm);
			}
		}
		return list;
	}
	

}
