package com.qihoo360os.service.impl;

import com.qihoo360os.common.SignUtils;
import com.qihoo360os.service.MonitorProcessService;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.mail.MessagingException;
import java.util.*;


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

    @Value("${serverURL}")
    private String serverURL;
    @Value("${whiteList}")
    private String whiteList;

    private static final Logger logger = LoggerFactory.getLogger(MonitorProcessServiceImpl.class);

    @Override
    public Map<String, String> getUpdates() {
        Jedis jedis = jedisPool.getResource();
        String sql2 ="  SELECT DISTINCT\n" +
                "                  s.`tacs_channel` channel,\n" +
                "                  h.real_pkgname pkgname \n" +
                "                FROM\n" +
                "                  `tp_all_channel_summery` s,\n" +
                "                  `tp_channel_plans` p,\n" +
                "                  `tp_real_hoaxes` h \n" +
                "                WHERE s.`tacs_channel` NOT IN ("+whiteList+")\n" +
                "                  AND s.`tacs_channel` = p.`tcr_channel` \n" +
                "                  AND s.tacs_rule_name=p.tcr_feature    \n" +
                "                  AND p.`tcr_channel` = h.real_channel  \n" +
                "                  AND s.`tacs_total_antihoax` > 500 \n" +
                "                  AND p.`tcr_done` != 1 \n" +
                "               \n" +
                "                ORDER BY s.`tacs_channel`";

//      list< map<channel,pkg>> from mysql
        List<Map<String, String>> channelAndPkg = jdbcTemplate.query(sql2, (rs, num) -> {
            HashMap map = new HashMap<String, String>();
            map.put(rs.getString("channel"), rs.getString("pkgname"));
            return map;
        });

//         channel + pkgs
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
<<<<<<< HEAD
//        Map< channel,pkgs> 最终新增　channel +pkgs
=======
>>>>>>> 8be4162fc085cb0418a7369a6098cf45a8f454d8
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
<<<<<<< HEAD
        String sign= SignUtils.getSign(channel,secretKey);
=======
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String body = sdf.format(date);
        String sign = "channel=" + channel + body;
        Mac macSha256 = Mac.getInstance("HmacSHA256");
>>>>>>> 8be4162fc085cb0418a7369a6098cf45a8f454d8
        String pkg = pkgs.replaceAll(" ", "");
        HttpPost post = new HttpPost(serverURL+"?sign=" + sign + "&channel=" + channel + "&pkg" + pkg);
        HttpResponse response = httpclient.execute(post);
        HttpEntity entity = response.getEntity();
//       {"data":"DFSL_BLF_T9PG_CM","msg":"正常调用,该接口已经存在，请更换接口名","status":200,"total":0}
        String json = EntityUtils.toString(entity, "utf-8");
        JSONObject jsonObject=JSONObject.fromObject(json);
        String msg=(String) jsonObject.get("msg");
        if(!msg.startsWith("正常调用")){
            //send me
            logger.error(msg);
        }
        logger.info(msg);
    }
}
