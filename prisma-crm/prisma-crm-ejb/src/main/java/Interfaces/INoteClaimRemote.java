package Interfaces;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Remote;

import Entities.Claim;
import Entities.NoteClaim;

@Remote
@LocalBean
public interface INoteClaimRemote {
	
	public List<NoteClaim> getAll();
	public NoteClaim addNoteClaim(NoteClaim nc , Claim c);
	public void deleteNotesByClaimId(int id);	
	public List<NoteClaim> getNotesByClaimId(int id);
	public NoteClaim getNoteById(int id) ;
	public void deleteNoteClaim(NoteClaim nc );
	public NoteClaim updateNoteClaim(NoteClaim nc);

}
