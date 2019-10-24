package Services;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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
import Enums.Role;
import Interfaces.IClaimServiceRemote;

@Stateless
@LocalBean
public class ClaimService implements IClaimServiceRemote {
	@PersistenceContext(unitName = "prisma-crm-ejb")
	EntityManager em;

	@Override
	public int addClaim(Claim c) {
		c.toString();
		c.setCreatedBy(em.find(Client.class, 2));
		System.out.print(c.getType());
		c.setResponsable(this.findAnAgentFreeAndActif(c.getType()));
		c.setResolvedBy(null);
		em.persist(c);
		c.setCode("CODE"+c.getId());
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
		if (status == ClaimStatus.RESOLU) {
			//c.getResolvedBy();
			//get agent connecté 
			//agent.setNbrClaims(a.getNbrClaims() + 1);

		}
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
	public List<Claim> getByPrioirty(ClaimPriority priority) {
		TypedQuery<Claim> query = em.createQuery("SELECT c from Claim c where c.priority=:priority", Claim.class).setParameter("priority", priority);
		List<Claim> cf = query.getResultList();
		System.out.println(cf);
		return cf;
	}

	@Override
	public List<Claim> getByStatus(ClaimStatus status) {
		TypedQuery<Claim> query = em.createQuery("SELECT c from Claim c where c.status=:s", Claim.class).setParameter("status", status);
		List<Claim> cf = query.getResultList();
		System.out.println(cf);
		return cf;
	}

	@Override
	public List<Claim> getByType(ClaimType type) {
		TypedQuery<Claim> query = em.createQuery("SELECT c from Claim c where c.type=:type", Claim.class).setParameter("type", type);
		List<Claim> cf = query.getResultList();
		System.out.println(cf);
		return cf;
	}


	@Override
	public Agent findAnAgentFreeAndActif(ClaimType t) {
		Agent a;
		Role type = null;
		
		if (t==ClaimType.FINANCIERE) { type = Role.financial;}
		else if (t==ClaimType.TECHNIQUE) { type = Role.technical;}
		else if (t==ClaimType.RELATIONNELLE) { type = Role.relational;}
		
		String qlString = "SELECT a from Agent a where a.roleAgent=:t and a.dispoClaim=:d ORDER BY a.nbrClaims";
		Query query = em.createQuery(qlString, Agent.class)
				.setParameter("d","disponible")					
				.setParameter("t", type);
		List<Agent> agents = query.getResultList();
		System.out.println("160 - CLAIM TYPE "+t);
	
		if (agents.size() == 1) {
			a = (Agent)query.getResultList().get(0);
			a.setDispoClaim("indisponible");
			System.out.println(a.getRoleAgent());
		} else { // on prend l'agent ayant le moins de Réclamation traités
			TypedQuery<Agent> query2 = em.createQuery("SELECT a from Agent a WHERE a.roleAgent=:t ORDER BY nbrClaims DESC", Agent.class)
					.setParameter("t", type);
			a = query2.getResultList().get(0);
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
