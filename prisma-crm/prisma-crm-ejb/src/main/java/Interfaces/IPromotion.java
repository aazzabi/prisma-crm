package Interfaces;

import java.util.List;
import javax.ejb.Remote;
import Entities.Product;
import Entities.Promotion;



@Remote
public interface IPromotion {
	public void addPromotion(Promotion promotion);
	public void deletePromotion(int id);
	public Promotion findPromotion(int id);
	public Promotion updatePromotion(Promotion promotion);
	public void addPromotiontoproduct(int p , int pr);
	public List<Product> listedesProduitpromotion();
	public List<Promotion> listepromotions();
	
}
