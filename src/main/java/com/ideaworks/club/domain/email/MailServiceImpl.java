package com.ideaworks.club.domain.email;

import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * 邮件服务的实现类
 * @author 王庆港
 * @version 1.0.0
*/
@Service("MailService")
public class MailServiceImpl implements MailService {
	@Autowired
    JavaMailSender javaMailSender;
	
	@Autowired
	TemplateEngine templateEngine;

	@Override
	public void sendMsg(String subject,   String to, String msg) {
		SimpleMailMessage mail = new SimpleMailMessage();    //构建一个邮件对象
		mail.setSubject(subject); // 设置邮件主题
//		mail.setFrom(from); // 设置邮箱发送者
		mail.setFrom("www_ideaworks_club@163.com");
		mail.setTo(to); // 设置邮件接收者，可以有多个接收者
		mail.setSentDate(new Date());    // 设置邮件发送日期
		mail.setText(msg);   // 设置邮件的正文
        javaMailSender.send(mail);
	}

	@Override
	public void sendTemplate(String subject, String to, String msg) throws MessagingException {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper helper;
		helper = new MimeMessageHelper(mimeMessage, true);
		helper.setFrom("www_ideaworks_club@163.com");
		helper.setTo(to);
		helper.setSubject(subject);
		
		// 利用 Thymeleaf 模板构建 html 文本
		Context ctx = new Context();
		ctx.setVariable("title", subject);
		ctx.setVariable("content",msg);
		String content = templateEngine.process("mail", ctx);
		helper.setText(content, true);
		javaMailSender.send(mimeMessage);
		
	}

}
