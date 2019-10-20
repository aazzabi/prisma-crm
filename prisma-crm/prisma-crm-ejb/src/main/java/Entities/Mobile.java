package Entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "Mobile")
@PrimaryKeyJoinColumn(name = "id")
public class Mobile extends Product implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Column(name = "brand")
	private String brand;
	
	@Column(name = "memory")
	private String memory;
	
	@Column(name = "resolution")
	private String resolution;
	
	@Column(name = "ram")
	private String ram;
	
	@Column(name = "system")
	private String system;
	
	@Column(name = "camera")
	private String camera;
	
	@Column(name = "imageUrl")
	private String imageUrl;
	
	
	

	public Mobile() {
		super();
	}
	

	public Mobile(String brand, String memory, String resolution, String ram, String system,
			String camera, String imageUrl) {
		super();
		this.brand = brand;
		this.memory = memory;
		this.resolution = resolution;
		this.ram = ram;
		this.system = system;
		this.camera = camera;
		this.imageUrl = imageUrl;
	}


	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}


	public String getMemory() {
		return memory;
	}

	public void setMemory(String memory) {
		this.memory = memory;
	}

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	public String getRam() {
		return ram;
	}

	public void setRAM(String ram) {
		this.ram = ram;
	}

	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public String getCamera() {
		return camera;
	}

	public void setCamera(String camera) {
		this.camera = camera;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "Mobile [brand=" + brand  + ", memory=" + memory + ", resolution=" + resolution
				+ ", RAM=" + ram + ", system=" + system + ", camera=" + camera + ", imageUrl=" + imageUrl + "]";
	}
	
	
	
	
	
	
	
	

}
