package org.outing.medicine.fun_drug;

public class AnDrug {
    private String name;
    private String com_name;
    private String id;

    public AnDrug(String name, String com_name, String id) {//id默认为""
        this.name = name;
        this.com_name = com_name;
        this.id = id;
    }

    // 目前只有getter
    public String getName() {
        return name;
    }

    public String getCommonName() {
        return com_name;
    }

    public String getID() {
        return id;
    }

}
