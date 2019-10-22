package Entities;

import java.io.Serializable;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.Destroyed;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import Enums.PeriodType;

@Entity
@DiscriminatorValue("Promotion")
@Table(name = "promotion")
public class Promotion implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column
	private Date s_date;

	@Column
	private Date e_date;
	
	@Enumerated(EnumType.STRING)
	private PeriodType period;

	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	@Column(name = "new_price")
	private double new_price;

	@Column(name = "percentage")
	private int percentage;

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public List<Product> getListP() {
		return listP;
	}

	public void setListP(List<Product> listP) {
		this.listP = listP;
	}

	

	@ManyToOne(cascade = CascadeType.ALL)
	Agent agent;

	@OneToMany
	List<Product> listP;
	
	

	public PeriodType getPeriod() {
		return period;
	}

	public void setPeriod(PeriodType period) {
		this.period = period;
	}

	public double getNew_price() {
		return new_price;
	}

	public void setNew_price(double new_price) {
		this.new_price = new_price;
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
