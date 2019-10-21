package Entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

public class DriverHistory implements Serializable {
	
	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private int id;
	@ManyToOne
	private Agent driver;
	private Date AssignDate;
	
}
