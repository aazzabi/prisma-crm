package Entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


import javax.persistence.Column;
import javax.persistence.Entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Tariff")
public class Tariff  implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	@Column(name = "cnxSpeed")
	private int cnxSpeed;
	
	@Column(name = "priceT")
	private double priceT;
	
	
	@OneToMany(mappedBy="tariff")
	private List<TarifProduct> tafifProductList = new ArrayList<TarifProduct>();
	
	public Tariff() {
		super();
	}

	public Tariff(int cnxSpeed, double priceT) {
		super();
		this.cnxSpeed = cnxSpeed;
		this.priceT = priceT;
	}

	public Tariff(int id, int cnxSpeed, double priceT) {
		super();
		this.id = id;
		this.cnxSpeed = cnxSpeed;
		this.priceT = priceT;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCnxSpeed() {
		return cnxSpeed;
	}

	public void setCnxSpeed(int cnxSpeed) {
		this.cnxSpeed = cnxSpeed;
	}

	public double getPriceT() {
		return priceT;
	}

	public void setPriceT(double priceT) {
		this.priceT = priceT;
	}

	public List<TarifProduct> getTarifProductList() {
		return tafifProductList;
	}

	public void setTarifProductList(List<TarifProduct> tafifProductList) {
		this.tafifProductList = tafifProductList;
	}
	

	

}
