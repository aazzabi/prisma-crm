package Interfaces;

import java.util.List;

import javax.ejb.Remote;

import Entities.Pack;
import Entities.Product;
import Entities.Promotion;

@Remote
public interface IPack {
	
	public Pack addpack(Pack pack);
	public void deletePack(int id);
	public Pack findpack(int id);
	public Pack updatepack(Pack pack);
	public void addproductpack(int idp,int idpa);
	public List<Product> getAllProductPerPack(int id) ;
	public void deleteproductpack(int idp,int idpa);
	public List<Pack> listepackss();

}
