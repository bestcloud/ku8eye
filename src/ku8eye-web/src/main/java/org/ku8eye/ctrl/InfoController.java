package org.ku8eye.ctrl;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller

public class InfoController {

	@Value("${application.hellowmsg:Hello World}")

	private String message = "Hello World";

	@RequestMapping("/")

	public String index(Map<String, Object> model) {

		model.put("time", new Date());

		model.put("message", this.message);

		return "main.html";

		/*** 当返回index字符串，会自动 路径寻找index.jsp */

	}

	@RequestMapping(value = "sign")

	public ModelAndView example(HttpServletRequest request) {

		return new ModelAndView("login.html");

	}

}