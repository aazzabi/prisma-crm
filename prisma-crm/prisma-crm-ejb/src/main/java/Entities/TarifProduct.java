package Entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "TarifProduct")
public class TarifProduct implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	@ManyToOne
	@JoinColumn(name="product_id", referencedColumnName="id")
	private Product product;

	
	@ManyToOne
	@JoinColumn(name="tarif_id", referencedColumnName="id")
	private Tariff tariff;
	
	
	public TarifProduct() {
		super();
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	
	public Tariff getTariff() {
		return tariff;
	}
	public void setTariff(Tariff tariff) {
		this.tariff = tariff;
	}
	
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}

}
