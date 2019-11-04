package Entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import Enums.ClientGroups;
import Enums.ClientType;

@Entity
@DiscriminatorValue("client")
@Table(name="client")
public class Client extends User implements Serializable {

	private static final long serialVersionUID = 1L;

	@OneToMany(mappedBy = "client", fetch = FetchType.EAGER)
	private Set<ClientOrder> clientOrders;
	@Enumerated(EnumType.STRING)
	private ClientGroups clientgroup;
	private int fidelityScore;
	@Enumerated(EnumType.STRING)
	private ClientType clientType;
	@Column(name = "entrepriseName", nullable = true, length = 255)
	private String entrepriseName;
	@Column(name = "entreprisePosition", nullable = true, length = 255)
	private String entreprisePosition;

	public Client() {
		super();
		this.clientOrders = new TreeSet<ClientOrder>();
	}

	public Set<ClientOrder> getClientOrders() {
		return clientOrders;
	}

	public void setClientOrders(Set<ClientOrder> clientOrders) {
		this.clientOrders = clientOrders;
	}

	public String getEntrepriseName() {
		return entrepriseName;
	}

	public void setEntrepriseName(String entrepriseName) {
		this.entrepriseName = entrepriseName;
	}

	public String getEntreprisePosition() {
		return entreprisePosition;
	}

	public void setEntreprisePosition(String entreprisePosition) {
		this.entreprisePosition = entreprisePosition;
	}

	public ClientGroups getClientgroup() {
		return clientgroup;
	}

	public void setClientgroup(ClientGroups clientgroup) {
		this.clientgroup = clientgroup;
	}

	public int getFidelityScore() {
		return fidelityScore;
	}

	public void setFidelityScore(int fidelityScore) {
		this.fidelityScore = fidelityScore;
	}

	public ClientType getClientType() {
		return clientType;
	}

	public void setClientType(ClientType clientType) {
		this.clientType = clientType;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Client other = (Client) obj;
		if (clientOrders == null) {
			if (other.clientOrders != null)
				return false;
		} else if (!clientOrders.equals(other.clientOrders))
			return false;
		if (clientType != other.clientType)
			return false;
		if (clientgroup != other.clientgroup)
			return false;
		if (fidelityScore != other.fidelityScore)
			return false;
		return true;
	}

}
