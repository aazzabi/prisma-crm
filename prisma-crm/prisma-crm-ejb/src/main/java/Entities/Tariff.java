package Entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
	
	
	@OneToMany(mappedBy="tariff",fetch=FetchType.EAGER)
	private Set<TarifProduct> tafifProductList ;
	
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

	public Set<TarifProduct> getTarifProductList() {
		return tafifProductList;
	}

	public void setTarifProductList(Set<TarifProduct> tafifProductList) {
		this.tafifProductList = tafifProductList;
	}



}
