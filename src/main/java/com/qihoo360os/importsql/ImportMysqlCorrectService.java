package com.qihoo360os.importsql;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by i-chendonglin on 2017/6/21.
 */
@Component
public class ImportMysqlCorrectService {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    //@Scheduled(fixedRate = 30000)
    public void importSql(){
        System.out.println(jdbcTemplate);
        Pattern pattern= Pattern.compile("ver_(.*)&");
        Pattern pattern2= Pattern.compile("\\d{2}:\\d{2}:\\d{2}");
        try {
            LineIterator iterator = FileUtils.lineIterator(new File("E:\\1.dat"));
            while (iterator.hasNext()) {
                String line=iterator.nextLine();
                String[] rows=line.split("\t");

                  if(rows.length==4&&!StringUtils.isEmpty(rows[3])){
                    Matcher matcher=pattern2.matcher(rows[3]);
                    if(!matcher.find()){
                        rows[3]=null;
                    }
                }

                if(rows.length==4&&!StringUtils.isEmpty(rows[0])&&!StringUtils.isEmpty(rows[3])&&!rows[1].equals("UNKNOWN")){

                    if(rows[1].startsWith("country")){
                        Matcher matcher = pattern.matcher(rows[1]);
                        if(matcher.find()){
                            rows[1]=matcher.group(1);
                        }
                    }
                    String insertSql="insert into tp_perfix_version(tpv_channel,tpv_osversion,tpv_versioncode,tpv_versionname,thedate) values(?,?,?,?, date_format(now(),'%y-%m-%d'))";
                    System.out.println(Arrays.toString(rows));
                    jdbcTemplate.update(insertSql,rows[0],rows[1],rows[2],rows[3]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
