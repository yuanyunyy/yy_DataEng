

public class word {
	private String word;
	private Integer freq;
	private Integer docID;
	
	public word (String word, Integer docID){
		this.word = word;
		this.freq = 1;
		this.docID = docID;
	}

	public void addFreq(Integer a){
		freq = freq + a;		
	}
	
	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public Integer getFreq() {
		return freq;
	}

	public void setFreq(Integer freq) {
		this.freq = freq;
	}

	public Integer getDocID() {
		return docID;
	}

	public void setDocID(Integer docID) {
		this.docID = docID;
	}

}
