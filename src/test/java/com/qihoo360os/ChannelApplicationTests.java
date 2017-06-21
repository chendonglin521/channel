package com.qihoo360os;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.serializer.SerializationUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.sql.RowSet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ChannelApplicationTests {

	@Autowired
	private  JdbcTemplate jdbcTemplate;

	@Autowired
	private JedisPool jedis;

	@Test
	public void contextLoads() {
//		String sql2="\tSELECT DISTINCT s.`tacs_channel` channel ,h.real_pkgname pkgname FROM\n" +
//				"\t\t  `tp_all_channel_summery` s,\n" +
//				"\t\t  `tp_channel_plans` p ,\n" +
//				"\t\t  `tp_real_hoaxes` h\n" +
//				"\t\tWHERE s.`tacs_channel` = p.`tcr_channel` AND p.`tcr_channel` = h.real_channel\n" +
//				"\t\tAND s.`tacs_total_antihoax`>500 AND p.`tcr_done`=0\n" +
//				"\t\tORDER BY s.`tacs_channel` ";
//
//		List<Map<String,String>> channelAndPkg=jdbcTemplate.query(sql2,(rs,num)->{
//			HashMap map=new HashMap<String,String>();
//			map.put(rs.getString("channel"),rs.getString("pkgname"));
//			return map;
//		});
//
//		// channel + pkgs
//		Map<String,List<String>>channelAndPkgs=new HashMap<>();
//		channelAndPkg.forEach( map->{
//			map.forEach((k,v)->{
//				if(!channelAndPkgs.containsKey(k)){
//					List<String> list=new LinkedList<>();
//					list.add(v);
//					channelAndPkgs.put(k,list);
//				}else{
//					channelAndPkgs.get(k).add(v);
//				}
//			});
//		});
////		初始化缓存
////		channelAndPkgs.forEach((k,v)->{
////			jedis.set(k,v.toString());
////			List<String> list=Arrays.asList(jedis.get(k).substring(1,jedis.get(k).length()-1).split(", "));
////		});
//		Map<String,List<String>> newConfig=new HashMap<>();
////		定时与缓存比较发起请求
//		channelAndPkgs.forEach((k,v)->{
//			String value=jedis.get(k);
//			List<String> tmp=new LinkedList(v);
//			if(!StringUtils.isEmpty(value)){
//				List<String> list=Arrays.asList(value.substring(1,jedis.get(k).length()-1).split(", "));
//				//记录之多不少
//				tmp.removeAll(list);
//				if(tmp.size()!=0){
//					jedis.set(k,v.toString());
//					newConfig.put(k,tmp);
//				}
//			}else{
//				jedis.set(k,v.toString());
//				newConfig.put(k,v);
//			}
//		});
//		return  newConfig;
	}

//	public static void main(String[] args) throws ExecutionException, InterruptedException {
//		ScheduledExecutorService service= Executors.newSingleThreadScheduledExecutor();
//		Callable<Map<String,List<String>>> callable=new Callable() {
//			@Override
//			public Object call() throws Exception {
//				return new ChannelApplicationTests().contextLoads();
//			}
//		};
//		ScheduledFuture future=service.schedule(callable,30, TimeUnit.SECONDS);
//		Map<String,List<String>> resultMap= (Map<String, List<String>>) future.get();
//	}
}
