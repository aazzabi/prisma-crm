package Entities;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class ClientCart implements Serializable {


	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private int id;
	private boolean isCheckedOut;
	private Timestamp createdAt;
	private Timestamp updatedAt;
	@JsonIgnore
	@ManyToOne
	private Client client;
	@JsonIgnore
	@OneToOne
	private ClientOrder clientOrder;
	public ClientOrder getOrder() {
		return clientOrder;
	}
	public void setOrder(ClientOrder order) {
		this.clientOrder = order;
	}
	@JsonIgnore
	@OneToMany(mappedBy="cart",fetch=FetchType.EAGER)
	private Set<CartProductRow> cartRows;
	
	public Set<CartProductRow> getCartRows() {
		return cartRows;
	}
	public void setCartRows(Set<CartProductRow> cartRows) {
		this.cartRows = cartRows;
	}
	public boolean isCheckedOut() {
		return isCheckedOut;
	}
	public void setCheckedOut(boolean isCheckedOut) {
		this.isCheckedOut = isCheckedOut;
	}
	public Timestamp getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}
	public Timestamp getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
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
	
	public void addProductToCart(CartProductRow row)
	{
		this.cartRows.add(row);
	}
	
	
	
	
	

}
