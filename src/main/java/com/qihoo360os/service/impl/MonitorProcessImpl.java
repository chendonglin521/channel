package com.qihoo360os.service.impl;

import com.qihoo360os.service.MonitorProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;

import java.util.*;

/**
 * Created by i-chendonglin on 2017/6/16.
 */
@Service
public class MonitorProcessImpl implements MonitorProcess {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Jedis jedis;

    @Override
    public Map<String, List<String>> getUpdates() {
        String sql2="\tSELECT DISTINCT s.`tacs_channel` channel ,h.real_pkgname pkgname FROM\n" +
                "\t\t  `tp_all_channel_summery` s,\n" +
                "\t\t  `tp_channel_plans` p ,\n" +
                "\t\t  `tp_real_hoaxes` h\n" +
                "\t\tWHERE s.`tacs_channel` = p.`tcr_channel` AND p.`tcr_channel` = h.real_channel\n" +
                "\t\tAND s.`tacs_total_antihoax`>500 AND p.`tcr_done`=0\n" +
                "\t\tORDER BY s.`tacs_channel` ";

        List<Map<String,String>> channelAndPkg=jdbcTemplate.query(sql2,(rs,num)->{
            HashMap map=new HashMap<String,String>();
            map.put(rs.getString("channel"),rs.getString("pkgname"));
            return map;
        });

        // channel + pkgs
        Map<String,List<String>>channelAndPkgs=new HashMap<>();
        channelAndPkg.forEach( map->{
            map.forEach((k,v)->{
                if(!channelAndPkgs.containsKey(k)){
                    List<String> list=new LinkedList<>();
                    list.add(v);
                    channelAndPkgs.put(k,list);
                }else{
                    channelAndPkgs.get(k).add(v);
                }
            });
        });
//		初始化缓存
//		channelAndPkgs.forEach((k,v)->{
//			jedis.set(k,v.toString());
//			List<String> list=Arrays.asList(jedis.get(k).substring(1,jedis.get(k).length()-1).split(", "));
//		});
        Map<String,List<String>> newConfig=new HashMap<>();
//		定时与缓存比较发起请求
        channelAndPkgs.forEach((k,v)->{
            String value=jedis.get(k);
            List<String> tmp=new LinkedList(v);
            if(!StringUtils.isEmpty(value)){
                List<String> list= Arrays.asList(value.substring(1,jedis.get(k).length()-1).split(", "));
                //记录之多不少
                tmp.removeAll(list);
                if(tmp.size()!=0){
                    jedis.set(k,v.toString());
                    newConfig.put(k,tmp);
                }
            }else{
                jedis.set(k,v.toString());
                newConfig.put(k,v);
            }
        });
        return  newConfig;
    }
}
