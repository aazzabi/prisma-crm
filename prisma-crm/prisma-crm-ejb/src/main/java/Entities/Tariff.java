package Entities;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import javax.persistence.Table;


@Entity
@Table(name = "Tariff")
public class Tariff implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	@Column(name = "cnxSpeed")
	private int cnxSpeed;
	
	@Column(name = "priceT")
	private double priceT;
	

	
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





}