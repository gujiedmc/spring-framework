package com.gujiedmc.demo;


import com.gujiedmc.demo.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

import java.io.IOException;
import java.text.ParseException;

/**
 * @author admin
 * @date 2019/12/19
 */
public class IocApplication {

	public static void main(String[] args) throws IOException, ParseException {

		ApplicationContext context = new ClassPathXmlApplicationContext("application.xml");
//		ApplicationContext context = new XmlWebApplicationContext();

		UserService userService = (UserService) context.getBean("userService");
		System.out.println(userService.getName());


		System.in.read();
	}
}
