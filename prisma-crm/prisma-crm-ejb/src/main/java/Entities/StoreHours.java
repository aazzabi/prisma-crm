package Entities;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "StoreHours")
public class StoreHours implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	@Column(name = "day")
	private String day;
	
	@Column(name = "openAt")
	private Timestamp openAt;
	
	@Column(name = "closeAt")
	private Timestamp closeAt;
	
	@ManyToOne
	Store store;

	public StoreHours() {
		super();
	}

	public StoreHours(String day, Timestamp openAt, Timestamp closeAt) {
		super();
		this.day = day;
		this.openAt = openAt;
		this.closeAt = closeAt;
	}

	public StoreHours(int id, String day, Timestamp openAt, Timestamp closeAt) {
		super();
		this.id = id;
		this.day = day;
		this.openAt = openAt;
		this.closeAt = closeAt;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public Timestamp getOpenAt() {
		return openAt;
	}

	public void setOpenAt(Timestamp openAt) {
		this.openAt = openAt;
	}

	public Timestamp getCloseAt() {
		return closeAt;
	}

	public void setCloseAt(Timestamp closeAt) {
		this.closeAt = closeAt;
	}

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	} 

	
}
