package Entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import Enums.ProductType;
@Entity
@Table(name="reductionRatio")
public class ReductionFidelityRation implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private int id;
	private String labelRatio;
	private int fidelityScoreForEach;
	private float reductionRatio;
	@Enumerated(EnumType.STRING)	
	private ProductType productType;
	public String getLabelRatio() {
		return labelRatio;
	}
	public void setLabelRatio(String labelRatio) {
		this.labelRatio = labelRatio;
	}
	public int getFidelityScoreForEach() {
		return fidelityScoreForEach;
	}
	public void setFidelityScoreForEach(int fidelityScoreForEach) {
		this.fidelityScoreForEach = fidelityScoreForEach;
	}
	public float getReductionRatio() {
		return reductionRatio;
	}
	public void setReductionRatio(float reductionRatio) {
		this.reductionRatio = reductionRatio;
	}
	public ProductType getProductType() {
		return productType;
	}
	public void setProductType(ProductType productType) {
		this.productType = productType;
	}
	public int getId() {
		return id;
	}
	
	
	
}
