package cn.mapotofu.utils;

import cn.mapotofu.utils.aesrsautils.RSA;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TestRSA {
    public static void main(String[] args) throws Exception {
        Map<String,String> rsaKey = new HashMap<String,String>();
        rsaKey = RSA.generateKeyPair();
        for(Map.Entry<String, String> entry : rsaKey.entrySet()){
            String mapKey = entry.getKey();
            String mapValue = entry.getValue();
            System.out.println(mapKey+":"+mapValue);
        }
    }
}
