package com.qihoo360os.service;

import javax.mail.MessagingException;
import java.util.List;
import java.util.Map;

/**
 * Created by i-chendonglin on 2017/6/16.
 */
public interface MonitorProcessService {

//     Map<String,List<String>> getUpdates() ;
     Map<String,String> getUpdates() ;
     void postChannelAndPkg(String channel, String pkgs)throws MessagingException, Exception;
}
