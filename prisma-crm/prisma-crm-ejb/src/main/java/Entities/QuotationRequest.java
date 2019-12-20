package Entities;

import java.io.Serializable;
import java.sql.Date;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="quotation")
public class QuotationRequest implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private int id;
	private Date createdAt;
	private String surferEmail;
	private String surferName;
	private String surferProfession;
	private String senderType;
	private boolean isProcessed;
	@ManyToOne
	private Client client;
	@OneToOne
	private Quotation quotation;
	@OneToMany
	private Set<QuotationRequestProductRow> rows;
	@ManyToOne
	private Agent processedBy;
	public Set<QuotationRequestProductRow> getRows() {
		return rows;
	}
	public void setRows(Set<QuotationRequestProductRow> rows) {
		this.rows = rows;
	}
	public Quotation getQuotation() {
		return quotation;
	}
	public void setQuotation(Quotation quotation) {
		this.quotation = quotation;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public String getSurferEmail() {
		return surferEmail;
	}
	public void setSurferEmail(String surferEmail) {
		this.surferEmail = surferEmail;
	}
	public String getSurferName() {
		return surferName;
	}
	public void setSurferName(String surferName) {
		this.surferName = surferName;
	}
	public String getSurferProfession() {
		return surferProfession;
	}
	public void setSurferProfession(String surferProfession) {
		this.surferProfession = surferProfession;
	}
	public Client getClient() {
		return client;
	}
	public void setClient(Client client) {
		this.client = client;
	}
	public int getId() {
		return id;
	}
	public String getSenderType() {
		return senderType;
	}
	public void setSenderType(String senderType) {
		this.senderType = senderType;
	}
	public boolean isProcessed() {
		return isProcessed;
	}
	public void setProcessed(boolean isProcessed) {
		this.isProcessed = isProcessed;
	}
	public Agent getProcessedBy() {
		return processedBy;
	}
	public void setProcessedBy(Agent processedBy) {
		this.processedBy = processedBy;
	}
	
		
}
