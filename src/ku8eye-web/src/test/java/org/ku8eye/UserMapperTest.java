package org.ku8eye;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ku8eye.domain.User;
import org.ku8eye.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { App.class })
@TransactionConfiguration(defaultRollback = true)
public class UserMapperTest {
	@Autowired()
	private UserMapper userDao;

	@Test
	public void findAllUsers() {
		List<User> userLists = userDao.findAll();
		Assert.assertEquals(1, userLists.size());
		Assert.assertEquals("guest", userLists.get(0).getUserId());
	}

	@Test
	@Transactional
	public void insertUser() {
		User user = new User();
		user.setUserId("guest2");
		user.setAlias("tester");
		user.setPassword("password");
		List<User> userListsSaveBefore = userDao.findAll();
		Assert.assertEquals(1, userListsSaveBefore.size());
		userDao.save(user);
		List<User> userListsSaveAfter = userDao.findAll();
		Assert.assertEquals(2, userListsSaveAfter.size());
	}
}