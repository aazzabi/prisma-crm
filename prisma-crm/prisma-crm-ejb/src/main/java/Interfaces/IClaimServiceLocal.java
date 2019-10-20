package Interfaces;

import java.util.List;

import javax.ejb.Local;
import javax.xml.ws.Response;

import Entities.Agent;
import Entities.Claim;
import Entities.Client;
import Entities.User;
import Enums.ClaimPriority;
import Enums.ClaimStatus;
import Enums.ClaimType;

@Local
public interface IClaimServiceLocal {
	
	public int addClaim(Claim c);
	public List<Claim> getAll();
	
	public Claim getById(int id);
	public Claim getByCode(String code);
	
	public void editClaim(Claim c);	
	public void changeStatus(Claim c, ClaimStatus status);
	public void changePriority(Claim c, ClaimPriority priority);
	
	public void deleteClaim(Claim c);	
	public void deleteClaimById(int id);	
	public void deleteClaimByClient(Client c);	
	
	public List<Claim> getClaimsByClient(Client u);
	public List<Claim> getClaimsByResponsable(Agent a);
	public List<Claim> getClaimsResolvedBy(Agent a);
	
	public List<Claim> getByPrioirty(ClaimPriority priority);
	public List<Claim> getByStatus(ClaimStatus status);
	public List<Claim> getByType(ClaimType type);
	public Agent findAnAgentFreeAndActif() ;
	public void affectClaimToAgent(Claim c, Agent a);
	
	public String claimToJSON(Claim c);
	public String getAllClaimJSON(List<Claim> listC);
	
}
