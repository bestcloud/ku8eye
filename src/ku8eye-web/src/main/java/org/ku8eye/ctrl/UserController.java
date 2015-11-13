package org.ku8eye.ctrl;

import org.ku8eye.domain.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@EnableAutoConfiguration  
@RestController
@RequestMapping("/user")
public class UserController {
	@RequestMapping("/{id}")
	public User view(@PathVariable("id") Long id) {
		User user = new User();
		user.setUserId("guest" + id);
		user.setAlias("zhang");
		return user;
	}
}