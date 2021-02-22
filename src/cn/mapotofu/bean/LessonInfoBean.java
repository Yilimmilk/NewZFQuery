package cn.mapotofu.bean;

import java.util.HashMap;
import java.util.Map;

public class LessonInfoBean {
    //消息内容
    private String content;

    //发布时间
    private String time;

    public LessonInfoBean(String content, String time) {
        super();
        this.content = content;
        this.time = time;
    }

    private void setContent(String content){
        this.content = content;
    }

    private String getContent(){
        return content;
    }

    private void setTime(String time){
        this.time = time;
    }

    private String getTime(){
        return time;
    }

    public Map<String,String> getScheduleBeanValues() {
        Map<String,String> list = new HashMap<>();
        list.put("content",content);
        list.put("time",time);
        return list;
    }
}
