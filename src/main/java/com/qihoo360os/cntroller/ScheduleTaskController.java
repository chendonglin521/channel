package com.qihoo360os.cntroller;

import com.qihoo360os.entity.EmailTable;
import com.qihoo360os.service.MonitorProcess;
import com.qihoo360os.service.SendEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.util.Date;
import java.util.Locale;

/**
 * Created by i-chendonglin on 2017/6/16.
 */
@Component
public class ScheduleTaskController {

    @Autowired
    private MonitorProcess monitorProcess;
    @Autowired
    private SendEmailService sendEmailService;

    @Scheduled(fixedRate = 60000)
    public void startMonitor() {
//        Map<String, List<String>> resultMap = monitorProcess.getUpdates();
        //resultMap 不为空时 发送通知邮件

        //根据渠道号请求接口，获取email参数
        //根据参数渲染html模板
        try {
            sendEmailService.sendHtmlEmail();
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        //获取模板内容 发送eamil

//        sendEmailService.sendEmail();

//        return "emailContent";
//        System.out.println(resultMap);
    }

}
