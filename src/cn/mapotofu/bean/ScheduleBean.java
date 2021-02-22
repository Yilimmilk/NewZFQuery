package cn.mapotofu.bean;

import java.util.HashMap;
import java.util.Map;

public class ScheduleBean {

    //课程名
    private String className;

    //教师名
    private String teacher;

    //校区
    private String campuses;

    //教学地点
    private String place;

    //上课周
    private String week;

    //上课日
    private String weekDay;

    //上课时间（节）
    private String clock;

    public ScheduleBean(String className, String teacher, String campuses, String place, String week, String weekDay, String clock) {
        super();
        this.className = className;
        this.teacher = teacher;
        this.campuses = campuses;
        this.place = place;
        this.week = week;
        this.weekDay = weekDay;
        this.clock = clock;
    }

    private void setClassName(String className){
        this.className = className;
    }

    private String getClassName(){
        return className;
    }

    private void setTeacher(String teacher){
        this.teacher = teacher;
    }

    private String getTeacher(){
        return teacher;
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

    private void setWeek(String week){
        this.week = week;
    }

    private String getWeek(){
        return week;
    }

    private void setWeekDay(String weekDay){
        this.weekDay = weekDay;
    }

    private String getWeekDay(){
        return weekDay;
    }

    private void setClock(String clock){
        this.clock = clock;
    }

    private String getClock(){
        return clock;
    }

    public Map<String,String> getScheduleBeanValues() {
        Map<String,String> list = new HashMap<>();
        list.put("className",className);
        list.put("teacher",teacher);
        list.put("campuses",campuses);
        list.put("place",place);
        list.put("week",week);
        list.put("weekday",weekDay);
        list.put("clock",clock);
        return list;
    }
}
