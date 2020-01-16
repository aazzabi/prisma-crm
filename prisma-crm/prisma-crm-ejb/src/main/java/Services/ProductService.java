package Services;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import Entities.Agent;
import Entities.MyImage;
import Entities.Product;
import Entities.Stock;
import Entities.Store;
import Entities.TarifProduct;
import Entities.Tariff;
import Entities.User;
import Interfaces.IProductServiceLocal;
import Interfaces.IProductServiceRemote;

@Stateless
public class ProductService implements IProductServiceLocal, IProductServiceRemote {
	@PersistenceContext(unitName = "prisma-crm-ejb")
	EntityManager em;

	@Override
	public Product addProduct(Product p) {
		// System.out.println(UserService.UserLogged.getFirstName());
		User u=em.find(User.class, 6);
		p.setAgent(u);
		em.persist(p);
		return p;
	}

	@Override
	public void removeProduct(int id) {
		Product p =em.find(Product.class, id);
		p.setAgent(null);
		em.remove(p);
	}

	@Override
	public Product updateProduct(Product newProduct) {

		Product p = em.find(Product.class, newProduct.getId());
		p.setName(newProduct.getName());
		p.setReference(newProduct.getReference());
		p.setDescription(newProduct.getDescription());
		p.setType(newProduct.getType());
		p.setGuarantee(newProduct.getGuarantee());
		p.setPrice(newProduct.getPrice());
		p.setBrand(newProduct.getBrand());
		p.setCamera(newProduct.getCamera());
		p.setImageUrl(newProduct.getImageUrl());
		p.setMemory(newProduct.getMemory());
		p.setResolution(newProduct.getResolution());
		
		
		System.out.print("newProduct "+newProduct);
	
		
		
		return p;

	}

	@Override
	public Product findProductById(int id) {
		Product p = em.find(Product.class, id);
		return p;
		
	}
	@Override
	public List<Product> findProductsByStore(Store s ) {
		TypedQuery<Stock> query = em.createQuery(
				"SELECT s FROM Stock s WHERE s.store = :store", Stock.class);

		List<Product> products= new ArrayList<>();
		for(Stock st:query.setParameter("store", s).getResultList()) {
			products.add(st.getProduct());
		}
		return products;
	}


	

	@Override
	public List<Product> findProductsByReference(String ref) {
		
		TypedQuery<Product> query = em.createQuery(
				"SELECT c FROM Product c WHERE c.reference = :ref", Product.class);

		return query.setParameter("ref", ref).getResultList();
	}

	@Override
	public List<Product> findAllProducts() {
		List<Product> products = em.createQuery("from Product", Product.class).getResultList();
		return products;
	}

	@Override
	public Tariff addTarif(Tariff t) {
		em.persist(t);
		return t;
	}

	@Override
	public void removeTarif(int id) {
		em.remove(em.find(Tariff.class, id));

	}

	@Override
	public Tariff updateTarif(Tariff newTarif) {
		Tariff t = em.find(Tariff.class, newTarif.getId());
		t.setCnxSpeed(newTarif.getCnxSpeed());
		t.setPriceT(newTarif.getPriceT());
		return t;

	}

	@Override
	public Tariff findTarifById(int id) {
		Tariff t = em.find(Tariff.class, id);
		return t;
	}

	@Override
	public List<Tariff> findAllTarifs() {
		List<Tariff> tarifs = em.createQuery("from Tariff", Tariff.class).getResultList();
		return tarifs;
	}


	@Override
	public void assignTarifToProduct(int idProduct, int idTarif) {
		Product p = findProductById(idProduct);
		Tariff t = findTarifById(idTarif);
		p.getTarifs().add(t);
		System.out.println("hello "+ p.getTarifs());
		/*TarifProduct tp = new TarifProduct();
		tp.setProduct(p);
		tp.setTariff(t);
		em.persist(tp);*/
	}

	@Override
	public MyImage uploadImage(String urlFile) {
		File file = new File("C:/uploads/"+urlFile);
		System.out.println("service= "+ file);

		String imageData = "";
		DateFormat dateformatImage;
		String location, format;

        try {

            BufferedImage imag = ImageIO.read(file);

            dateformatImage = new SimpleDateFormat("yyyyMMddHHmmssSSS");

            imageData = dateformatImage.format(new Date()) + file.getName();
            System.out.println("file name= "+ file.getName());

            /*location = "C:/AngularWorkspace/angular/src/assets/img/prods/" + imageData;*/
            
            location = "C:/AngularWorkspace/angular/src/assets/img/prods/" + imageData;

            format = file.getName().substring(file.getName().lastIndexOf(".") + 1, file.getName().length());
            ImageIO.write(imag, format, new File(location));

        } catch (IOException ex) {
            //Logger.getLogger(AjouterEventController.class.getName()).log(Level.SEVERE, null, ex);
        	System.out.println("img error");
        }

        System.out.println("imageData= "+ imageData);
        MyImage imggg = new MyImage(imageData);
        return imggg;
		
	}
	




}
