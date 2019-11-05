package Entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import Entities.foreignid.QuotationRequestProductId;

@Entity
@Table(name="quotationrequestproductrow")
@IdClass(QuotationRequestProductId.class)
public class QuotationRequestProductRow implements Serializable {
	
	private static final long serialVersionUID = 1L;
	@Id
	@ManyToOne
	@JoinColumn(name = "product_id", referencedColumnName = "id")
	private Product product;
	@Id
	@ManyToOne
	@JoinColumn(name = "quotationRequest_id", referencedColumnName = "id")
	private QuotationRequest quotationRequest;
	@Column(name = "desiredQuantity")
	private int quantity;
	@Column(name = "OriginalPrice")
	private double originaUnitlPrice;
	@Column(name="finalPrice")
	private double finalPrice;
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public QuotationRequest getQuotationRequest() {
		return quotationRequest;
	}
	public void setQuotationRequest(QuotationRequest quotationRequest) {
		this.quotationRequest = quotationRequest;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public double getOriginaUnitlPrice() {
		return originaUnitlPrice;
	}
	public void setOriginaUnitlPrice(double originaUnitlPrice) {
		this.originaUnitlPrice = originaUnitlPrice;
	}
	public double getFinalPrice() {
		return finalPrice;
	}
	public void setFinalPrice(double finalPrice) {
		this.finalPrice = finalPrice;
	}

	
}
