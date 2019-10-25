package Entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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

	@ManyToOne(cascade = CascadeType.ALL)
	Agent agent;


	//@JsonIgnore
	@OneToMany(mappedBy = "product",fetch=FetchType.EAGER)
	private Set<Stock> stockList ;

	//@JsonIgnore
	@OneToMany(mappedBy = "tariff",fetch=FetchType.EAGER)
	private Set<TarifProduct> tafifProductList ;

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

	
	public Set<Stock> getStockList() {
		return stockList;
	}

	public void setStockList(Set<Stock> stockList) {
		this.stockList = stockList;
	}

	public Set<TarifProduct> getTafifProductList() {
		return tafifProductList;
	}

	public void setTafifProductList(Set<TarifProduct> tafifProductList) {
		this.tafifProductList = tafifProductList;
	}

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}


	

}
