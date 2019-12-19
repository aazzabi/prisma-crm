package Interfaces;

import java.util.List;

import javax.ejb.Remote;

import Entities.Address;

@Remote
public interface IAdressRemote {
	public Address createAdress(String display_name);

	public Address updateAdress(Address address);

	public String deleteAdress(Address address);

	public List<Address> fetchAddresses();
}
