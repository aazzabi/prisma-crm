package Interfaces;

import java.util.List;

import javax.ejb.Remote;

import Entities.Offer;
import Entities.Promotion;

@Remote
public interface IOffer {
	public Offer addOffer(Offer offer);
	public void deleteOffer(int id);
	public Offer findOffer(int id);
	public Offer updateOffer(Offer offer,int id );
	public List<Offer> listeoffer();


}
