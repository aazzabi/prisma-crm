package Entities.foreignid;

import java.io.Serializable;

public class CartProductRowId implements Serializable {
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
private int product;
private int cart;


public int getProduct() {
	return product;
}
public void setProduct(int product) {
	this.product = product;
}
public int getCart() {
	return cart;
}
public void setCart(int cart) {
	this.cart = cart;
}
@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + cart;
	result = prime * result + product;
	return result;
}
@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (getClass() != obj.getClass())
		return false;
	CartProductRowId other = (CartProductRowId) obj;
	if (cart != other.cart)
		return false;
	if (product != other.product)
		return false;
	return true;
}


}
