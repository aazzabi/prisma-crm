package Entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
	
	@OneToMany(mappedBy="store")
	private List<Stock> stockList = new ArrayList<>();
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy="store")
	private List<StoreHours> storeHoursList = new ArrayList<>();


	public Store() {
		super();
	}

	public Store(String name, String addresStr, String telephone) {
		super();
		this.name = name;
		this.addresStr = addresStr;
		this.telephone = telephone;
	}
	
	public Store(int id, String name, String addresStr, String telephone) {
		super();
		this.id = id;
		this.name = name;
		this.addresStr = addresStr;
		this.telephone = telephone;
	}

	public Store(int id, String name, String addresStr, String telephone, Address address) {
		super();
		this.id = id;
		this.name = name;
		this.addresStr = addresStr;
		this.telephone = telephone;
		this.address = address;
	}

	public Store(String name, String addresStr, String telephone, Address address) {
		super();
		this.name = name;
		this.addresStr = addresStr;
		this.telephone = telephone;
		this.address = address;
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

	public List<Stock> getStockList() {
		return stockList;
	}

	public void setStockList(List<Stock> stockList) {
		this.stockList = stockList;
	}

	public List<StoreHours> getStoreHoursList() {
		return storeHoursList;
	}

	public void setStoreHoursList(List<StoreHours> storeHoursList) {
		this.storeHoursList = storeHoursList;
	}


	
	
	
	
	
	
	
}
