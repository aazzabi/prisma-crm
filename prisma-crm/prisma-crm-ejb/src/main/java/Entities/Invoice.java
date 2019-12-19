package Entities;

import java.io.Serializable;

import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "invoices")
public class Invoice implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private int id;

	private Timestamp createdAt;
	private Timestamp updatedAt;
	@OneToOne
	private ClientOrder orderInvoice;
	@OneToMany	
	@JsonIgnore
	private Set<CurrencyUnit> currencies;
	@OneToMany
	@JsonIgnore
	private Set<Product> products;

	public ClientOrder getOrderInvoice() {
		return orderInvoice;
	}

	public void setOrderInvoice(ClientOrder orderInvoice) {
		this.orderInvoice = orderInvoice;
	}

	public Set<Product> getProducts() {
		return products;
	}

	public void setProducts(Set<Product> products) {
		this.products = products;
	}
	
	public void addProduct(Product p)
	{
		this.products.add(p);
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

	public ClientOrder getOrder() {
		return orderInvoice;
	}

	public void setOrder(ClientOrder orderInvoice) {
		this.orderInvoice = orderInvoice;
	}

	public Set<CurrencyUnit> getCurrencies() {
		return currencies;
	}

	public void setCurrencies(Set<CurrencyUnit> currencies) {
		this.currencies = currencies;
	}

	public int getId() {
		return id;
	}

}
