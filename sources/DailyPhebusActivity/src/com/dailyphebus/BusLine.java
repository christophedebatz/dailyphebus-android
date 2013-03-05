package com.dailyphebus;


public class BusLine {
	
	private String letter = null;
	private String firstExtremity = null;
	private String secondExtremity = null;
	
	BusLine (String letter, String firstExtremity, String secondExtremity) {
		this.setLetter(letter);
		this.setFirstExtremity(firstExtremity);
		this.setSecondExtremity(secondExtremity);
	}
	
	BusLine (String spinnerLineSelection) {
		this.setLineBySpinnerSelection(spinnerLineSelection);
	}
	
	public String toString () {
		if (this.firstExtremity.equals((String)"0") && this.letter.equals((String)"0") && this.secondExtremity.equals((String)"0"))
			return "Choisissez dans la liste...";
		else
			return this.letter + " (" + this.firstExtremity + " - " + this.secondExtremity + ")";
	}
	
	public void setLineBySpinnerSelection (String spinnerLineSelection) {
		String[] split1 = spinnerLineSelection.substring(0, spinnerLineSelection.length() - 1).split("\\(");
		String[] split2 = split1[1].trim().split("-");
		this.setLetter(split1[0].trim());
		this.setFirstExtremity(split2[0].trim());
		this.setSecondExtremity(split2[1].trim());
	}
	
	public String getExtremityByDirection (short direction) {
		return (direction == 1) ? this.getFirstExtremity() : this.getSecondExtremity();
	}
	
	public String getLetter() {
		return letter;
	}

	public void setLetter(String letter) {
		this.letter = letter;
	}

	public String getFirstExtremity() {
		return firstExtremity;
	}

	public void setFirstExtremity(String firstExtremity) {
		this.firstExtremity = firstExtremity;
	}

	public String getSecondExtremity() {
		return secondExtremity;
	}

	public void setSecondExtremity(String secondExtremity) {
		this.secondExtremity = secondExtremity;
	}
}
