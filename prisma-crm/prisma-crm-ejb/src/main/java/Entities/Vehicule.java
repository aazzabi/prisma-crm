package Entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import Enums.FuelType;
@Entity
public class Vehicule implements Serializable {
	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private int id;
	private String plate;
	private float odometer;
	private FuelType fuelType;
	@OneToOne
	private Agent driver;
	private float location;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public float getOdometer() {
		return odometer;
	}
	public void setOdometer(float odometer) {
		this.odometer = odometer;
	}
	public FuelType getFuelType() {
		return fuelType;
	}
	public void setFuelType(FuelType fuelType) {
		this.fuelType = fuelType;
	}
	public Agent getDriver() {
		return driver;
	}
	public void setDriver(Agent driver) {
		this.driver = driver;
	}
	public float getLocation() {
		return location;
	}
	public void setLocation(float location) {
		this.location = location;
	}

	public Vehicule(int id, float odometer, FuelType fuelType, Agent driver, float location) {
		super();
		this.id = id;
		this.odometer = odometer;
		this.fuelType = fuelType;
		this.driver = driver;
		this.location = location;
	}
	
	public Vehicule(int id, String plate, float odometer, FuelType fuelType, float location) {
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
	

}
