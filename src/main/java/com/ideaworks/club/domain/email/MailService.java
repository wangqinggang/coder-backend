package com.ideaworks.club.domain.email;

import javax.mail.MessagingException;

/**
 * 邮件服务,发件人默认 www_ideaworks_club@163.com
 * @author 王庆港
 * @version 1.0.0
*/
public interface MailService {
	/**
	 * 发送普通的文字消息
	 * @param subject  邮件主题
	 * @param to 收件人
	 * @param msg 邮件内容
	 */
	 public void sendMsg(String subject , String to, String msg);
	 
	 /**
	  * 发送模板消息，使用 thymleaf 设置模板
	  * @param subject
	  * @param to
	  * @param msg
	 * @throws MessagingException 
	  */
	 public void sendTemplate(String subject, String to,String msg) throws MessagingException;
}
