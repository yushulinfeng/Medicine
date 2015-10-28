package org.outing.medicine.fun_remind;

public class AnRemind {
    private String drugId = "";
    private String drugName = "";
    private String drugText = "";

    public AnRemind() {
    }

    public AnRemind(String drugId, String drugName, String drugText) {
        this.drugId = drugId;
        this.drugName = drugName;
        this.drugText = drugText;
    }

    public String getDrugId() {
        return drugId;
    }

    public void setDrugId(String drugId) {
        this.drugId = drugId;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public String getDrugText() {
        return drugText;
    }

    public void setDrugText(String drugText) {
        this.drugText = drugText;
    }
}
