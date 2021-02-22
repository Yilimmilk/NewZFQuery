package cn.mapotofu.bean;

import java.util.HashMap;
import java.util.Map;

public class ExamInfoBean {
    //课程名
    private String className;

    //校区名
    private String campuses;

    //教室
    private String place;

    //考试时间
    private String time;

    public ExamInfoBean(String className, String campuses, String place, String time) {
        super();
        this.className = className;
        this.campuses = campuses;
        this.place = place;
        this.time = time;
    }

    private void setClassName(String className){
        this.className = className;
    }

    private String getClassName(){
        return className;
    }

    private void setCampuses(String campuses){
        this.campuses = campuses;
    }

    private String getCampuses(){
        return campuses;
    }

    private void setPlace(String place){
        this.place = place;
    }

    private String getPlace(){
        return place;
    }

    private void setTime(String time){
        this.time = time;
    }

    private String getTime(){
        return time;
    }

    public Map<String,String> getScheduleBeanValues() {
        Map<String,String> list = new HashMap<>();
        list.put("className",className);
        list.put("campuses",campuses);
        list.put("place",place);
        list.put("time",time);
        return list;
    }
}
