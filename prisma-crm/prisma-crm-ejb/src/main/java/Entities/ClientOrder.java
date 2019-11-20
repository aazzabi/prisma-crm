package Entities;

import java.io.Serializable;

import java.sql.Date;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import Enums.OrderType;

@Entity
@Table(name = "ClientOrder")
public class ClientOrder implements Serializable {


	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)	
	private int id;
	private Date createdAt;
	private Date updatedAt;
	private boolean isValid;
	private float reductionRatio;
	@Enumerated(EnumType.STRING)	
	private OrderType orderNature;
	private float totale;
	@ManyToOne
	@JsonIgnore
	private Client client;
	@JsonIgnore
	@ManyToOne
	private Store store;
	@JsonIgnore
	@OneToOne
	private Invoice invoice;
	@JsonIgnore
	@OneToOne
	private ClientCart cart;

	public ClientCart getCart() {
		return cart;
	}

	public void setCart(ClientCart cart) {
		this.cart = cart;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

	public float getReductionRatio() {
		return reductionRatio;
	}

	public void setReductionRatio(float reductionRatio) {
		this.reductionRatio = reductionRatio;
	}

	public OrderType getOrderNature() {
		return orderNature;
	}

	public void setOrderNature(OrderType orderNature) {
		this.orderNature = orderNature;
	}

	public float getTotale() {
		return totale;
	}

	public void setTotale(float totale) {
		this.totale = totale;
	}

	public int getId() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((client == null) ? 0 : client.hashCode());
		result = prime * result + ((createdAt == null) ? 0 : createdAt.hashCode());
		result = prime * result + id;
		result = prime * result + (isValid ? 1231 : 1237);
		result = prime * result + ((orderNature == null) ? 0 : orderNature.hashCode());
		result = prime * result + Float.floatToIntBits(reductionRatio);
		result = prime * result + Float.floatToIntBits(totale);
		result = prime * result + ((updatedAt == null) ? 0 : updatedAt.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClientOrder other = (ClientOrder) obj;
		if (client == null) {
			if (other.client != null)
				return false;
		} else if (!client.equals(other.client))
			return false;
		if (createdAt == null) {
			if (other.createdAt != null)
				return false;
		} else if (!createdAt.equals(other.createdAt))
			return false;
		if (id != other.id)
			return false;
		if (isValid != other.isValid)
			return false;
		if (orderNature == null) {
			if (other.orderNature != null)
				return false;
		} else if (!orderNature.equals(other.orderNature))
			return false;
		if (Float.floatToIntBits(reductionRatio) != Float.floatToIntBits(other.reductionRatio))
			return false;
		if (Float.floatToIntBits(totale) != Float.floatToIntBits(other.totale))
			return false;
		if (updatedAt == null) {
			if (other.updatedAt != null)
				return false;
		} else if (!updatedAt.equals(other.updatedAt))
			return false;
		return true;
	}

}
