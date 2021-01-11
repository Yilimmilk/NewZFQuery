package cn.mapotofu;

import cn.mapotofu.utils.MainFunc;
import cn.mapotofu.utils.aesrsautils.AES;
import cn.mapotofu.utils.aesrsautils.ConvertUtils;
import cn.mapotofu.utils.aesrsautils.EncryUtil;
import cn.mapotofu.utils.aesrsautils.RSA;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class getJWData extends HttpServlet {

    /***
     * @apiNote 状态码:
     *     200：请求成功；
     *     221；登录成功；
     *     421：解密失败；
     *     422：参数错误；
     *     423：学号密码错误；
     *     424：features错误；
     *     500：服务器内部错误；
     */

    protected static final String SUCCESS_CODE = "200";
    protected static final String SUCCESS_CODE_LOGIN = "221";

    protected static final String ERR_CODE_DECRYPT = "421";
    protected static final String ERR_CODE_PARAMETER = "422";
    protected static final String ERR_CODE_STUDENTNUM = "423";
    protected static final String ERR_CODE_FEATURES = "424";

    protected static final String SERVER_ERR_CODE = "500";

    protected static final String clientPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCzkBgNxJ0h0oV4HsNUf+0TH9LT" +
            "+WD3VyaXmlWMrioWOA7/ksZCNpgINXT8aWKor2C0TE1jmInTZDzcvWoRiPWriUm+" +
            "jg5LHt9LXUTVglZm7IOD5miqOb53sdlxbriK3vWEhiMEthDbGXSn3KaGKz5m0Uwy" +
            "SX+ktjDdeRWevHpDowIDAQAB";

    protected static final String serverPrivateKey = "MIICxjBABgkqhkiG9w0BBQ0wMzAbBgkqhkiG9w0BBQwwDgQIxZ/5cr0kHucCAggA" +
            "MBQGCCqGSIb3DQMHBAhSczR4QnxvcwSCAoB9wo6jmQ/7z1AxCruGVE8NVnd0dZO4" +
            "Hc2eKUnruECk4G81PEvUw8NjOqI5bENN8y/Y0wTWI13nokSRmX4BLeAxd4pl0Rgx" +
            "k+v4ITcPRK9kkOxzgh3PLtWpQ6WS9jskN0ngZyzYXJR6FNY8d6rEcn07d9WxoghK" +
            "1M/KZiCiNCbCnNG0o6J++yOl8sZn1wqxC9DK0hERf0u+xolIX7HswXEVGyvAYU1x" +
            "VvEheWh9Iy634DNRwtw8GZt6wOKS1F4K/G7cWr0j8CmuYegc0IuCtVdGSRg0DxRJ" +
            "8ShchOpVvLDroKwkOhXyeeZh6uPGJMcJjgs4j3Sw+/4YHoR+3F54XpekB6hV2E+e" +
            "D1sxHWYmiIl6bwbKTLypzK2NhvbFjKOjcbPDq7Qhc2VSx9oKPWjwsZgLEi+mEU/y" +
            "2UjwlNWTeBBvn3esEw/Ob8V/NZ9NBK8MFxUp7fUJFN1vVsHHlsNm5sM4SMAjJOsO" +
            "/b+GpvPY2LiAm+1D24yx9Fa14Vkn2V+9a4jfN9lEH1xnKjAku4vtQXcKm4Q4zXQU" +
            "pWmKrmr2ce1/3fAfZ5EINS3RB1s0+zU15xMUTr6tWeO9O3S9W6q+IRpb+ygfspAo" +
            "anZn70d2eABj934CWuLDjXiYh5yxfFcPXMicCKGZSvvz8YLVahN78iJbdt9R6kwC" +
            "UCFAFrgZ3VDtaHfO9w/m3HaJYwBbbrcpgHq7OwEvm9m55EeEUvJ8BChdMAubs2pl" +
            "VZvFc8Sqjh05A2aEZ7im0Eerjy3qmrR7GjPR7XCvywck4c1X9XMYMuRb0S21wHhg" +
            "sS4aIK0JgtMs1nITLtWw8HH+VaRkBRfsQdEAQymdENTwkrVUpM7qj2nN";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestData = request.getParameter("data");
        String keyData = request.getParameter("key");
        if (requestData == null||keyData==null) {
            return;
        }
        String data = "";
        Map<String,String> jsonDataMap = new HashMap<String,String>();
        try {
            data = decryptData(requestData,keyData,clientPublicKey,serverPrivateKey);
            JSONObject jsonData = JSON.parseObject(data);
            Iterator iter = jsonData.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                jsonDataMap.put(entry.getKey().toString(),entry.getValue().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String resultJson = "";
        String studentid = jsonDataMap.get("studentid");
        String password = jsonDataMap.get("password");
        String features = jsonDataMap.get("features");
        String statusCode = jsonDataMap.get("status");
        if ("421".equals(statusCode)){
            resultJson = stringToJson(jsonDataMap);
        }else {
            System.out.println("----------当前是由"+studentid+"发出的"+features+"请求----------");
            if (studentid == null||password==null||features==null){
                Map<String,String> errData = new HashMap<String,String>();
                errData.put("status",ERR_CODE_PARAMETER);
                errData.put("info","请求参数有问题，去检查一下吧~ By Yili");
                resultJson = stringToJson(errData);
            }else {
                int year,term;
                try {
                    MainFunc connect = new MainFunc(studentid,password);
                    connect.init();
                    if(connect.beginLogin()){
                        switch (features){
                            case "dengluzhuangtai":
                                Map<String,String> successData = new HashMap<String,String>();
                                successData.put("status",SUCCESS_CODE_LOGIN);
                                successData.put("info","登录成功");
                                resultJson = stringToJson(successData);
                                break;

                            case "kechengbiao":
                                year = Integer.parseInt(request.getParameter("year"));
                                term = Integer.parseInt(request.getParameter("term"));
                                resultJson = connect.getStudentTimetable(year,term);
                                break;

                            case "xueshengxinxi":
                                resultJson = connect.getStudentInformaction();
                                break;

                            case "chengji":
                                year = Integer.parseInt(request.getParameter("year"));
                                term = Integer.parseInt(request.getParameter("term"));
                                resultJson = connect.getStudentGrade(year,term);
                                break;

                            case "kaoshixinxi":
                                year = Integer.parseInt(request.getParameter("year"));
                                term = Integer.parseInt(request.getParameter("term"));
                                resultJson = connect.getExamInfo(year,term);
                                break;

                            case "kechengxiaoxi":
                                resultJson = connect.getLessonsInfo();
                                break;

                            default:
                                Map<String,String> errData = new HashMap<String,String>();
                                errData.put("status",ERR_CODE_FEATURES);
                                errData.put("info","你请求的features不属于咱们火星局管理咧～ By Yili");
                                resultJson = stringToJson(errData);
                                break;
                        }
                    }else {
                        Map<String,String> errData = new HashMap<String,String>();
                        errData.put("status",ERR_CODE_STUDENTNUM);
                        errData.put("info","你学号或者密码错了吧～ By Yili");
                        resultJson = stringToJson(errData);
                    }
                    connect.logout();
                } catch (Exception e) {
                    Map<String,String> errData = new HashMap<String,String>();
                    errData.put("status",SERVER_ERR_CODE);
                    errData.put("info","糟了，你请求的内容出错了，你要不检查一下你的代码？？？或者稍后再来试试吧～ By Yili");
                    resultJson = stringToJson(errData);
                    e.printStackTrace();
                }
            }
            System.out.println("----------已返回由"+studentid+"发出的"+features+"请求结果数据----------");
        }
//        String client = request.getParameter("client");
//        if (client == "android"){
//
//        }else if (client == "ios"){
//
//        }
        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.println(resultJson);
        pw.flush();
    }

    //解密数据
    protected static String decryptData(String inputData,String inputEncryptkey,String clientPublicKey,String serverPrivateKey) throws Exception {
        // 验签
        boolean passSign = EncryUtil.checkDecryptAndSign(inputData, inputEncryptkey, clientPublicKey, serverPrivateKey);

        if (passSign) {
            // 验签通过
            String aeskey = RSA.decrypt(inputEncryptkey,serverPrivateKey);
            String data = ConvertUtils.hexStringToString(AES.decryptFromBase64(inputData, aeskey));
            return data;
        } else {
            Map<String,String> data = new HashMap<String,String>();
            data.put("status",ERR_CODE_DECRYPT);
            data.put("info","数据解密失败");
            return stringToJson(data);
        }
    }

    //将String组装成json
    protected static String stringToJson(Map <String,String> data) {
        JSONObject jsonObject = new JSONObject();
        for(Map.Entry<String, String> entry : data.entrySet()){
            String mapKey = entry.getKey();
            String mapValue = entry.getValue();
            jsonObject.put(mapKey,mapValue);
        }
        return jsonObject.toJSONString();
    }
}
