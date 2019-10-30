package Entities;

import java.io.Serializable;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import Enums.ProductType;


@Entity
@Table(name = "Product")
@Inheritance(strategy = InheritanceType.JOINED)
public class Product implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "reference")
	private String reference;

	@Column(name = "name")
	private String name;

	@Column(name = "description")
	private String description;

	@Column(name = "type")
	@Enumerated(EnumType.STRING)
	private ProductType type;

	@Column(name = "guarantee")
	private int guarantee;


	@Column(name = "price")
	private double price;
	@JsonIgnore
	@ManyToMany(mappedBy="products",fetch=FetchType.EAGER)
	private List<Pack> packs;
	
	public Product(int id, String reference, String name, String description, ProductType type, int guarantee,
			double price/*  List<TarifProduct> tafifProductList */, Promotion promotion,
			double new_price) {
		super();
		this.id = id;
		this.reference = reference;
		this.name = name;
		this.description = description;
		this.type = type;
		this.guarantee = guarantee;
		this.price = price;
		//this.stockList = stockList;
	//	this.tafifProductList = tafifProductList;
		this.promotion = promotion;
		this.new_price = new_price;
	}

	
	public Product() {
		super();
	}

	public Product(int id, String reference, String name, String description, ProductType type, int guarantee,
			double price,  List<Pack> packs, Promotion promotion, double new_price) {
		super();
		this.id = id;
		this.reference = reference;
		this.name = name;
		this.description = description;
		this.type = type;
		this.guarantee = guarantee;
		this.price = price;
		this.packs = packs;
		this.promotion = promotion;
		this.new_price = new_price;
	}

	public Product(int id, String reference, String name, String description, ProductType type, int guarantee,
			double price ) {
		super();
		this.id = id;
		this.reference = reference;
		this.name = name;
		this.description = description;
		this.type = type;
		this.guarantee = guarantee;
		this.price = price;
	}

	public Product(String reference, String name, String description, ProductType type, int guarantee, double price) {
		super();
		this.reference = reference;
		this.name = name;
		this.description = description;
		this.type = type;
		this.guarantee = guarantee;
		this.price = price;
		
	}


	//@OneToMany(mappedBy = "tariff" ,cascade = {CascadeType.ALL},fetch=FetchType.EAGER)
	//private List<TarifProduct> tafifProductList = new ArrayList<>();
	
	
	
	
	

	@ManyToOne
	private Promotion promotion;

	public List<Pack> getPacks() {
		return packs;
	}


	public void setPacks(List<Pack> packs) {
		this.packs = packs;
	}

	@Column(name = "new_price")
	private double new_price;

	public Promotion getPromotion() {
		return promotion;
	}

	public void setPromotion(Promotion promotion) {
		this.promotion = promotion;
	}

	public double getNew_price() {
		return new_price;
	}

	public void setNew_price(double new_price) {
		this.new_price = new_price;
	}

	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ProductType getType() {
		return type;
	}

	public void setType(ProductType type) {
		this.type = type;
	}

	public int getGuarantee() {
		return guarantee;
	}

	public void setGuarantee(int guarantee) {
		this.guarantee = guarantee;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

//	public List<TarifProduct> getTarifProductList() {
//		return tafifProductList;
//	}

	/*
	 * public void setTarifProductList(List<TarifProduct> tafifProductList) {
	 * this.tafifProductList = tafifProductList; }
	 * 
	 * public List<Stock> getStockList() { return stockList; }
	 */

	/*
	 * public void setStockList(List<Stock> stockList) { this.stockList = stockList;
	 * }
	 * 
	 * public List<TarifProduct> getTafifProductList() { return tafifProductList; }
	 * 
	 * public void setTafifProductList(List<TarifProduct> tafifProductList) {
	 * this.tafifProductList = tafifProductList; }
	 */



	/*
	 * @Override public String toString() { return "Product [id=" + id +
	 * ", reference=" + reference + ", name=" + name + ", description=" +
	 * description + ", type=" + type + ", guarantee=" + guarantee + ", price=" +
	 * price + ", stockList=" + stockList + ", tafifProductList=" + tafifProductList
	 * + "]"; }
	 */

}
