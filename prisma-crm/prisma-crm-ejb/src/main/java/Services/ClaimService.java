package Services;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
/*
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
*/
import Entities.Agent;
import Entities.Claim;
import Entities.Client;
import Entities.NoteClaim;
import Enums.ClaimPriority;
import Enums.ClaimStatus;
import Enums.ClaimType;
import Interfaces.IClaimServiceLocal;
import Interfaces.IClaimServiceRemote;

@Stateless
@LocalBean
public class ClaimService implements IClaimServiceLocal, IClaimServiceRemote {
	@PersistenceContext(unitName = "prisma-crm-ejb")
	EntityManager em;

	@Override
	public int addClaim(Claim c) {
		c.toString();
		c.setCreatedBy(em.find(Client.class, 2));
		c.setResponsable(this.findAnAgentFreeAndActif());
		c.setResolvedBy(null);
		em.persist(c);
		return c.getId();
	}

	@Override
	public List<Claim> getAll() {
		TypedQuery<Claim> query = em.createQuery("SELECT c from Claim c", Claim.class);
		List<Claim> cf = query.getResultList();
		return cf;
	}

	@Override
	public Claim getById(int id) {
		Claim c = (Claim) em.find(Claim.class, id);
		return c;
	}

	@Override
	public Claim getByCode(String code) {
		TypedQuery<Claim> query = em.createQuery("SELECT c from Claim c where c.code=:cd ORDER BY c.id", Claim.class)
				.setParameter("cd", code);
		Claim c = query.getResultList().get(0);
		System.out.println(c);
		return c;
	}

	@Override
	public void changeStatus(Claim c, ClaimStatus status) {
		c.setStatus(status);
		em.merge(c);
	}

	@Override
	public void changePriority(Claim c, ClaimPriority priority) {
		c.setPriority(priority);
	}

	@Override
	public void deleteClaim(Claim c) {
		em.remove(c);
	}

	@Override
	public int deleteClaimById(int id) {
		Claim c = em.find(Claim.class, id);
		int i = em.createQuery("delete from Claim c where c.id= :identifiant").setParameter("identifiant", id)
				.executeUpdate();
		System.out.println("CLAIMSERVICE DEL !");
		return i;
	}

	@Override
	public void deleteClaimByClient(Client c) {
		em.remove(em.find(Claim.class, c));
	}

	@Override
	public void deleteNotesByClaimId(int id) {
		List<NoteClaim> noteClaims = this.getNotesByClaimId(id);
		if (noteClaims.size() != 0) {
			for (NoteClaim nc : noteClaims) {
				em.remove(nc);
			}
		}
	}

	@Override
	public List<Claim> getClaimsByClient(Client c) {
		TypedQuery<Claim> query = em.createQuery("SELECT c from Claim c where c.createdBy=:c", Claim.class);
		List<Claim> cf = query.getResultList();
		System.out.println(cf);
		return cf;
	}

	@Override
	public List<Claim> getClaimsByResponsable(Agent a) {
		TypedQuery<Claim> query = em.createQuery("SELECT c from Claim c where c.responsable=:a", Claim.class);
		List<Claim> cf = query.getResultList();
		System.out.println(cf);
		return cf;
	}

	@Override
	public List<Claim> getClaimsResolvedBy(Agent a) {
		TypedQuery<Claim> query = em.createQuery("SELECT c from Claim c where c.resolvedBy=:a", Claim.class);
		List<Claim> cf = query.getResultList();
		System.out.println(cf);
		return cf;
	}

	@Override
	public List<Claim> getByPrioirty(ClaimPriority p) {
		TypedQuery<Claim> query = em.createQuery("SELECT c from Claim c where c.priority=:a", Claim.class);
		List<Claim> cf = query.getResultList();
		System.out.println(cf);
		return cf;
	}

	@Override
	public List<Claim> getByStatus(ClaimStatus s) {
		TypedQuery<Claim> query = em.createQuery("SELECT c from Claim c where c.status=:s", Claim.class);
		List<Claim> cf = query.getResultList();
		System.out.println(cf);
		return cf;
	}

	@Override
	public List<Claim> getByType(ClaimType type) {
		TypedQuery<Claim> query = em.createQuery("SELECT c from Claim c where c.type=:type", Claim.class);
		List<Claim> cf = query.getResultList();
		System.out.println(cf);
		return cf;
	}

	@Override
	public List<NoteClaim> getNotesByClaimId(int id) {
		Claim c = em.find(Claim.class, id);
		TypedQuery<NoteClaim> query = em.createQuery("SELECT n from NoteClaim n where n.claim=:claim", NoteClaim.class)
				.setParameter("claim", c);
		List<NoteClaim> cf = query.getResultList();
		System.out.println(cf);
		return cf;
	}

	// ne9set'ha ayant la meme specialité que le domaine de la reclamation
	@Override
	public Agent findAnAgentFreeAndActif() {
		Agent a;
		TypedQuery<Agent> queryDispo = em
				.createQuery("SELECT a from Agent a where a.dispoClaim =:d ORDER BY a.nbrClaims", Agent.class)
				.setParameter("d", "disponible");
		if (queryDispo.getSingleResult() != null) {
			a = queryDispo.getSingleResult();
			a.setNbrClaims(a.getNbrClaims() + 1);
			a.setDispoClaim("indisponible");
		} else { // on prend l'agent ayant le moins de Réclamation traités
			TypedQuery<Agent> query = em.createQuery("SELECT a.* from Agent a ORDER BY nbrClaims DESC", Agent.class)
					.setParameter("d", "disponible");
			a = query.getSingleResult();
			a.setNbrClaims(a.getNbrClaims() + 1);
			a.setDispoClaim("indisponible");
		}
		return a;
	}

	@Override
	public void affectClaimToAgent(Claim c, Agent a) {
		c.setResponsable(a);
		a.setNbrClaims(a.getNbrClaims() + 1);
		a.setDispoClaim("indisponible");
	}


	@Override
	public Object merge(Object o) {
		 em.merge(o);
		 return o ;
	}
}
