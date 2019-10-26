package Interfaces;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Remote;

import Entities.Agent;
import Entities.Claim;
import Entities.Client;
import Entities.NoteClaim;
import Enums.ClaimPriority;
import Enums.ClaimStatus;
import Enums.ClaimType;

@Remote
@LocalBean
public interface IClaimServiceRemote {

	public int addClaim(Claim c);
	public List<Claim> getAll();
	
	public Claim getById(int id);
	public Claim getByCode(String code);
	
	public Object merge(Object o);
	public void changeStatus(Claim c, ClaimStatus status);
	public void changePriority(Claim c, ClaimPriority priority);
	
	public void deleteClaim(Claim c);	
	
	public int deleteClaimById(int id);	
	public void deleteClaimByClient(Client c);	
	public int deletNoteClaimById(int id);	
	
	public List<Claim> getClaimsByClient(Client u);
	public List<Claim> getClaimsByResponsable(Agent a);
	public List<Claim> getClaimsResolvedBy(Agent a);
	
	public List<Claim> getByPrioirty(ClaimPriority priority);
	public List<Claim> getByStatus(ClaimStatus status);
	public List<Claim> getByType(ClaimType type);
	public Agent findAnAgentFreeAndActif(ClaimType t) ;
	public void affectClaimToAgent(Claim c, Agent a);

	public Agent getResponsableById(int id);
	
	
	public Claim deleguer(Claim c);
	public Claim open(Claim c) ;
	public Claim resolve(Claim c);
	//autre que le premier choisis ( lors de la delgation)
	public Agent findAnOtherAgentFreeAndActif(Agent a, ClaimType t);
}
