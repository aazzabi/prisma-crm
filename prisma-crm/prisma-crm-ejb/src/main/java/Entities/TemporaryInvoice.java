package Entities;

import java.io.Serializable;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="temoraryInvoice")
public class TemporaryInvoice implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private int id;
	private Date createdAt;
	private Date deadline;
	@ManyToOne
	private ClientOrder order;
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public Date getDeadline() {
		return deadline;
	}
	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}
	public ClientOrder getOrder() {
		return order;
	}
	public void setOrder(ClientOrder order) {
		this.order = order;
	}
	public int getId() {
		return id;
	}
	
	
}
