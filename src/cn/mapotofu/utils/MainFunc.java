package cn.mapotofu.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainFunc {

    private final String url = "http://syjw.wsyu.edu.cn";
    //完整cookie
    private Map<String,String> cookies;
    //用于存储中间值的cookie
    private Map<String,String> cookiesConvert = new HashMap<String,String>();
    private String modulus;
    private String exponent;
    private String csrftoken;
    private Connection connection;
    private Connection.Response response;
    private Document document;
    private String stuNum;
    private String password;

    public MainFunc(String stuNum, String password){
        this.stuNum = stuNum;
        this.password = password;
    }

    public void init() throws Exception{
        getCsrftoken();
        getRSApublickey();
    }

    // 获取csrftoken和Cookies
    private void getCsrftoken(){
        try{
            connection = Jsoup.connect(url+ "/xtgl/login_slogin.html?language=zh_CN&_t="+new Date().getTime());
            connection.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36");
            response = connection.execute();
            cookies = response.cookies();
            //调用函数取出insert_cookie参数
            cookiesConvert = convertBeforeCookie(cookies);
            //保存csrftoken
            document = Jsoup.parse(response.body());
            csrftoken = document.getElementById("csrftoken").val();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    // 获取公钥并加密密码
    public void getRSApublickey() throws Exception{
        connection = Jsoup.connect(url+ "/xtgl/login_getPublicKey.html?" +
                "time="+ new Date().getTime());
        connection.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36");
        response = connection.cookies(cookies).ignoreContentType(true).execute();
        JSONObject jsonObject = JSON.parseObject(response.body());
        modulus = jsonObject.getString("modulus");
        exponent = jsonObject.getString("exponent");
        password = RSAEncoder.RSAEncrypt(password, Base64Encryption.b64tohex(modulus), Base64Encryption.b64tohex(exponent));
        password = Base64Encryption.hex2b64(password);
    }

    //登录
    public boolean beginLogin() throws Exception{

        connection = Jsoup.connect(url+ "/xtgl/login_slogin.html");
        connection.header("Content-Type","application/x-www-form-urlencoded;charset=utf-8");
        connection.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36");

        connection.data("csrftoken",csrftoken);
        connection.data("yhm",stuNum);
        connection.data("mm",password);
        connection.data("mm",password);
        response = connection.cookies(cookies).ignoreContentType(true)
                .method(Connection.Method.POST).execute();
        //调用函数去除rememberMe参数，增加insert_cookie参数
        cookies = convertAfterCookie(response.cookies());
        document = Jsoup.parse(response.body());
        if(document.getElementById("tips") == null){
            System.out.println("当前登陆账号:"+stuNum);
            return true;
        }else{
            System.out.println(document.getElementById("tips").text());
            return false;
        }
    }

    /***
    * 在获取csrftoken后会返回一个cookie，从中取出insert_cookie参数，然后返回
    * @return Map<String, String>，返回的是取出参数后的Map
    ***/
    public Map<String, String> convertBeforeCookie(Map<String, String> cookie) {
        cookiesConvert.clear();
        String insert_cookie = cookie.get("insert_cookie");
        cookiesConvert.put("insert_cookie",insert_cookie);
        return cookiesConvert;
    }

    /***
     * 从登陆成功后返回的response中去除rememberMe参数,并且加入上一个函数存储的insert_cookie参数，并返回
     * @return Map<String, String>，返回的是去除rememberMe参数，增加insert_cookie参数后的Map
     ***/
    public Map<String, String> convertAfterCookie(Map<String, String> cookie) {
        cookie.remove("rememberMe");
        cookie.put("insert_cookie",cookiesConvert.get("insert_cookie"));
        return cookie;
    }

    // 查询学生信息
    public String getStudentInformaction() throws Exception {
        System.out.println("----查询学生信息----:"+stuNum);
        connection = Jsoup.connect(url+ "/xsxxxggl/xsxxwh_cxCkDgxsxx.html?gnmkdm=N100801&su="+ stuNum);
        connection.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36");
        response = connection.cookies(cookies).ignoreContentType(true).execute();
        return response.body();
    }

    // 获取课表信息
    public String getStudentTimetable(int year , int term) throws Exception {
        System.out.println("----获取课程表----:"+stuNum);
        connection = Jsoup.connect(url+ "/kbcx/xskbcx_cxXsKb.html?gnmkdm=N2151");
        connection.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36");
        connection.data("xnm",String.valueOf(year));
        connection.data("xqm",String.valueOf(term * term * 3));
        response = connection.cookies(cookies).method(Connection.Method.POST).ignoreContentType(true).execute();
        return response.body();
    }

    // 获取成绩信息
    public String getStudentGrade(int year , int term) throws Exception {
        System.out.println("----查询成绩----:"+stuNum);
        Map<String,String> datas = new HashMap<>();
        datas.put("xnm",String.valueOf(year));
        datas.put("xqm",String.valueOf(term * term * 3));
        datas.put("_search","false");
        datas.put("nd",String.valueOf(new Date().getTime()));
        datas.put("queryModel.showCount","20");
        datas.put("queryModel.currentPage","1");
        datas.put("queryModel.sortName","");
        datas.put("queryModel.sortOrder","asc");
        datas.put("time","0");

        connection = Jsoup.connect(url+ "/cjcx/cjcx_cxDgXscj.html?gnmkdm=N305005&layout=default&su=" + stuNum);
        connection.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36");
        response = connection.cookies(cookies).method(Connection.Method.POST)
                .data(datas).ignoreContentType(true).execute();
        connection = Jsoup.connect(url+ "/cjcx/cjcx_cxDgXscj.html?doType=query&gnmkdm=N305005");
        connection.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36");
        response = connection.cookies(cookies).method(Connection.Method.POST)
                .data(datas).ignoreContentType(true).execute();
        return response.body();
    }

    // 查询考试信息
    public String getExamInfo(int year, int term) throws Exception {
        System.out.println("----查询考试信息----:"+stuNum);
        Map<String,String> datas = new HashMap<>();
        datas.put("xnm",String.valueOf(year));
        datas.put("xqm",String.valueOf(term * term * 3));
        datas.put("_search","false");
        datas.put("nd",String.valueOf(new Date().getTime()));
        datas.put("queryModel.showCount","20");
        datas.put("queryModel.currentPage","1");
        datas.put("queryModel.sortName","");
        datas.put("queryModel.sortOrder","asc");
        datas.put("time","0");

        connection = Jsoup.connect(url+ "/kwgl/kscx_cxXsksxxIndex.html?doType=query&gnmkdm=N358105");
        connection.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36");
        response = connection.cookies(cookies).data(datas).method(Connection.Method.POST).ignoreContentType(true).execute();
        return response.body();
    }

    // 获取课程消息
    public String getLessonsInfo() throws Exception {
        System.out.println("----获取课程信息----:"+stuNum);
        Map<String,String> datas = new HashMap<>();
        datas.put("sfyy","0");
        datas.put("flag","1");
        datas.put("_search","false");
        datas.put("nd",String.valueOf(new Date().getTime()));
        datas.put("queryModel.showCount","20");
        datas.put("queryModel.currentPage","1");
        datas.put("queryModel.sortName","cjsj");
        datas.put("queryModel.sortOrder","desc");
        datas.put("time","0");

        connection = Jsoup.connect(url+ "/xtgl/index_cxDbsy.html?doType=query");
        connection.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36");
        response = connection.cookies(cookies).data(datas).method(Connection.Method.POST).ignoreContentType(true).execute();
        return response.body();
    }

    public void logout() throws Exception {
        connection = Jsoup.connect(url+ "/logout");
        connection.header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36");
        response = connection.cookies(cookies).ignoreContentType(true).execute();
        System.out.println("退出登录当前帐号:"+stuNum);
    }

}