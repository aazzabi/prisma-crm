package Services;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import Entities.Claim;
import Entities.NoteClaim;
import Interfaces.IClaimServiceRemote;
import Interfaces.INoteClaimRemote;


@Stateless
@LocalBean
public class NoteClaimService implements INoteClaimRemote {
	
	@PersistenceContext(unitName = "prisma-crm-ejb")
	EntityManager em;
	IClaimServiceRemote cs;


	@Override
	public List<NoteClaim> getAll() {
		TypedQuery<NoteClaim> query = em
				.createQuery("SELECT n from NoteClaim n ", NoteClaim.class);
		List<NoteClaim> cf = query.getResultList();
		System.out.println(cf);
		return cf;
	}
	
	@Override
	public NoteClaim addNoteClaim(NoteClaim nc , Claim c) {
		nc.setClaim(c);
		//*************************
		//******** Ne9saa *********
		//*************************
		//nc.setCreatedBy(user.connecté);
		em.persist(nc);
		return nc;
	}
	
	@Override
	public void deleteNotesByClaimId(int id) {
		Claim c = em.find(Claim.class, id);
		List<NoteClaim> noteClaims = this.getNotesByClaimId(id);
		if (noteClaims.size() != 0) {
			for (NoteClaim nc : noteClaims) {
				em.remove(nc);
			}
		}
	}

	@Override
	public List<NoteClaim> getNotesByClaimId(int id) {
		//TypedQuery<NoteClaim> query = em.createQuery("SELECT n.* from NoteClaim n , Claim c where n.claim_id= c.id AND c =: claim ", NoteClaim.class)
		//TypedQuery<NoteClaim> query = em.createQuery("SELECT * from NoteClaim  Where claim = :c ", NoteClaim.class).setParameter("c", c);
		Claim c = em.find(Claim.class, id);
		TypedQuery<NoteClaim> query = em
				.createQuery("SELECT n from NoteClaim n where n.claim=:claim", NoteClaim.class)
				.setParameter("claim", c);
		List<NoteClaim> cf = query.getResultList();
		System.out.println(cf);
		return cf;
	}


	@Override
	public NoteClaim getNoteById(int id) {
		NoteClaim nc = em.find(NoteClaim.class,id);
		return nc;
	}
	
	@Override
	public void deleteNoteClaim(NoteClaim nc) {
		em.remove(nc);
	}

	@Override
	public NoteClaim updateNoteClaim(NoteClaim nc) {
		em.merge(nc);
		return nc;
	}

}
