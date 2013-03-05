package com.dailyphebus;


public class BusStop {
	
	private String name = null;
	private String code = null;
	
	BusStop (String name, String code) {
		this.setName(name);
		this.setCode(code);
	}


	public String toString () {
		if (this.code.equals((String)"0") && this.name.equals((String)"0"))
				return "Choisissez dans la liste...";
		else
			return getName();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
