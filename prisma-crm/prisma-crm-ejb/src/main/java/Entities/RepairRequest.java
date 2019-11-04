package Entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import Enums.RepairStatus;

@Entity
public class RepairRequest implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private int id;
	private Date warentyExp;
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JsonIgnore
	private User client;
	@OneToOne(cascade = CascadeType.REMOVE)
	@JsonIgnore
	private Invoice invoice;
	@Enumerated(EnumType.STRING)
	private RepairStatus statusRep;
	private Date createdDate;
	private String notes;
	private Date endDate;

	public int getId() {
		return id;
	}

	public Date getWarentyExp() {
		return warentyExp;
	}

	public void setWarentyExp(Date warentyExp) {
		this.warentyExp = warentyExp;
	}

	public User getClient() {
		return client;
	}

	public void setClient(User client) {
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

	public RepairRequest(RepairStatus statusRep) {

		this.statusRep = statusRep;

	}

	public RepairRequest(int id, Date warentyExp, User client, RepairStatus statusRep, Date createdDate, String notes) {
		super();
		this.id = id;
		this.warentyExp = warentyExp;
		this.client = client;
		this.statusRep = statusRep;
		this.createdDate = createdDate;
		this.notes = notes;
	}

	public RepairRequest(Date warentyExp, User client, Invoice invoice, RepairStatus statusRep, Date createdDate,
			String notes) {
		super();
		this.warentyExp = warentyExp;
		this.client = client;
		this.invoice = invoice;
		this.statusRep = statusRep;
		this.createdDate = createdDate;
		this.notes = notes;
	}

	public RepairRequest() {
		super();
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	@Override
	public String toString() {
		return "RepairRequest [id=" + id + ", warentyExp=" + warentyExp + ", client=" + client + ", product=" + invoice
				+ ", statusRep=" + statusRep + ", createdDate=" + createdDate + ", notes=" + notes + ", endDate="
				+ endDate + "]";
	}

}
