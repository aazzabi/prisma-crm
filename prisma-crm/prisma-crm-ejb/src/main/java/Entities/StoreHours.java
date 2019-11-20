package Entities;

import java.io.Serializable;
import java.sql.Timestamp;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "StoreHours")
public class StoreHours implements Serializable{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private String day;
	
	@JsonFormat(pattern="HH:mm",timezone = "GMT+1")
	private Timestamp openAt;
	
	@JsonFormat(pattern="HH:mm",timezone = "GMT+1")
	private Timestamp closeAt;
	
	@ManyToOne
	@JsonIgnore
	Store store;

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
