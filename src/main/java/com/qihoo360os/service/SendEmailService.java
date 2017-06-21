package com.qihoo360os.service;

import javax.mail.MessagingException;

/**
 * Created by i-chendonglin on 2017/6/20.
 */
public interface SendEmailService {

    void sendEmail();
    void sendHtmlEmail(String channel) throws MessagingException;

}
