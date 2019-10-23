package Entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import Enums.AccountState;
import Enums.Role;
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorColumn(name="userType")
@Table(name="user")
@Entity
public class User implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private int id;
	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private Date createdAt;
	private String phoneNumber;
	private String confirmationToken;
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastAuthentificated;
	@Temporal(TemporalType.TIMESTAMP)
	private Date passwordLastChanged;
	private AccountState accountState;
	@ManyToOne
	private Address address;
	private String profileImage;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getConfirmationToken() {
		return confirmationToken;
	}

	public void setConfirmationToken(String confirmationToken) {
		this.confirmationToken = confirmationToken;
	}

	public String getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}
 
	
	public AccountState getAccountState() {
		return accountState;
	}

	public void setAccountState(AccountState accountState) {
		this.accountState = accountState;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public int getId() {
		return id;
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


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + id;
		result = prime * result + ((lastAuthentificated == null) ? 0 : lastAuthentificated.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((passwordLastChanged == null) ? 0 : passwordLastChanged.hashCode());
		result = prime * result + ((phoneNumber == null) ? 0 : phoneNumber.hashCode());
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
		return true;
	}

	public User() {
	}

	public User(int id, String firstName, String lastName, String email, String password, Date createdAt,
			String phoneNumber, String confirmationToken, Date lastAuthentificated, Date passwordLastChanged,
			AccountState accountState, Address address, String profileImage) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.createdAt = createdAt;
		this.phoneNumber = phoneNumber;
		this.confirmationToken = confirmationToken;
		this.lastAuthentificated = lastAuthentificated;
		this.passwordLastChanged = passwordLastChanged;
		this.accountState = accountState;
		this.address = address;
		this.profileImage = profileImage;
	}

	public User(int id, String firstName, String lastName,String profileImage,
			String phoneNumber, String email, String password, Date createdAt, AccountState accountState, String confirmationToken) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.createdAt = createdAt;
		this.phoneNumber = phoneNumber;
		this.confirmationToken = confirmationToken;
		this.accountState = accountState;
		this.profileImage = profileImage;

	}

	public User(int id, String confirmationToken) {
		super();
		this.id = id;
		this.confirmationToken = confirmationToken;
	}
	
	



}