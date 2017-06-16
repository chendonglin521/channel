package com.qihoo360os;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ChannelApplicationTests {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private Jedis jedis;

	@Test
	public void contextLoads() {
		String key=jedis.get("key");

		jdbcTemplate.execute(" select * from proc");
	}


}
