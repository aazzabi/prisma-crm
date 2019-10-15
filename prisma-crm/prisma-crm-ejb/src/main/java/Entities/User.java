package Entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import Enums.Role;

@Entity
public class User implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private int id;
	private String email;
	private String password;
	private String address;
	private String phoneNumber;
	@Enumerated(EnumType.STRING)
	private  Role role ;
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastAuthentificated;
	@Temporal(TemporalType.TIMESTAMP)
	private Date passwordLastChanged;

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Date getLastAuthentificated() {
		return lastAuthentificated;
	}

	public void setLastAuthentificated(Date lastAuthentificated) {
		this.lastAuthentificated = lastAuthentificated;
	}

	public Date getPasswordLastChanged() {
		return passwordLastChanged;
	}

	public void setPasswordLastChanged(Date passwordLastChanged) {
		this.passwordLastChanged = passwordLastChanged;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + id;
		result = prime * result + ((lastAuthentificated == null) ? 0 : lastAuthentificated.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((passwordLastChanged == null) ? 0 : passwordLastChanged.hashCode());
		result = prime * result + ((phoneNumber == null) ? 0 : phoneNumber.hashCode());
		result = prime * result + ((role == null) ? 0 : role.hashCode());
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
		User other = (User) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (id != other.id)
			return false;
		if (lastAuthentificated == null) {
			if (other.lastAuthentificated != null)
				return false;
		} else if (!lastAuthentificated.equals(other.lastAuthentificated))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (passwordLastChanged == null) {
			if (other.passwordLastChanged != null)
				return false;
		} else if (!passwordLastChanged.equals(other.passwordLastChanged))
			return false;
		if (phoneNumber == null) {
			if (other.phoneNumber != null)
				return false;
		} else if (!phoneNumber.equals(other.phoneNumber))
			return false;
		if (role != other.role)
			return false;
		return true;
	}



}