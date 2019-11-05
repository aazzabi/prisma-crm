package Entities;

<<<<<<< prisma-crm/prisma-crm-ejb/src/main/java/Entities/Quotation.java
import java.io.Serializable;

import java.sql.Date;
=======
import java.util.Date;
>>>>>>> prisma-crm/prisma-crm-ejb/src/main/java/Entities/Quotation.java

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
<<<<<<< prisma-crm/prisma-crm-ejb/src/main/java/Entities/Quotation.java
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

=======
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Quotation {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private int id;	
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;
	
>>>>>>> prisma-crm/prisma-crm-ejb/src/main/java/Entities/Quotation.java
}
