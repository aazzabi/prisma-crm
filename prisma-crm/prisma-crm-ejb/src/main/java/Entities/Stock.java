package Entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "Stock")
public class Stock  implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne
	private Store store;
	
	private String productRef;
	
	private int quantity;
	
	private int quantityMin;
	
	private int recentQuantity;
	
	@JsonFormat(pattern = "dd-MM-yyyy")
	private Date createdAt = new Date(System.currentTimeMillis());

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public String getProductRef() {
		return productRef;
	}

	public void setProductRef(String productRef) {
		this.productRef = productRef;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getQuantityMin() {
		return quantityMin;
	}

	public void setQuantityMin(int quantityMin) {
		this.quantityMin = quantityMin;
	}

	public int getRecentQuantity() {
		return recentQuantity;
	}

	public void setRecentQuantity(int recentQuantity) {
		this.recentQuantity = recentQuantity;
	}
}
