package com.qihoo360os.common;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import redis.clients.jedis.Jedis;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Collections;
import java.util.Properties;

/**
 * Created by chendonglin on 2017/6/15.
 */

@Configuration
public class DbAndEmailConfiguration {

    @Bean("mysqlDataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource mysqlDataSource(){
        return DataSourceBuilder.create().build();
    }



    @Value("${redis.host}")
    private String host;
    @Value("${redis.port}")
    private String port;
    @Value("${email.to}")
    private String to;

    @Bean
    public Jedis redisDataSource(){
        return new Jedis(host,Integer.valueOf(port));
    }

    @Bean
    public JdbcTemplate secondJdbcTemplate(@Qualifier("mysqlDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }


    @Bean
    public JavaMailSender mailSender() throws IOException {
        final JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        // Basic mail sender configuration, based on emailconfig.properties
        mailSender.setHost("localhost");
        mailSender.setPort(25);
        mailSender.setUsername("i-chendonglin");
        mailSender.setPassword("chendonglin");
         Properties javaMailProperties = new Properties();
        javaMailProperties=System.getProperties();
        mailSender.setJavaMailProperties(javaMailProperties);
        return mailSender;
    }
    @Bean
    @Primary
    public TemplateEngine emailTemplateEngine() {
        final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(htmlTemplateResolver());
        templateEngine.setTemplateResolver(htmlTemplateResolver());
        return templateEngine;
    }

    public ITemplateResolver htmlTemplateResolver() {
        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setOrder(Integer.valueOf(1));
//        templateResolver.setResolvablePatterns(Collections.singleton("templates/*"));
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML5");
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setCacheable(false);
        return templateResolver;
    }
}
