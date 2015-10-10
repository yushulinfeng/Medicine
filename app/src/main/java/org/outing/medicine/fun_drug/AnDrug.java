package org.outing.medicine.fun_drug;

public class AnDrug {
	private String name;
	private String com_name;

	public AnDrug(String name, String com_name) {
		this.name = name;
		this.com_name = com_name;
	}

	// 目前只有getter
	public String getName() {
		return name;
	}

	public String getCommonName() {
		return com_name;
	}

}
