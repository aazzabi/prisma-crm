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
@Table(name="cartproductrow")
@IdClass(CartProductRowId.class)
public class CartProductRow implements Serializable {
		@Id
	    @ManyToOne
	    @JoinColumn(name = "product_id", referencedColumnName = "id")
		private Product product;
		
		@Id
	    @ManyToOne
	    @JoinColumn(name = "cart_id", referencedColumnName = "id")
		private ClientCart cart;
		
		@Column(name="desiredQuantity")
		private int quantity;

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
		
}
