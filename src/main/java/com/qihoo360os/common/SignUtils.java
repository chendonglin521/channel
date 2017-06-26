package com.qihoo360os.common;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by i-chendonglin on 2017/6/26.
 */
public class SignUtils {

    public static String getSign(String channel,String secretKey) throws NoSuchAlgorithmException, InvalidKeyException {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String body = sdf.format(date);
        String sign = "channel=" + channel + body;
        Mac macSha256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec macKey = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
        macSha256.init(macKey);
        sign = bytesToHexString(macSha256.doFinal(sign.getBytes()));
        return sign;
    }
    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}
