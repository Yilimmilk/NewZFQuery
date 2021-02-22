package cn.mapotofu.bean;

import java.util.HashMap;
import java.util.Map;

public class GradeBean {
    //课程名
    private String className;

    //教师名
    private String teacher;

    //分数
    private String fraction;

    //绩点
    private String gpa;

    public GradeBean(String className, String teacher, String fraction, String gpa) {
        super();
        this.className = className;
        this.teacher = teacher;
        this.fraction = fraction;
        this.gpa = gpa;
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

    private void setFraction(String fraction){
        this.fraction = fraction;
    }

    private String getFraction(){
        return fraction;
    }

    private void setGpa(String gpa){
        this.gpa = gpa;
    }

    private String getGpa(){
        return gpa;
    }

    public Map<String,String> getScheduleBeanValues() {
        Map<String,String> list = new HashMap<>();
        list.put("className",className);
        list.put("teacher",teacher);
        list.put("fraction",fraction);
        list.put("gpa",gpa);
        return list;
    }

}
