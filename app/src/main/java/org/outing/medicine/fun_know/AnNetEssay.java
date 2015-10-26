package org.outing.medicine.fun_know;

public class AnNetEssay {
    private String id;
    private String title;
    private String time;
    private String author;

    public AnNetEssay() {
    }

    public AnNetEssay(String id, String title, String time, String author) {
        this.id = id;
        this.title = title;
        this.time = time;
        this.author = author;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
