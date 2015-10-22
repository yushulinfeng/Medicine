package org.outing.medicine.fun_remind;

public class AnRemind {
    private String drugId = "";
    private String drugName = "";
    private String drugText = "";

    //图片先用默认的，检测SD卡是否有该图片，然后判断是否加载即可

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
