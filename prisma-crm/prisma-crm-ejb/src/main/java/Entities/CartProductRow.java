package Entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import Entities.foreignid.CartProductRowId;

@Entity
@Table(name = "cartproductrow")
@IdClass(CartProductRowId.class)
public class CartProductRow implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@ManyToOne
	@JoinColumn(name = "product_id", referencedColumnName = "id")
	private Product product;

	@Id
	@ManyToOne
	@JoinColumn(name = "cart_id", referencedColumnName = "id")
	private ClientCart cart;

	@Column(name = "desiredQuantity")
	private int quantity;

	@Column(name = "OriginalPrice")
	private double originaUnitlPrice;
	@ManyToOne
	private ReductionFidelityRation reductionRatio;
	@Column(name = "finalPrice")
	private double finalPrice;
	private int usedFidelityPoints;
	private double totalPriceWNReduction;
	


	public double getOriginalPrice() {
		return originaUnitlPrice;
	}

	public void setOriginalPrice(double originalPrice) {
		this.originaUnitlPrice = originalPrice;
	}

	public ReductionFidelityRation getReductionRatio() {
		return reductionRatio;
	}

	public void setReductionRatio(ReductionFidelityRation reductionRatio) {
		if (reductionRatio.getProductType() == this.product.getType()) {
			this.reductionRatio = reductionRatio;
		}
	}

	public double getFinalPrice() {
		return finalPrice;
	}

	public void setFinalPrice(double finalPrice) {
		this.finalPrice = finalPrice;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public ClientCart getCart() {
		return cart;
	}

	public void setCart(ClientCart cart) {
		this.cart = cart;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getUsedFidelityPoints() {
		return usedFidelityPoints;
	}

	public void setUsedFidelityPoints(int usedFidelityPoints) {
		this.usedFidelityPoints = usedFidelityPoints;
	}

	public double getTotalPriceWNReduction() {
		return totalPriceWNReduction;
	}

	public void setTotalPriceWNReduction(double totalPriceWNReduction) {
		this.totalPriceWNReduction = totalPriceWNReduction;
	}

}
