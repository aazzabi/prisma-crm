package Entities;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;

import Enums.PeriodType;

@Entity
@DiscriminatorValue("Promotion")
@Table(name = "promotion")
public class Promotion implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Temporal(TemporalType.DATE)
	private Date s_date;

	@Column(name = "percentage")
	private int percentage;
	@Temporal(TemporalType.DATE)
	private Date e_date;

	@Enumerated(EnumType.STRING)
	private PeriodType period;

	@JsonIgnore
	@OneToMany(mappedBy = "promotion", cascade = { CascadeType.PERSIST }, fetch = FetchType.EAGER)
	private List<Product> products = new ArrayList<>();

	public Promotion() {
		super();

	}

	public Promotion(int id, Date s_date, Date e_date, PeriodType period, int percentage) {
		super();
		this.id = id;
		this.s_date = s_date;
		this.e_date = e_date;
		this.period = period;
		this.percentage = percentage;
	}

	public int getId() {
		return id;
	}

	public Promotion(int id, Date s_date, Date e_date, PeriodType period, int percentage, List<Product> products) {
		super();
		this.id = id;
		this.s_date = s_date;
		this.e_date = e_date;
		this.period = period;
		this.percentage = percentage;
		this.products = products;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	public PeriodType getPeriod() {
		return period;
	}

	public void setPeriod(PeriodType period) {
		this.period = period;
	}

	public int getPercentage() {
		return percentage;
	}

	public void setPercentage(int percentage) {
		this.percentage = percentage;
	}

	public Date getS_date() {
		return s_date;
	}

	public void setS_date(Date s_date) {
		this.s_date = s_date;
	}

	public Date getE_date() {
		return e_date;
	}

	public void setE_date(Date e_date) {
		this.e_date = e_date;
	}

}
