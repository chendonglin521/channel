package com.qihoo360os.service.impl;

import com.qihoo360os.service.MonitorProcessService;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.qihoo360os.service.impl.SendEmailServiceImpl.bytesToHexString;

/**
 * Created by i-chendonglin on 2017/6/16.
 */
@Service
public class MonitorProcessServiceImpl implements MonitorProcessService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JedisPool jedisPool;

    @Value("${secretKey}")
    private String secretKey;

    //    @Override
//    public Map<String, List<String>> getUpdates() {
//        Jedis jedis=jedisPool.getResource();
//        String sql2="SELECT DISTINCT \n" +
//                "  s.`tacs_channel` channel,\n" +
//                "  h.real_pkgname pkgname \n" +
//                "FROM\n" +
//                "  `tp_all_channel_summery` s,\n" +
//                "  `tp_channel_plans` p,\n" +
//                "  `tp_real_hoaxes` h \n" +
//                "WHERE s.`tacs_channel` = p.`tcr_channel` \n" +
//                "  AND p.`tcr_channel` = h.real_channel \n" +
//                "  AND s.`tacs_total_antihoax` > 500 \n" +
//                "  AND p.`tcr_done` = 0 \n" +
//                "ORDER BY s.`tacs_channel` ";
//
//        List<Map<String,String>> channelAndPkg=jdbcTemplate.query(sql2,(rs,num)->{
//            HashMap map=new HashMap<String,String>();
//            map.put(rs.getString("channel"),rs.getString("pkgname"));
//            return map;
//        });
//
//        // channel + pkgs
//        Map<String,List<String>>channelAndPkgs=new HashMap<>();
//        channelAndPkg.forEach( map->{
//            map.forEach((k,v)->{
//                if(!channelAndPkgs.containsKey(k)){
//                    List<String> list=new LinkedList<>();
//                    list.add(v);
//                    channelAndPkgs.put(k,list);
//                }else{
//                    channelAndPkgs.get(k).add(v);
//                }
//            });
//        });
////		初始化缓存
////		channelAndPkgs.forEach((k,v)->{
////			jedis.set(k,v.toString());
////			List<String> list=Arrays.asList(jedis.get(k).substring(1,jedis.get(k).length()-1).split(", "));
////		});
//        Map<String,List<String>> newConfig=new HashMap<>();
////		定时与缓存比较发起请求
//        channelAndPkgs.forEach((k,v)->{
//            String value=jedis.get(k);
//            List<String> tmp=new LinkedList(v);
//            if(!StringUtils.isEmpty(value)){
//                List<String> list= Arrays.asList(value.substring(1,jedis.get(k).length()-1).split(", "));
//                //记录之多不少
//                tmp.removeAll(list);
//                if(tmp.size()!=0){
//                    jedis.set(k,v.toString());
//                    newConfig.put(k,tmp);
//                }
//            }else{
//                jedis.set(k,v.toString());
//                newConfig.put(k,v);
//            }
//        });
//        return  newConfig;
//    }
    @Override
    public Map<String, String> getUpdates() {
        Jedis jedis = jedisPool.getResource();
        String sql2 = "SELECT DISTINCT \n" +
                "  s.`tacs_channel` channel,\n" +
                "  h.real_pkgname pkgname \n" +
                "FROM\n" +
                "  `tp_all_channel_summery` s,\n" +
                "  `tp_channel_plans` p,\n" +
                "  `tp_real_hoaxes` h \n" +
                "WHERE s.`tacs_channel` = p.`tcr_channel` \n" +
                "  AND p.`tcr_channel` = h.real_channel \n" +
                "  AND s.`tacs_total_antihoax` > 500 \n" +
                "  AND p.`tcr_done` = 0 \n" +
                "ORDER BY s.`tacs_channel` ";

        List<Map<String, String>> channelAndPkg = jdbcTemplate.query(sql2, (rs, num) -> {
            HashMap map = new HashMap<String, String>();
            map.put(rs.getString("channel"), rs.getString("pkgname"));
            return map;
        });

        // channel + pkgs
        Map<String, List<String>> channelAndPkgs = new HashMap<>();
        channelAndPkg.forEach(map -> {
            map.forEach((k, v) -> {
                if (!channelAndPkgs.containsKey(k)) {
                    List<String> list = new LinkedList<>();
                    list.add(v);
                    channelAndPkgs.put(k, list);
                } else {
                    channelAndPkgs.get(k).add(v);
                }
            });
        });
        Map<String, String> newConfig = new HashMap<>();
//		定时与缓存比较发起请求
        channelAndPkgs.forEach((k, v) -> {
            String value = jedis.get(k);
            List<String> tmp = new LinkedList(v);
            if (!StringUtils.isEmpty(value)) {
                List<String> list = Arrays.asList(value.substring(1, jedis.get(k).length() - 1).split(", "));
                //记录之多不少
                tmp.removeAll(list);
                if (tmp.size() != 0) {
                    jedis.set(k, v.toString());
                    newConfig.put(k, tmp.toString().substring(1, tmp.toString().length() - 1));
                }
            } else {
                jedis.set(k, v.toString());
                newConfig.put(k, tmp.toString().substring(1, tmp.toString().length() - 1));
            }
        });
        return newConfig;
    }

    @Override
    public void postChannelAndPkg(String channel, String pkgs) throws MessagingException, Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String body = sdf.format(date);
        String sign = "channel=" + channel + body;
        Mac macSha256 = Mac.getInstance("HmacSHA256");
        String pkg = pkgs.replaceAll(" ", "");
        SecretKeySpec macKey = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
        macSha256.init(macKey);
        sign = bytesToHexString(macSha256.doFinal(sign.getBytes()));
        HttpPost post = new HttpPost("http://10.18.49.151:8080/autoconfig/registerchannel?sign=" + sign + "&channel=" + channel + "&pkg" + pkg);
        HttpResponse response = httpclient.execute(post);
        HttpEntity entity = response.getEntity();
//       {"data":"DFSL_BLF_T9PG_CM","msg":"正常调用,该接口已经存在，请更换接口名","status":200,"total":0}
        String json = EntityUtils.toString(entity, "utf-8");
        System.out.println(json);
        System.out.println("监测到数据已提交");
    }
}
