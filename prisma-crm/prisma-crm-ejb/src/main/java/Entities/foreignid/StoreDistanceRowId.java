package Entities.foreignid;

import java.io.Serializable;

public class StoreDistanceRowId implements Serializable {
private int storeA;
private int storeB;
public int getStoreA() {
	return storeA;
}
public void setStoreA(int storeA) {
	this.storeA = storeA;
}
public int getStoreB() {
	return storeB;
}
public void setStoreB(int storeB) {
	this.storeB = storeB;
}
@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + storeA;
	result = prime * result + storeB;
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
	StoreDistanceRowId other = (StoreDistanceRowId) obj;
	if (storeA != other.storeA)
		return false;
	if (storeB != other.storeB)
		return false;
	return true;
}



}
