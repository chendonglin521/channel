package com.qihoo360os.common;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import redis.clients.jedis.Jedis;

import javax.sql.DataSource;

/**
 * Created by chendonglin on 2017/6/15.
 */

@Configuration
public class DbConfiguration {

    @Bean("mysqlDataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource mysqlDataSource(){
        return DataSourceBuilder.create().build();
    }



    @Bean
    public Jedis redisDataSource(){
        return new Jedis("127.0.0.1",6379);
    }

    @Bean
    public JdbcTemplate secondJdbcTemplate(@Qualifier("mysqlDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}
