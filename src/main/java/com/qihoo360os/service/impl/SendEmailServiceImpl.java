package com.qihoo360os.service.impl;

import com.qihoo360os.entity.EmailTable;
import com.qihoo360os.service.SendEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

/**
 * Created by i-chendonglin on 2017/6/20.
 */
@Service
public class SendEmailServiceImpl implements SendEmailService {

    // 收件人电子邮箱
    @Value("${email.to}")
    private String to;
    // 发件人电子邮箱
    @Value("${email.from}")
    private String from;
    // 指定发送邮件的主机为 localhost
    @Value("${email.host}")
    private String host;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine htmlTemplateEngine;

    @Override
    public void sendEmail() {
        // 获取系统属性
        Properties properties = System.getProperties();
        // 设置邮件服务器
        properties.setProperty("mail.smtp.host", host);
        // 获取默认session对象
        Session session = Session.getDefaultInstance(properties);
        // 创建默认的 MimeMessage 对象
        MimeMessage message = new MimeMessage(session);
        // Set From: 头部头字段
        try {
            message.setFrom(new InternetAddress(from));
            // Set To: 头部头字段
            message.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(to));

            // Set Subject: 头部头字段
            message.setSubject("测试申请（dzdz6760002507_filemanager）");
            StringBuffer htmlEmail = new StringBuffer();
            ClassLoader classLoader = getClass().getClassLoader();
            ClassPathResource classPathResource = new ClassPathResource("templates/emailContent.html");
            String line; // 用来保存每行读取的内容
            BufferedReader reader = new BufferedReader(new InputStreamReader(classPathResource.getInputStream()));
            line = reader.readLine(); // 读取第一行
            while (line != null) { // 如果 line 为空说明读完了
                htmlEmail.append(line); // 将读到的内容添加到 buffer 中
                line = reader.readLine(); // 读取下一行
            }
//            reader.close();
            // 设置消息体
            message.setContent(htmlEmail.toString(), "text/html;charset=utf-8");
            // 发送消息
            Transport.send(message);
            System.out.println("Sent message successfully....");
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendHtmlEmail() throws MessagingException {
        // Prepare the evaluation context
        Locale locale=Locale.CHINA;
        final Context ctx = new Context(locale);

        EmailTable table=new EmailTable();
        table.setTableHead("远程恢复_测试申请表");
        table.setProgramName("DZ_DZ_DZ6735R6_CN");
        table.setPhoneVersion("dzdz6760002507");
        table.setFlashVersion("量产版本即可，如6.0.054.p1.161201040158.dz_dz_dz6735r6_cn");
        table.setTestDate(new Date());
        table.setRdLeader("沈贺");
        table.setPushWay("emmcid/cpuid");
        table.setFlashLink("http://10.100.14.13/projects/antithief/wiki/Flash_recovery");
        table.setConfLink("http://manage.online.test.os.qkcorp.net/conf");
        table.setRescovery(" dzdz6760002507_filemanager");
        table.setRelation("无");
        table.setSuggest("1.验证配置的恢复路径是否为预装应用路径（没有填写路径，看是否支持恢复,不可以请帮忙提供测试机器的预装应用路径及包名）\n" +
                "2.预装应用是否恢复成功\n" +
                "3.请反馈桌面是否重置到最初版本，即桌面reset是否有效\n" +
                "4.请反馈从点击开始恢复到恢复完成所花费的时间\n" +
                "5.当弹出提示框时，界面是否有取消按钮，请给出结果\n");
        ctx.setVariable("email", table);

        final MimeMessage mimeMessage = mailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
        message.setSubject(" 测试申请");
        message.setFrom(from);
        message.setTo(to);
        final String htmlContent = this.htmlTemplateEngine.process("emailContent", ctx);
        message.setText(htmlContent, true /* isHtml */);
        // Send email
        this.mailSender.send(mimeMessage);
    }
}
