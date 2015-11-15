package org.outing.medicine.fun_remind;

public class AnHistory {
    private String id = "";
    private String time = "";
    private String date = "";
    private String text = "";
    private String state = "";

    public AnHistory() {
    }

    public AnHistory(String id,String time, String date, String text, String state) {
        this.id = id;
        this.time = time;
        this.date = date;
        this.text = text;
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
