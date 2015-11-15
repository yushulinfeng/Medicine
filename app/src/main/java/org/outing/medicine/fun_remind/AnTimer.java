package org.outing.medicine.fun_remind;

public class AnTimer {
    private String id = "";
    private String name = "";
    private String text = "";

    private int hour = -1;
    private int minute = -1;
    private int method = 0;

    private boolean enable = true;// 是否可用，便于以后拓展
    private String times="0";//提醒次数，0为每天，1为单次，其他的暂定为7位0或1字符串

    public AnTimer() {
    }

    public AnTimer(String name, int hour, int minute, int method) {
        this.name = name;
        this.hour = hour;
        this.minute = minute;
        this.method = method;
        id = System.currentTimeMillis() + "";
        text = "";
        enable = true;
        times="0";
    }

    public AnTimer(String id, String name, String text, int hour, int minute, int method) {
        this.id = id;
        this.name = name;
        this.text = text;
        this.hour = hour;
        this.minute = minute;
        this.method = method;
        this.enable = true;
        this.times="0";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getMethod() {
        return method;
    }

    public void setMethod(int method) {
        this.method = method;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    //获取可显示的时间
    public String getTime() {
        return formatTime(hour, minute);
    }

    /**
     * 将时间格式化，用于显示(时间不对返回"")
     */
    public static String formatTime(int hour, int min) {
        if (hour == -1 || min == -1) {
            return "";
        }
        String hour_show = "", min_show = "", ampm_sow = "上午";
        if (hour < 10) hour_show = "0" + hour;
        else if (hour < 12) hour_show = "" + hour;
        else if (hour == 12) {//单独列出防止歧义
            ampm_sow = "中午";
            hour_show = "" + hour;
        } else {
            ampm_sow = "下午";
            hour -= 12;
            if (hour < 10) hour_show = "0" + hour;
            else hour_show = "" + hour;
        }
        if (min < 10) min_show = "0" + min;
        else min_show = "" + min;
        String time_show = ampm_sow + " " + hour_show + ":" + min_show;
        return time_show;
    }
}
