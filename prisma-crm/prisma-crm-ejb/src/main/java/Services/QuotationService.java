package Services;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import Interfaces.IQuotationLocal;
@LocalBean
@Stateless
public class QuotationService implements IQuotationLocal {
	@PersistenceContext(unitName = "prisma-crm-ejb")
	EntityManager manager;
	
	
	
	
	
	
	
}
