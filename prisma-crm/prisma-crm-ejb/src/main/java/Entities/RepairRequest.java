package Entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import Enums.RepairStatus;

@Entity
public class RepairRequest implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private int id;
	private Date warentyExp; 
	@ManyToOne
	private Client client;
	private RepairStatus statusRep;
	private Date createdDate;
	private String notes;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Date getWarentyExp() {
		return warentyExp;
	}
	public void setWarentyExp(Date warentyExp) {
		this.warentyExp = warentyExp;
	}
	public Client getClient() {
		return client;
	}
	public void setClient(Client client) {
		this.client = client;
	}
	public RepairStatus getStatusRep() {
		return statusRep;
	}
	public void setStatusRep(RepairStatus statusRep) {
		this.statusRep = statusRep;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public RepairRequest(int id, Date warentyExp, Client client, RepairStatus statusRep, Date createdDate,
			String notes) {
		super();
		this.id = id;
		this.warentyExp = warentyExp;
		this.client = client;
		this.statusRep = statusRep;
		this.createdDate = createdDate;
		this.notes = notes;
	}
	public RepairRequest() {
		super();
	}
	@Override
	public String toString() {
		return "RepairRequest [id=" + id + ", warentyExp=" + warentyExp + ", client=" + client + ", statusRep="
				+ statusRep + ", createdDate=" + createdDate + ", notes=" + notes + "]";
	}


	
}
