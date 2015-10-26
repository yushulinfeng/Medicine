package org.outing.medicine.fun_remind;

public class AnRing implements Comparable<AnRing> {
    //此处public方便调用
    public AnRemind remind;
    public AnTimer timer;

    public AnRing() {
    }

    public AnRing(AnRemind remind, AnTimer timer) {
        this.remind = remind;
        this.timer = timer;
    }

    public String getDrugId() {//暂时不要用它，以后不好再删除
        if (remind != null)
            return remind.getDrugId();
        return "";
    }

    public String getTimerId() {
        if (timer != null)
            return timer.getId();
        return "";
    }

    public int getLocation() {
        if (timer != null)
            return timer.getHour() * 100 + timer.getMinute();
        return 0;
    }

    @Override
    public int compareTo(AnRing that) {//按时间排序
        int this_location = this.getLocation();
        int that_location = that.getLocation();
        if (this_location < that_location)
            return -1;
        else
            return 1;
    }
}
