package Entities;

import java.util.List;

public class FreqWordAllClaims {

	private int freqTotal;
	private String word;
	
	private List<FreqWordClaim> listFwc;
	private List<Claim> listClaims;
	
	public FreqWordAllClaims() {
		this.freqTotal = 0;
	}
	
	public FreqWordAllClaims(int freqTotal, String word, List<FreqWordClaim> listFwc, List<Claim> listClaims) {
		super();
		this.freqTotal = freqTotal;
		this.word = word;
		this.listFwc = listFwc;
		this.listClaims = listClaims;
	}
	
	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public int getFreqTotal() {
		return freqTotal;
	}
	public void setFreqTotal(int freqTotal) {
		this.freqTotal = freqTotal;
	}
	public List<FreqWordClaim> getListFwc() {
		return listFwc;
	}
	public void setListFwc(List<FreqWordClaim> listFwc) {
		this.listFwc = listFwc;
	}
	
	public void addToListFwc(FreqWordClaim f) {
		listFwc.add(f);
	}

	public List<Claim> getListClaims() {
		return listClaims;
	}

	public void setListClaims(List<Claim> listClaims) {
		this.listClaims = listClaims;
	}
	
	
}
