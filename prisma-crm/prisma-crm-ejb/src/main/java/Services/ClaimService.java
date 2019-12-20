package Services;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import java.security.SecureRandom;

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
import Entities.User;
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


    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String NUMBER = "0123456789";

    private static final String DATA_FOR_RANDOM_STRING = CHAR_LOWER + CHAR_UPPER + NUMBER;
    private static SecureRandom random = new SecureRandom();
	
	@Override
	public int addClaim(Claim c) throws Exception {
		c.toString();
		c.setCreatedBy(em.find(Client.class, c.getCreatedById()));
		System.out.print(c.getType());
		c.setResponsable(this.findAnAgentFreeAndActif(c.getType()));
		c.setFirstResponsable(c.getResponsable());
		c.setResolvedBy(null);
		em.persist(c);
		c.setCode(this.generateRandomString(5));
		if (c.getCreatedBy().getEmail() != "") {
			JavaMailUtil.sendMail(c.getCreatedBy().getEmail(), "Réclamation ajouté ", "Bonjour " + c.getCreatedBy().getLastName()
					+ "<br> Votre réclamation a été bien enrégistré .  <br> Pour pouvoir suivre l'avancement de votre réclamation, voici le code : "+ c.getCode());
		}
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
		if (c!= null) {
			return c;
		} else {
			return null;	
		}
	}

	@Override
	public Claim getByCode(String code) {
		TypedQuery<Claim> query = em.createQuery("SELECT c from Claim c where c.code=:cd ORDER BY c.id", Claim.class).setParameter("cd", code);
		if (query.getResultList().size() !=0) {
			Claim c = query.getResultList().get(0);
			System.out.println(c);
			return c;
		} return null;
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
		TypedQuery<Claim> query = em.createQuery("SELECT c from Claim c where c.createdBy=:c", Claim.class).setParameter("c", c);
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
	public Agent findAnAgentFreeAndActif(ClaimType t) throws Exception {
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
			JavaMailUtil.sendMail(a.getEmail(), "Nouvelle réclamation", "Bonjour " + a.getLastName()
			+ ", <br> Une nouvelle réclamation vous a etais affecté, veuillez nous aider à la résoudre au plus vite ! . <br> Cordialement .");
			System.out.println(a.getRole());
		} else { // on prend l'agent ayant le moins de Réclamation traités
			TypedQuery<Agent> query2 = em
					.createQuery("SELECT a from Agent a WHERE a.role=:t ORDER BY nbrClaimsResolved DESC", Agent.class)
					.setParameter("t", type);
			a = query2.getResultList().get(0);
			JavaMailUtil.sendMail(a.getEmail(), "Nouvelle réclamation", "Bonjour " + a.getLastName()
			+ ", <br> Une nouvelle réclamation vous a étais affecté, veuillez nous aider à la résoudre au plus vite ! <br> . <br> Cordialement .");
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

		String qlString = "SELECT a from Agent a WHERE a.role=:t AND a.id<>:idOld ORDER BY nbrClaimsResolved";
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
			ag.setMoyReponse(calculMoyTemp(ag.getNbrClaimsResolved(), ag.getMoyReponse(), c.getOpenedAt(), c.getResolvedAt()));

			// l responsable houw bidou nafs l repsonsable lowel
			if (c.getResponsable().equals(c.getFirstResponsable())) {
				ag.setNbrClaimsOpenedAndResolved(ag.getNbrClaimsOpenedAndResolved() + 1);
			}
			if (c.getCreatedBy().getEmail() != "") {
				try {
					JavaMailUtil.sendMail(c.getCreatedBy().getEmail(), "Réclamation résolut", "Bonjour " + c.getCreatedBy().getLastName()
							+ ", <br> Votre réclamation à ete mis en état résolu, veulliez la confirmer en accédant à votre espace client . <br> Cordialement .");
				} catch (Exception e) {
					e.printStackTrace();
				}
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
		oldAg.setMoyAssiduite(calculMoyTemp(oldAg.getNbrClaimsOpened(), oldAg.getMoyAssiduite(),c.getOpenedAt(), this.getDateNow()));
		this.upgradePriority(c, newAg);
		this.changeStatus(c, ClaimStatus.EN_ATTENTE);
		c.setDelegatedAt(new Date());
		c.setResponsable(newAg);
		c.setOpenedAt(null);
		em.merge(oldAg);
		em.merge(c);
		return c;
	}
	
	public Claim confirmer(Claim c) {
		Agent ag = c.getResponsable();
		ag.setNbrClaimsConfirmed(ag.getNbrClaimsConfirmed() + 1);
		this.changeStatus(c, ClaimStatus.CONFIRMEE);
		em.merge(c);
		em.merge(ag);
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
	
	public void updateFAQ() {
		List<FreqWordAllClaims> listFwac = this.getFwac();
		List<Claim> listC = listFwac.get(0).getListClaims();
		
		for (int i=0 ; i< listC.size() ; i++) {
			if (listC.get(i).getStatus() == ClaimStatus.RESOLU) {
				listC.get(i).setIsFaq(true);
				this.merge(listC.get(i));
				break;
			}
		}
		
		
		/*Claim c = listFwac.get(0).getListClaims().get(0);
		c.setIsFaq(true);
		Boolean b = c.getIsFaq();
		this.merge(c);*/
	}
	
	public List<FreqWordAllClaims> getFwac() {
		List<Claim> allClaims = this.getAll();
		List<FreqWordClaim> allFwc = new ArrayList<>();
		List<FreqWordAllClaims> listFwac = new ArrayList<>();

		for (Claim c:allClaims) {
			allFwc.addAll(this.getKeyWords(c));
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
		return listFwac;
	}
	
	public List<FreqWordClaim> getFwc() {
		List<Claim> allClaims = this.getAll();
		List<FreqWordClaim> allFwc = new ArrayList<>();
		List<FreqWordAllClaims> listFwac = new ArrayList<>();

		for (Claim c:allClaims) {
			allFwc.addAll(this.getKeyWords(c));
		}
		allFwc.sort(Comparator.comparing(FreqWordClaim::getFrequence).reversed());
		return allFwc;
	}
	
	public List<FreqWordClaim> getKeyWords(Claim c)  {
		List<FreqWordClaim> list = new ArrayList<>();
		String text = c.getDescription().replaceAll("[\\p{Punct}]","");
		
		String[] words = text.split(" ");

		 
		/*
		try (BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\arafe\\git\\prisma-crm\\prisma-crm\\prisma-crm-ejb\\src\\main\\java\\Utils\\ENGLISH.txt"))) {
		    while (br.ready()) {
		    	toExtract.add(br.readLine());
		    }
		}
		System.out.println(words);
		for (String a:toExtract) {
			if (words.contains(a)) {
				words.remove(words.get(a));
			}
		}
		System.out.println(words);
		*/

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
	
	public Client findClientById(int id ) {
		return em.find(Client.class, id);
	}
	public User findUserById(int id ) {
		return em.find(User.class, id);
	}
	
	public void bipperAgent(int id ) throws Exception {
		System.out.println("clm servc");
		Agent a = em.find(Agent.class, id);
		if (a.getEmail() != "") {
			JavaMailUtil.sendMail(a.getEmail(), "Réclamation en attente non traitée ", "Bonjour " + a.getLastName()
					+ "<br> Vous avez une réclamation non traitée <br> Veuillez réagir en repondant à la demande de notre client ");
		}
	}
	
	public static String generateRandomString(int length) {
        if (length < 1) throw new IllegalArgumentException();

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {

			// 0-62 (exclusive), random returns 0-61
            int rndCharAt = random.nextInt(DATA_FOR_RANDOM_STRING.length());
            char rndChar = DATA_FOR_RANDOM_STRING.charAt(rndCharAt);

            // debug
            System.out.format("%d\t:\t%c%n", rndCharAt, rndChar);

            sb.append(rndChar);

        }

        return sb.toString();

    }
	
	public List<Agent> getAllAgent() {
		TypedQuery<Agent> query = em.createQuery("SELECT a from Agent a where a.role<>:a", Agent.class)
				.setParameter("a", Role.Admin);
		List<Agent> cf = query.getResultList();
		return cf;
	}
	
}