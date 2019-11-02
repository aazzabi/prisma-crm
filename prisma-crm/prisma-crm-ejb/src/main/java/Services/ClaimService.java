package Services;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

/*
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
*/
import Entities.Agent;
import Entities.Claim;
import Entities.Client;
import Entities.FreqWordAllClaims;
import Entities.FreqWordClaim;
import Entities.NoteClaim;
import Enums.ClaimPriority;
import Enums.ClaimStatus;
import Enums.ClaimType;
import Enums.Role;
import Interfaces.IClaimServiceRemote;
import Utils.JavaMailUtil;

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
	public List<Claim> getAllFaq() {
		TypedQuery<Claim> query = em.createQuery("SELECT c from Claim c where c.isFaq=true", Claim.class);
		List<Claim> cf = query.getResultList();
		return cf;
	}

	@Override
	public Claim getFaqById(int id) {
		Claim c = (Claim) em.find(Claim.class, id);
		return c;
	}

	@Override
	public Claim addClaimToFaq(int id) {
		Claim c = (Claim) em.find(Claim.class, id);
		c.setIsFaq(true);
		em.merge(c);
		return c;
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
		TypedQuery<Claim> query = em.createQuery("SELECT c from Claim c where c.responsable=:a", Claim.class)
				.setParameter("a", a);
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

		String qlString = "SELECT a from Agent a where a.role=:t and a.dispoClaim=:d ORDER BY a.nbrClaimsResolved";
		Query query = em.createQuery(qlString, Agent.class).setParameter("d", "disponible").setParameter("t", type);
		List<Agent> agents = query.getResultList();
		System.out.println("160 - CLAIM TYPE " + t);

		if (agents.size() == 1) {
			a = (Agent) query.getResultList().get(0);
			a.setDispoClaim("indisponible");
			System.out.println(a.getRole());
		} else { // on prend l'agent ayant le moins de Réclamation traités
			TypedQuery<Agent> query2 = em
					.createQuery("SELECT a from Agent a WHERE a.roleAgent=:t ORDER BY nbrClaimsResolved DESC",
							Agent.class)
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
		if (a.getEmail() != "") {
			try {
				JavaMailUtil.sendMail(a.getEmail(), "Nouvelle réclamation", "Bonjour " + a.getLastName()
						+ ", <br> Une nouvelle réclamation vous a etais affecté, veuillez nous aider à la résoudre au plus vite ! . <br> Cordialement .");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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
		if (c.getStatus() != ClaimStatus.RESOLU) {
			c.setResolvedBy(ag);
			c.setResolvedAt(new Date());
			c.setStatus(ClaimStatus.RESOLU);
			// l'agent connecté , c le responsable de la claim.. (normalement)
			ag.setNbrClaimsResolved(ag.getNbrClaimsResolved() + 1);
	
			// l responsable houw bidou nafs l repsonsable lowel
			if (c.getResponsable().equals(c.getFirstResponsable())) {
				ag.setMoyAssiduite(calculMoyTemp(ag.getNbrClaimsOpened(), ag.getMoyAssiduite(), c.getCreatedAt(), c.getResolvedAt()));
				ag.setMoyReponse(calculMoyTemp(ag.getNbrClaimsResolved(), ag.getMoyReponse(), c.getOpenedAt(), c.getResolvedAt()));
				ag.setNbrClaimsOpenedAndResolved(ag.getNbrClaimsOpenedAndResolved() + 1);
			} else { // sarét delegation
				ag.setMoyAssiduite(calculMoyTemp(ag.getNbrClaimsOpened(), ag.getMoyAssiduite(), c.getDelegatedAt(),c.getResolvedAt()));
				ag.setMoyReponse(calculMoyTemp(ag.getNbrClaimsResolved(), ag.getMoyReponse(), c.getOpenedAt(), c.getResolvedAt()));
			}
			em.merge(ag);
			em.merge(c);
		}
		return c;
	}

	@Override
	public Claim open(Claim c) {
		Agent ag = c.getResponsable();
		if (c.getStatus() == ClaimStatus.EN_ATTENTE) {
			//idha tet7al awel marra , walla t7alet ba3d délégation
			ag.setNbrClaimsOpened(ag.getNbrClaimsOpened() + 1);
			c.setStatus(ClaimStatus.EN_COURS);
			c.setOpenedAt(new Date());
			
			// l responsable houwa bidou nafs l repsonsable lowel ( li 7alha zeda )
			if (c.getResponsable().equals(c.getFirstResponsable())) {
				ag.setMoyAssiduite(calculMoyTemp(ag.getNbrClaimsOpened(), ag.getMoyAssiduite(), c.getCreatedAt(), this.getDateNow()));
			} else { // sarét delegation deja : donc li bech y7elha mehouch nafsou le premier responsable
				ag.setMoyAssiduite(calculMoyTemp(ag.getNbrClaimsOpened(), ag.getMoyAssiduite(), c.getDelegatedAt(),this.getDateNow()));
			}		

			em.merge(ag);
			em.merge(c);
		}
		
		return c;
	}

	@Override
	public Claim deleguer(Claim c) throws Exception {
		Agent oldAg = c.getResponsable();
		Agent newAg = this.findAnOtherAgentFreeAndActif(oldAg, c.getType());

		if (c.getResponsable().equals(c.getFirstResponsable())) {
			// awel mara tsir delegation : donc bech n3a9eb l 1er responsable,
			// w nzziiddlou l wa9t li 9a3dou entre OpenedAt w delegatedAt , lel moyAssiduité
			System.out.println("1er RESP === RESP ");
			oldAg.setMoyAssiduite(calculMoyTemp(oldAg.getNbrClaimsOpened(), oldAg.getMoyAssiduite(),c.getCreatedAt(), c.getOpenedAt()));
		} else {
			oldAg.setMoyAssiduite(calculMoyTemp(oldAg.getNbrClaimsOpened(), oldAg.getMoyAssiduite(),c.getDelegatedAt(), this.getDateNow()));
		}
		this.upgradePriority(c, newAg);
		this.changeStatus(c, ClaimStatus.EN_ATTENTE);
		c.setDelegatedAt(new Date());
		c.setResponsable(newAg);
		c.setOpenedAt(null);
		em.merge(oldAg);
		em.merge(c);
		return c;
	}

	private void upgradePriority(Claim c, Agent a) throws Exception {
		String links = "http://localhost:9080/prisma-crm-web/claim/id/";
		String link = links.concat(Integer.toString(c.getId()));
		if (c.getPriority() == ClaimPriority.FAIBLE) {
			this.changePriority(c, ClaimPriority.MOYEN);
			if (a.getEmail() != "") {
				JavaMailUtil.sendMail(a.getEmail(), "Réclamation ", "Bonjour " + a.getLastName()
						+ "<br> Un de vos colégue a trouvé des problémes pour résoudre une réclation .  <br> Veuillez réagir en repondant à la demande de notre client . <br>  <a href="
						+ link + ">Cliquer ici</a> ");
			}
		}
		else if (c.getPriority() == ClaimPriority.MOYEN) {
			this.changePriority(c, ClaimPriority.ELEVEE);
			if (a.getEmail() != "") {
				JavaMailUtil.sendMail(a.getEmail(), "Réclamation ", "Bonjour " + a.getLastName()
						+ "<br> Un de vos colégue a trouvé des problémes pour résoudre une réclation . <br> Veuillez réagir en repondant à la demande de notre client . <br>  <a href="
						+ link + ">Cliquer ici</a> ");
			}
		}
		else if (c.getPriority() == ClaimPriority.ELEVEE) {
			this.changePriority(c, ClaimPriority.URGENT);
			this.sendMailToAllAgent(c);// informer tout les Agents
		}
	}

	public long calculMoyTemp(int nbClaim, long moyenne, Date deb, Date fin) {
		long result = 0;
		long diff = (long) ((fin.getTime() - deb.getTime()));
		if (nbClaim == 0 ) {
			result = (moyenne + diff);
		} else { result = (moyenne + diff) / nbClaim;}

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

	public void sendMailToAllAgent(Claim c) throws Exception {
		List<Agent> list = this.getAllAgents();
		String links = "http://localhost:9080/prisma-crm-web/claim/id/";
		String link = links.concat(Integer.toString(c.getId()));
		for (Agent a : list) {
			if (a.getEmail() != "") {
				JavaMailUtil.sendMail(a.getEmail(), "Réclamation urgente", "Bonjour " + a.getLastName()
						+ "<br> Nous vennons de recvoir le passage d'une réclamation à une priorité d'urgence . <br>"
						+ "Veuillez réagir en repondant à la demande de notre client . <br>  <a href=" + link
						+ ">Cliquer ici</a> ");
			}
		}
	}

	@Override
	public List<Agent> getAllAgents() {
		List<Agent> list = em.createQuery("SELECT a from Agent a", Agent.class).getResultList();
		return list;
	}
	
	public List<FreqWordAllClaims> getKeyWords() {
	//public FreqWordAllClaims getKeyWords() {
	//public Map<String , List<FreqWordClaim>> getKeyWords() {
		List<Claim> allClaims = this.getAll();
		List<FreqWordClaim> allFwc = new ArrayList<>();
		List<FreqWordAllClaims> listFwac = new ArrayList<>();

		for (Claim c:allClaims) {
			allFwc.addAll(this.extractKeyWords(c));
		}
		allFwc.sort(Comparator.comparing(FreqWordClaim::getFrequence).reversed());
		
		Map<String , List<FreqWordClaim>> mapFwc = new TreeMap<>();
		for (FreqWordClaim fwp : allFwc) {
			//fwp.word existe dans MAP , mais fwp non
			if ((mapFwc.containsKey(fwp.getWord())) && (!mapFwc.containsValue(fwp))) {
				List<FreqWordClaim> list = mapFwc.get(fwp.getWord());
				list.add(fwp);
				mapFwc.put(fwp.getWord(), list);
			} else if (!mapFwc.containsKey(fwp.getWord())) { // mapFwp ne contient pas le mot
				List<FreqWordClaim> list = new ArrayList<>();
				list.add(fwp);
				mapFwc.put(fwp.getWord(), list);
			}
		}
		
		for(Entry<String, List<FreqWordClaim>> entry : mapFwc.entrySet()) {
			FreqWordAllClaims fwac = new FreqWordAllClaims();
			List<Claim> lc = new ArrayList<>();
			
			int freqT = 0;
			fwac.setWord(entry.getKey());
			List<FreqWordClaim> l = entry.getValue();
			for (FreqWordClaim a: l) {
				lc.add(a.getClaim());
				freqT = freqT + a.getFrequence(); 
			}
			fwac.setFreqTotal(freqT);
			fwac.setListFwc(l);
			fwac.setListClaims(lc);
			listFwac.add(fwac);
		}
		listFwac.sort(Comparator.comparing(FreqWordAllClaims::getFreqTotal).reversed());
		
		
		//allFwc.sort(Comparator.comparing(FreqWordClaim::getFrequence).reversed());

		//FreqWordAllClaims fwaaaac = new FreqWordAllClaims();
		//fwaaaac.setListFwc(allFwc);
		
		
		/*
		 * for (FreqWordAllClaims fwac : allFwac)
		for (FreqWordClaim fwc: allFwc) {
			FreqWordAllClaims fwac = new FreqWordAllClaims();
			//if (fwac.getListFwc().contains(fwc) == false) {
				fwac.addToListFwc(fwc);
				fwac.setFreqTotal(fwac.getFreqTotal() + fwc.getFrequence());
			//}
			
			int index = allFwc.indexOf(fwc);
			for (FreqWordClaim fwc2: allFwc) {
				if ((allFwc.indexOf(fwc2) != index) &&  (fwc.getWord().equals(fwc2.getWord())) ) {
					fwac.addToListFwc(fwc2);
					fwac.setFreqTotal(fwac.getFreqTotal() + fwc2.getFrequence());
				}
			}
		}*/
		
		/*
		for (FreqWordClaim fwc: allFwc) {
			fwac.addToListFwc(fwc);
			for (FreqWordClaim fwc2: allFwc) {
				if ( fwc.getWord().equals(fwc2.getWord()) ) {
					fwac.setFreqTotal(fwac.getFreqTotal() + fwc2.getFrequence());
					fwac.addToListFwc(fwc2);
				}
			}
			listFwac.add(fwac);
		}
		*/
		return listFwac;
	}
	
	public List<FreqWordClaim> extractKeyWords(Claim c) {
		List<FreqWordClaim> list = new ArrayList<>();
		String text = c.getDescription().replaceAll("[\\p{Punct}]","");
		
		String[] words = text.split(" ");
		Map<String, Integer> map = new TreeMap<>();
		 
	    for (String w : words) {
	    	if (w.length() >= 3) {
		        Integer n = map.get(w);
		        n = (n == null) ? 1 : ++n;
		        map.put(w, n);
	    	}
	    }
	    
	    for(Entry<String, Integer> entry : map.entrySet()) {
			FreqWordClaim fwc = new FreqWordClaim();
			fwc.setClaim(c);
	    	fwc.setWord(entry .getKey());
	    	fwc.setFrequence(entry .getValue());
	    	list.add(fwc);
		}	
	    
	    list.sort(Comparator.comparing(FreqWordClaim::getFrequence).reversed());	    
	    
	    return list;
	}
	
	
}
