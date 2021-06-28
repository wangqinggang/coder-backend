package com.ideaworks.club;

import javax.mail.MessagingException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.ideaworks.club.domain.email.MailService;


@RunWith(SpringRunner.class)
@SpringBootTest
public class Ideaworks1ApplicationTests {
	
	@Autowired
	private MailService mailService;

	@Test
	public void testSend() throws MessagingException {
		mailService.sendTemplate("网站注册成功", "599252594@qq.com", "你是傻子么，我发个邮件告诉你");
	}

}
