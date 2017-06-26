package com.qihoo360os.service.impl;

import com.qihoo360os.common.SignUtils;
import com.qihoo360os.entity.EmailTable;
import com.qihoo360os.service.SendEmailService;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Calendar;
import java.util.Locale;

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
    // 指定抄送人
    @Value("${email.cc}")
    private String[] cc;
    @Value("${email.monitor}")
    private String monitor;
    @Value("${sayHi}")
    private String sayHi;
    @Value("${secretKey}")
    private String secretKey;
    @Value("${serverURL}")
    private String serverURL;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine htmlTemplateEngine;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SendEmailServiceImpl.class);

    @Override
    public void sendEmail() throws MessagingException {
        //获取模板内容 发送eamil
        Locale locale = Locale.CHINA;
        final Context ctx = new Context(locale);
        final MimeMessage mimeMessage = mailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
        message.setSubject("服务器请求返回异常");
        message.setFrom(from);
        message.setTo(monitor);
        message.setText("服务器 msg 异常邮件", true /* isHtml */);
        // Send email
        this.mailSender.send(mimeMessage);
        logger.info("发送服务器异常邮件" );
    }


    /**
     *
     1.接口 /autoconfig/registerchannel?sign=xxx&channel=xxx
     2.说明
     sign 值 等于 query_str = "channel=xxx" + data（年月日）
     3.sign 签名 结合key生成签名
     sign = hash_hmac("sha256", query_str + body, key);
     * @param channel
     * @throws MessagingException
     * @throws Exception
     */
    @Override
    public void sendHtmlEmail(String channel) throws MessagingException, Exception {
        //根据渠道号请求接口，获取email参数
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String sign= SignUtils.getSign(channel,secretKey);
        HttpPost post = new HttpPost(serverURL+"?sign="+sign+"&channel="+channel);
        HttpResponse response = httpclient.execute(post);
        HttpEntity entity = response.getEntity();
//       {"data":"DFSL_BLF_T9PG_CM","msg":"正常调用,该接口已经存在，请更换接口名","status":200,"total":0}
        String json= EntityUtils.toString(entity, "utf-8");
        JSONObject jsonObject=JSONObject.fromObject(json);
        String msg=(String) jsonObject.get("msg");
        if(!msg.startsWith("正常调用")){
            //send me
            logger.error(msg);
        }
        logger.info(msg);
        String programName=(String) jsonObject.get("data");
        //根据参数渲染html模板
        //获取模板内容 发送eamil
        Locale locale = Locale.CHINA;
        final Context ctx = new Context(locale);
        EmailTable table = new EmailTable();
        table.setTableHead("远程恢复_测试申请表");
        table.setProgramName(programName);
        table.setPhoneVersion(channel);
        table.setFlashVersion("");
        table.setTestDate(Calendar.getInstance());
        table.setRdLeader("沈贺");
        table.setPushWay("emmcid/cpuid");
        table.setFlashLink("http://10.100.14.13/projects/antithief/wiki/Flash_recovery");
        table.setConfLink("http://manage.online.test.os.qkcorp.net/conf");
        table.setRescovery(channel + "_new");
        table.setRelation("无");
        table.setSuggest("1.验证配置的恢复路径是否为预装应用路径（没有填写路径，看是否支持恢复,不可以请帮忙提供测试机器的预装应用路径及包名）\n" +
                "2.预装应用是否恢复成功\n" +
                "3.请反馈桌面是否重置到最初版本，即桌面reset是否有效\n" +
                "4.请反馈从点击开始恢复到恢复完成所花费的时间\n" +
                "5.当弹出提示框时，界面是否有取消按钮，请给出结果\n");
        ctx.setVariable("email", table);
        ctx.setVariable("sayHi", sayHi);

        final MimeMessage mimeMessage = mailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
        message.setSubject(" 测试申请(" + channel + "_new)");
        message.setFrom(from);
        message.setTo(to);
        message.setCc(cc);
        final String htmlContent = this.htmlTemplateEngine.process("emailContent", ctx);
        String content = htmlContent.replaceAll("\\?", "<br>");
        message.setText(content, true /* isHtml */);
        // Send email
        this.mailSender.send(mimeMessage);
        logger.info("发送邮件成功，channel： "+channel );
    }
}
