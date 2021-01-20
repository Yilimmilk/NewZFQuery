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
import java.util.Enumeration;
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

    protected static final String clientPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCwzwrcAE4X4+9/cYFEfD7i04Bt" +
            "FKJENBPyq/JMJxY6qR1GLl0izW8v7/uHdJkH6w4vqWvUz2olBj9NXfJbr06vW5IP" +
            "jLm7HgQ0MkxR7pPKuvW0R/5G7+WuR9B0Tpk34p8NVPFOE0MFVHxcLP0k2mpvSlJb" +
            "dGCGa8ZiZPE72qvZXwIDAQAB";

    protected static final String serverPrivateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAO/caHJAsRjcL6a3" +
            "N0dHy91OC2ZWkhdGRtdyYmVX9SNXoYyTdx6f64Y4+QR4cSy0GOlzqdsweHb6tc2g" +
            "H3KL0rMQlcaJCsKBghC/n1xu4xsafy8ehg/xWVsI+uhjJ1SkMtnsIaUkN+REh8Ej" +
            "MTuCQqzzEGYxgUme6oklqD4eEYfdAgMBAAECgYAw3+6AaWIsuKEVDXw8EAsgWwjD" +
            "n5xBFdbVi780+0k+HFsUs++v09JAFVfYa1pUS5ZP63uO6D+Dru5gqeH+izMxWIyj" +
            "FtJVVfwo9DGIgxZ8giOE/5Htp2bCOhs4FCI7lOXptPWv8jbuXc2+z5OdWbPyAj8z" +
            "3GwgdLBrHslAoKMfwQJBAPyXY8vCLUfu2bPur+TMOTTV7I8yxb81bTc7ZU8Wrr4a" +
            "6oq2dR5CRw9mRhmRXRpvRJUdRIkX57v4cx5NLAcQsCUCQQDzGQoRae+qCaGMiyAC" +
            "Ygmk8RajlvnO+9T1Qxfj+uBFEqHqdaMfoXvWT/nXxgnIJpy8jREaF3hWpAc+mOzc" +
            "/69ZAkAyMmPCunQFN5JMD8Mk1PEJbvnz+0MRHKz3rPYVIYzYesDhkCD2QnWcudZ0" +
            "fk7iUfpvJ8HW/Mkwsc8u1mdJgCeBAkEA7Bz34G4KcBcmp8FBd88NGv1nRcEQJXAI" +
            "Kafe/nnfdndgeL+FjuVjyIapXmBlg9etCJ3jbHo7UbvDpZMXDJfzEQJAbeygfbAC" +
            "14kUsyr/WzwrsjJt8RvXWJOkiQrPK7m1EdJXiIlYr6rD0TTxd/0NHaTuVCmhQIg6" +
            "SW4hzU2zDOvHVw==";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        decryptAndGetData(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        decryptAndGetData(request, response);
    }

    protected void decryptAndGetData(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String clientData, flagData, requestData, keyData, studentidData, passwordData, featuresData, resultJson, studentid, password, features, statusCode;
        boolean isFlagTest;
        Map<String, String> jsonDataMap, errData, successData;

        clientData = request.getParameter("client");
        flagData = request.getParameter("flag");
        requestData = request.getParameter("data");
        keyData = request.getParameter("key");
        if (clientData != null && flagData != null) {
            if (flagData.equals("normal")) {
                isFlagTest = false;
                if (requestData == null || keyData == null) return;
            } else if (flagData.equals("test")) {
                isFlagTest = true;
                studentidData = request.getParameter("studentid");
                passwordData = request.getParameter("password");
                featuresData = request.getParameter("features");
                if (studentidData == null || passwordData == null || featuresData == null) return;
            } else {
                return;
            }
        } else {
            return;
        }
        jsonDataMap = new HashMap<String, String>();
        if (!isFlagTest) {
            jsonDataMap = getClientAndDecrypt(clientData, requestData, keyData, clientPublicKey, serverPrivateKey);
        } else {
            Enumeration paramNames = request.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String paramName = (String) paramNames.nextElement();
                String[] paramValues = request.getParameterValues(paramName);
                if (paramValues.length == 1) {
                    String paramValue = paramValues[0];
                    if (paramValue.length() != 0) {
                        //System.out.println("参数：" + paramName + "=" + paramValue);
                        jsonDataMap.put(paramName, paramValue);
                    }
                }
            }
        }
        studentid = jsonDataMap.get("studentid");
        password = jsonDataMap.get("password");
        features = jsonDataMap.get("features");
        statusCode = jsonDataMap.get("status");
        if (statusCode != null) {
            resultJson = stringMapToJson(jsonDataMap);
        } else {
            System.out.println("----------" + flagData + ":" + clientData + ":当前是由" + studentid + "发出的" + features + "请求----------");
            if (studentid == null || password == null || features == null) {
                errData = new HashMap<String, String>();
                errData.put("status", ERR_CODE_PARAMETER);
                errData.put("info", "请求参数有问题，去检查一下吧~ By Yili");
                resultJson = stringMapToJson(errData);
            } else {
                int year, term;
                try {
                    MainFunc connect = new MainFunc(studentid, password);
                    connect.init();
                    if (connect.beginLogin()) {
                        switch (features) {
                            case "dengluzhuangtai":
                                successData = new HashMap<String, String>();
                                successData.put("status", SUCCESS_CODE_LOGIN);
                                successData.put("info", "登录成功");
                                resultJson = stringMapToJson(successData);
                                break;

                            case "kechengbiao":
                                year = Integer.parseInt(jsonDataMap.get("year"));
                                term = Integer.parseInt(jsonDataMap.get("term"));
                                resultJson = connect.getStudentTimetable(year, term);
                                break;

                            case "xueshengxinxi":
                                resultJson = connect.getStudentInformaction();
                                break;

                            case "chengji":
                                year = Integer.parseInt(jsonDataMap.get("year"));
                                term = Integer.parseInt(jsonDataMap.get("term"));
                                resultJson = connect.getStudentGrade(year, term);
                                break;

                            case "kaoshixinxi":
                                year = Integer.parseInt(jsonDataMap.get("year"));
                                term = Integer.parseInt(jsonDataMap.get("term"));
                                resultJson = connect.getExamInfo(year, term);
                                break;

                            case "kechengxiaoxi":
                                resultJson = connect.getLessonsInfo();
                                break;

                            default:
                                errData = new HashMap<String, String>();
                                errData.put("status", ERR_CODE_FEATURES);
                                errData.put("info", "你请求的features不属于咱们火星局管理咧～ By Yili");
                                resultJson = stringMapToJson(errData);
                                break;
                        }
                    } else {
                        errData = new HashMap<String, String>();
                        errData.put("status", ERR_CODE_STUDENTNUM);
                        errData.put("info", "你学号或者密码错了吧～ By Yili");
                        resultJson = stringMapToJson(errData);
                    }
                    connect.logout();
                } catch (Exception e) {
                    errData = new HashMap<String, String>();
                    errData.put("status", SERVER_ERR_CODE);
                    errData.put("info", "糟了，你请求的内容出错了，你要不检查一下你的代码？？？或者稍后再来试试吧～ By Yili");
                    resultJson = stringMapToJson(errData);
                    e.printStackTrace();
                }
            }
            System.out.println("----------已返回由" + studentid + "发出的" + features + "请求结果数据----------\n\n");
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

    //获取client并解密
    protected static Map<String, String> getClientAndDecrypt(String clientData, String requestData, String keyData, String clientPublicKey, String serverPrivateKey) {
        Map<String, String> map = new HashMap<String, String>();
        switch (clientData) {
            case "android":
                try {
                    String data = decryptData(requestData, keyData, clientPublicKey, serverPrivateKey);
                    JSONObject jsonData = JSON.parseObject(data);
                    Iterator iter = jsonData.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry entry = (Map.Entry) iter.next();
                        map.put(entry.getKey().toString(), entry.getValue().toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case "ios":

                break;

            default:
                map.put("status", ERR_CODE_PARAMETER);
                map.put("info", "client参数出错");
                break;
        }
        return map;
    }

    //解密数据
    protected static String decryptData(String inputData, String inputEncryptkey, String clientPublicKey, String serverPrivateKey) throws Exception {
        // 验签
        boolean passSign = EncryUtil.checkDecryptAndSign(inputData, inputEncryptkey, clientPublicKey, serverPrivateKey);

        if (passSign) {
            // 验签通过
            String aeskey = RSA.decrypt(inputEncryptkey, serverPrivateKey);
            String data = ConvertUtils.hexStringToString(AES.decryptFromBase64(inputData, aeskey));
            return data;
        } else {
            Map<String, String> data = new HashMap<String, String>();
            data.put("status", ERR_CODE_DECRYPT);
            data.put("info", "数据解密失败");
            return stringMapToJson(data);
        }
    }

    //将String组装成json
    protected static String stringMapToJson(Map<String, String> data) {
        JSONObject jsonObject = new JSONObject();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            String mapKey = entry.getKey();
            String mapValue = entry.getValue();
            jsonObject.put(mapKey, mapValue);
        }
        return jsonObject.toJSONString();
    }
}
