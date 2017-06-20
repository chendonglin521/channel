package com.qihoo360os.service;

import javax.mail.MessagingException;
import java.util.Locale;

/**
 * Created by i-chendonglin on 2017/6/20.
 */
public interface SendEmailService {

    void sendEmail();
    void sendHtmlEmail() throws MessagingException;

}
