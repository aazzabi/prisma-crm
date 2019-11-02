package Entities;

public class FreqWordClaim  {

	private Claim claim;
	private String word;
	private int frequence;
	public Claim getClaim() {
		return claim;
	}
	
	public FreqWordClaim() {}
	
	public FreqWordClaim(Claim claim, String word, int frequence) {
		super();
		this.claim = claim;
		this.word = word;
		this.frequence = frequence;
	}

	public void setClaim(Claim claim) {
		this.claim = claim;
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public int getFrequence() {
		return frequence;
	}
	public void setFrequence(int frequence) {
		this.frequence = frequence;
	}
	

	 /* @Override
	  public int compareTo(FreqWordClaim u) {
	    if (getFrequence() == 0 || u.getFrequence() == 0) {
	      return 0;
	    }
	    return getFrequence() > u.getFrequence();
	  }*/
	
}
