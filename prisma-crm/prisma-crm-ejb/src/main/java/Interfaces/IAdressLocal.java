package Interfaces;

import java.util.List;

import javax.ejb.Local;

import Entities.Address;
@Local
public interface IAdressLocal {
	public Address createAdress(String display_name);

	public Address updateAdress(Address address);

	public String deleteAdress(Address address);

	public List<Address> fetchAddresses();

}
