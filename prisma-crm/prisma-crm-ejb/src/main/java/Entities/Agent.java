package Entities;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import Enums.*;

@Entity
@DiscriminatorValue("Agent")
@Table(name = "agent")
public class Agent extends User implements Serializable {
	@Enumerated(EnumType.STRING)
	private Role roleAgent;
	private String contractType;
	private Date startDate;
	private Date endDate;
	private double salary;
	
	@Column
	private int nbrClaimsOpenedAndResolved;
	private int nbrClaimsOpened;
	private int nbrClaimsResolved;
	private long moyAssiduite;
	private long moyReponse;
	@Column 
	private String dispoClaim;

	public Role getRoleAgent() {
		return roleAgent;
	}

	public void setRoleAgent(Role roleAgent) {
		this.roleAgent = roleAgent;
	}

	public String getContractType() {
		return contractType;
	}

	public void setContractType(String contractType) {
		this.contractType = contractType;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}


	
	public int getNbrClaimsOpenedAndResolved() {
		return nbrClaimsOpenedAndResolved;
	}

	public void setNbrClaimsOpenedAndResolved(int nbrClaimsOpenedAndResolved) {
		this.nbrClaimsOpenedAndResolved = nbrClaimsOpenedAndResolved;
	}

	public String getDispoClaim() {
		return dispoClaim;
	}

	public void setDispoClaim(String dispoClaim) {
		this.dispoClaim = dispoClaim;
	}
	
	

	public int getNbrClaimsOpened() {
		return nbrClaimsOpened;
	}

	public void setNbrClaimsOpened(int nbrClaimsOpened) {
		this.nbrClaimsOpened = nbrClaimsOpened;
	}

	public int getNbrClaimsResolved() {
		return nbrClaimsResolved;
	}

	public void setNbrClaimsResolved(int nbrClaimsResolved) {
		this.nbrClaimsResolved = nbrClaimsResolved;
	}

	public long getMoyAssiduite() {
		return moyAssiduite;
	}

	public void setMoyAssiduite(long moyAssiduite) {
		this.moyAssiduite = moyAssiduite;
	}

	public long getMoyReponse() {
		return moyReponse;
	}

	public void setMoyReponse(long moyReponse) {
		this.moyReponse = moyReponse;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((contractType == null) ? 0 : contractType.hashCode());
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result + ((roleAgent == null) ? 0 : roleAgent.hashCode());
		long temp;
		temp = Double.doubleToLongBits(salary);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Agent other = (Agent) obj;
		if (contractType == null) {
			if (other.contractType != null)
				return false;
		} else if (!contractType.equals(other.contractType))
			return false;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
		if (roleAgent != other.roleAgent)
			return false;
		if (Double.doubleToLongBits(salary) != Double.doubleToLongBits(other.salary))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		return true;
	}

}
