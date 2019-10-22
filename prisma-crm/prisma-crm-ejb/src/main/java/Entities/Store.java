package Entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Store")
public class Store implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "addresStr")
	private String addresStr;
	
	@Column(name = "telephone")
	private String telephone;
	
	@ManyToOne
	private Address address;
	
	@OneToMany(mappedBy="store",fetch=FetchType.EAGER)
	private Set<Stock> stockList ;
	
	@OneToMany(mappedBy="store",fetch=FetchType.EAGER)
	private Set<StoreHours> storeHoursList;


	public Store() {
		super();
	}


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddresStr() {
		return addresStr;
	}

	public void setAddresStr(String addresStr) {
		this.addresStr = addresStr;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public Set<Stock> getStockList() {
		return stockList;
	}

	public void setStockList(Set<Stock> stockList) {
		this.stockList = stockList;
	}

	public Set<StoreHours> getStoreHoursList() {
		return storeHoursList;
	}

	public void setStoreHoursList(Set<StoreHours> storeHoursList) {
		this.storeHoursList = storeHoursList;
	}


	
	
}
