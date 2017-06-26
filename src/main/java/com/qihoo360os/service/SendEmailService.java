package com.qihoo360os.service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by i-chendonglin on 2017/6/20.
 */
public interface SendEmailService {

    void sendEmail() throws MessagingException;
    void sendHtmlEmail(String channel) throws MessagingException, Exception;


}
