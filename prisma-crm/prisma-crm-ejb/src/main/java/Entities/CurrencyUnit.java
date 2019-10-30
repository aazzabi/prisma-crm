package Entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="currencyValues")
public class CurrencyUnit implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private int id;
	private String currencyLabel;
	private double currencyValue;
	public String getCurrencyLabel() {
		return currencyLabel;
	}
	public void setCurrencyLabel(String currencyLabel) {
		this.currencyLabel = currencyLabel;
	}
	public double getCurrencyValue() {
		return currencyValue;
	}
	public void setCurrencyValue(double currencyValue) {
		this.currencyValue = currencyValue;
	}
	public int getId() {
		return id;
	}
	
}
