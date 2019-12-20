package Services;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import Entities.Address;
import Interfaces.IAdressLocal;
import Interfaces.IAdressRemote;

@LocalBean
@Stateless
public class AddressService implements IAdressLocal,IAdressRemote {
	
	private static final String OPEN_STREET_MAP_API_URI="";

	@Override
	public Address createAdress(String display_name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Address updateAdress(Address address) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String deleteAdress(Address address) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Address> fetchAddresses() {
		// TODO Auto-generated method stub
		return null;
	}

}
