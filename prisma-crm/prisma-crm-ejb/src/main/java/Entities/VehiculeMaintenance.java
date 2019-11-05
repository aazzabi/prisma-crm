package Entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import Enums.RepairStatus;
import Enums.ServiceType;

/**
 * Entity implementation class for Entity: MaintainceService
 *
 */
@Entity

public class VehiculeMaintenance implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@ManyToOne(fetch = FetchType.EAGER)
	private Vehicule vehicule;

	private ServiceType serviceType;
	private RepairStatus repairStatus;
	private float totalprice;

	private Date maintainceDate;

	private int odometer;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Vehicule getVehicule() {
		return vehicule;
	}

	public void setVehicule(Vehicule vehicule) {
		this.vehicule = vehicule;
	}

	public ServiceType getServiceType() {
		return serviceType;
	}

	public void setServiceType(ServiceType serviceType) {
		this.serviceType = serviceType;
	}

	public float getTotalprice() {
		return totalprice;
	}

	public void setTotalprice(float totalprice) {
		this.totalprice = totalprice;
	}

	public Date getMaintainceDate() {
		return maintainceDate;
	}

	public RepairStatus getRepairStatus() {
		return repairStatus;
	}

	public void setRepairStatus(RepairStatus repairStatus) {
		this.repairStatus = repairStatus;
	}

	public void setMaintainceDate(Date maintainceDate) {
		this.maintainceDate = maintainceDate;
	}

	public int getOdometer() {
		return odometer;
	}

	public void setOdometer(int odometer) {
		this.odometer = odometer;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public VehiculeMaintenance(Vehicule vehicule, ServiceType serviceType, float totalprice, int odometer) {
		super();
		this.vehicule = vehicule;
		this.serviceType = serviceType;
		this.totalprice = totalprice;
		this.odometer = odometer;
	}

	public VehiculeMaintenance(Vehicule vehicule, float totalprice, int odometer) {
		super();
		this.vehicule = vehicule;
		this.totalprice = totalprice;
		this.odometer = odometer;
	}

	public VehiculeMaintenance() {
	}

	@Override
	public String toString() {
		return "VehiculeMaintenance [id=" + id + ", vehicule=" + vehicule + ", serviceType=" + serviceType
				+ ", repairStatus=" + repairStatus + ", totalprice=" + totalprice + ", maintainceDate=" + maintainceDate
				+ ", odometer=" + odometer + "]";
	}

}
