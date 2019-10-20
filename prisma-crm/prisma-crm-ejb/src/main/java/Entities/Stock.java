package Entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Stock")
public class Stock implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "quantity")
	private int quantity;


	@ManyToOne
	@JoinColumn(name="product_id", referencedColumnName="id", insertable=false, updatable=false)
	private Product product;


	@ManyToOne
	@JoinColumn(name="store_id", referencedColumnName="id", insertable=false, updatable=false)
	private Store store;


	public Stock() {
		super();
	}

	public Stock(int id, Product product, Store store, int quantity) {
		super();
		this.id = id;
		this.product = product;
		this.store = store;
		this.quantity = quantity;
	}

	public Stock(Product product, Store store, int quantity) {
		super();
		this.product = product;
		this.store = store;
		this.quantity = quantity;
	}

	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	} 

	public Store getStore() {
		return store;
	}
	public void setStore(Store store) {
		this.store = store;
	}



	



}
