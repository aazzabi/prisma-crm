package Services;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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
		c.setFirstResponsable(c.getResponsable());
		c.setResolvedBy(null);
		em.persist(c);
		c.setCode("CODE" + c.getId());
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
			this.resolve(c);
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
	public int deletNoteClaimById(int id) {
		int i = em.createQuery("delete from NoteClaim c where c.id=:identifiant").setParameter("identifiant", id)
				.executeUpdate();
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
		TypedQuery<Claim> query = em.createQuery("SELECT c from Claim c where c.responsable=:a", Claim.class).setParameter("a", a);
		List<Claim> cf = query.getResultList();
		System.out.println(cf);
		return cf;
	}
	@Override
	public Agent getResponsableById(int id) {
		Agent a = (Agent) em.find(Agent.class, id);
		return a;
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
		TypedQuery<Claim> query = em.createQuery("SELECT c from Claim c where c.priority=:priority", Claim.class)
				.setParameter("priority", priority);
		List<Claim> cf = query.getResultList();
		System.out.println(cf);
		return cf;
	}

	@Override
	public List<Claim> getByStatus(ClaimStatus status) {
		TypedQuery<Claim> query = em.createQuery("SELECT c from Claim c where c.status=:s", Claim.class)
				.setParameter("status", status);
		List<Claim> cf = query.getResultList();
		System.out.println(cf);
		return cf;
	}

	@Override
	public List<Claim> getByType(ClaimType type) {
		TypedQuery<Claim> query = em.createQuery("SELECT c from Claim c where c.type=:type", Claim.class)
				.setParameter("type", type);
		List<Claim> cf = query.getResultList();
		System.out.println(cf);
		return cf;
	}

	@Override
	public Agent findAnAgentFreeAndActif(ClaimType t) {
		Agent a;
		Role type = null;

		if (t == ClaimType.FINANCIERE) {
			type = Role.financial;
		} else if (t == ClaimType.TECHNIQUE) {
			type = Role.technical;
		} else if (t == ClaimType.RELATIONNELLE) {
			type = Role.relational;
		}

		String qlString = "SELECT a from Agent a where a.roleAgent=:t and a.dispoClaim=:d ORDER BY a.nbrClaims";
		Query query = em.createQuery(qlString, Agent.class).setParameter("d", "disponible").setParameter("t", type);
		List<Agent> agents = query.getResultList();
		System.out.println("160 - CLAIM TYPE " + t);

		if (agents.size() == 1) {
			a = (Agent) query.getResultList().get(0);
			a.setDispoClaim("indisponible");
			System.out.println(a.getRoleAgent());
		} else { // on prend l'agent ayant le moins de Réclamation traités
			TypedQuery<Agent> query2 = em
					.createQuery("SELECT a from Agent a WHERE a.roleAgent=:t ORDER BY nbrClaims DESC", Agent.class)
					.setParameter("t", type);
			a = query2.getResultList().get(0);
		}
		return a;
	}

	// choix d'un nouveau agent , lors de la delegation
	// different que le 1er chosis
	@Override
	public Agent findAnOtherAgentFreeAndActif(Agent old, ClaimType t) {
		Agent a;
		Role type = null;

		if (t == ClaimType.FINANCIERE) {
			type = Role.financial;
		} else if (t == ClaimType.TECHNIQUE) {
			type = Role.technical;
		} else if (t == ClaimType.RELATIONNELLE) {
			type = Role.relational;
		}
		System.out.println(type);

		String qlString = "SELECT a from Agent a WHERE a.roleAgent=:t AND a.id<>:idOld ORDER BY nbrClaimsResolved";
		Query query = em.createQuery(qlString, Agent.class).setParameter("idOld", old.getId()).setParameter("t", type);
		List<Agent> agents = query.getResultList();
		System.out.println(agents);
		a = (Agent) query.getResultList().get(0);
		a.setDispoClaim("indisponible");
		/*
		 * if (agents.size() == 1) { a = (Agent)query.getResultList().get(0);
		 * a.setDispoClaim("indisponible"); System.out.println(a.getRoleAgent()); } else
		 * { // on prend l'agent ayant le moins de Réclamation traités TypedQuery<Agent>
		 * query2 = em.
		 * createQuery("SELECT a from Agent a WHERE a.roleAgent=:t AND a.id<>:idOld ORDER BY nbrClaimsResolved DESC"
		 * , Agent.class) .setParameter("t", type) .setParameter("idOld",old.getId()); a
		 * = query2.getResultList().get(0); }
		 */
		return a;
	}

	@Override
	public void affectClaimToAgent(Claim c, Agent a) {
		c.setResponsable(a);
		a.setDispoClaim("indisponible");
	}

	@Override
	public Object merge(Object o) {
		em.merge(o);
		return o;
	}

	@Override
	public Claim resolve(Claim c) {
		Agent ag = c.getResponsable();
		c.setResolvedBy(ag);
		c.setResolvedAt(new Date());
		// l'agent connecté , c le responsable de la claim.. (normalement)
		ag.setNbrClaimsResolved(ag.getNbrClaimsResolved() + 1);

		// l responsable houw bidou nafs l repsonsable lowel
		if (c.getResponsable().equals(c.getFirstResponsable())) {
			ag.setMoyAssiduite(
					calculMoyTemp(ag.getNbrClaimsOpened(), ag.getMoyAssiduite(), c.getCreatedAt(), c.getResolvedAt()));
			ag.setMoyReponse(
					calculMoyTemp(ag.getNbrClaimsResolved(), ag.getMoyReponse(), c.getOpenedAt(), c.getResolvedAt()));
			ag.setNbrClaimsOpenedAndResolved(ag.getNbrClaimsOpenedAndResolved() + 1);
		} else { // sarét delegation
			ag.setMoyAssiduite(calculMoyTemp(ag.getNbrClaimsOpened(), ag.getMoyAssiduite(), c.getDelegatedAt(),
					c.getResolvedAt()));
			ag.setMoyReponse(
					calculMoyTemp(ag.getNbrClaimsResolved(), ag.getMoyReponse(), c.getOpenedAt(), c.getResolvedAt()));
		}
		em.merge(ag);
		return c;
	}

	@Override
	public Claim open(Claim c) {
		Agent ag = c.getResponsable();
		ag.setNbrClaimsOpened(ag.getNbrClaimsOpened() + 1);
		c.setStatus(ClaimStatus.EN_COURS);
		c.setOpenedAt(new Date());

		// l responsable houwa bidou nafs l repsonsable lowel ( li 7alha zeda )
		if (c.getResponsable().equals(c.getFirstResponsable())) {
			ag.setMoyAssiduite(calculMoyTemp(ag.getNbrClaimsOpened(), ag.getMoyAssiduite(), c.getCreatedAt(), this.getDateNow()));
		} else { // sarét delegation deja
			ag.setMoyAssiduite(calculMoyTemp(ag.getNbrClaimsOpened(), ag.getMoyAssiduite(), c.getDelegatedAt(), this.getDateNow()));
		}
		em.merge(ag);

		return c;
	}

	@Override
	public Claim deleguer(Claim c) {
		Agent oldAg = c.getResponsable();
		System.out.println("old ag " + oldAg.getId());
		System.out.println("c.getDelegatedAt()" + c.getDelegatedAt());

		Agent newAg = this.findAnOtherAgentFreeAndActif(oldAg, c.getType());
		System.out.println("New ag : " + newAg.getId());
		if (c.getResponsable().equals(c.getFirstResponsable())) {
			System.out.println("1er RESP === RESP ");
			if (oldAg.getNbrClaimsOpened() == 0) {
				oldAg.setMoyAssiduite(calculMoyTemp(1, oldAg.getMoyAssiduite(), c.getCreatedAt(), c.getOpenedAt()));
			} else {
				oldAg.setMoyAssiduite(calculMoyTemp(oldAg.getNbrClaimsOpened(), oldAg.getMoyAssiduite(),
						c.getCreatedAt(), c.getOpenedAt()));
			}
			c.setTitle("delegated 1 ");
		} else {
			if (oldAg.getNbrClaimsOpened() == 0) {
				oldAg.setMoyAssiduite(calculMoyTemp(1, oldAg.getMoyAssiduite(), c.getDelegatedAt(), this.getDateNow()));
			} else {
				oldAg.setMoyAssiduite(calculMoyTemp(oldAg.getNbrClaimsOpened(), oldAg.getMoyAssiduite(),
						c.getDelegatedAt(), this.getDateNow()));
			}
			c.setTitle("delegated 2 ");
			// infomer tout les agents
		}
		if (c.getPriority() == ClaimPriority.FAIBLE) {
			this.changePriority(c, ClaimPriority.MOYEN);
		}
		if (c.getPriority() == ClaimPriority.MOYEN) {
			this.changePriority(c, ClaimPriority.ELEVEE);
		}
		if (c.getPriority() == ClaimPriority.ELEVEE) {
			this.changePriority(c, ClaimPriority.URGENT);
			// informer tout les Agents
		}
		this.changeStatus(c, ClaimStatus.EN_ATTENTE);
		c.setDelegatedAt(new Date());
		c.setResponsable(newAg);
		em.merge(oldAg);
		em.merge(c);
		return c;
	}

	
	public long calculMoyTemp(int nbClaim, long moyenne, Date deb, Date fin) {
		long result = 0;
		long diff = (long) ((fin.getTime() - deb.getTime()));
		result = (moyenne + diff) / nbClaim;

		return result;
	}

	
	public Date getDateNow() {
		SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String pattern = "yyyy-MM-dd HH:mm:ss.SSS";

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, new Locale("fr", "FR"));

		String date = simpleDateFormat.format(new Date());
		System.out.println("DATE " + date);

		java.sql.Timestamp sqlTimestamp = java.sql.Timestamp.valueOf(date);
		java.util.Date improperUtilDate = sqlTimestamp;

		return improperUtilDate;
	}

}
