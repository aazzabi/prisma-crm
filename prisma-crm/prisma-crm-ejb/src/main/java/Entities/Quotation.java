package Entities;

import java.io.Serializable;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="quotation")
public class Quotation implements Serializable {
private static final long serialVersionUID = 1L;
@Id
@GeneratedValue(strategy=GenerationType.TABLE)
private int id;
private Date createdAt;
private boolean isValid;
@OneToOne(mappedBy="quotation")
private QuotationRequest request;

public Date getCreatedAt() {
	return createdAt;
}
public void setCreatedAt(Date createdAt) {
	this.createdAt = createdAt;
}
public boolean isValid() {
	return isValid;
}
public void setValid(boolean isValid) {
	this.isValid = isValid;
}
public int getId() {
	return id;
}

}
