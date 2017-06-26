package com.qihoo360os.cntroller;

import com.qihoo360os.service.MonitorProcessService;
import com.qihoo360os.service.SendEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.util.List;
import java.util.Map;

/**
 * Created by i-chendonglin on 2017/6/16.
 */
@Component
public class MonitorScheduleTask {

    @Autowired
    private MonitorProcessService monitorProcess;
    @Autowired
    private SendEmailService sendEmailService;

    @Scheduled(fixedRate = 60000)
    public void startMonitor() {
//        Map<String, List<String>> resultMap = monitorProcess.getUpdates();
        Map<String, String> resultMap = monitorProcess.getUpdates();
        //resultMap 不为空时 发送通知邮件
        if(resultMap!=null&&resultMap.size()>0){
            resultMap.forEach((k,v)->{
                try {
                    monitorProcess.postChannelAndPkg(k,v);
                    sendEmailService.sendHtmlEmail(k);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
