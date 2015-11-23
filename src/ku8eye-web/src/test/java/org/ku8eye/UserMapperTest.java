package org.ku8eye;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ku8eye.domain.User;
import org.ku8eye.mapping.UserMapper;
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
		List<User> userLists = userDao.selectAll();
		Assert.assertEquals(true, userLists.size()>0);
		//Assert.assertEquals("guest", userLists.get(0).getUserId());
	}

	//@Test
	@Transactional
	public void insertUser() {
		
		List<User> userListsSaveBefore = userDao.selectAll();
		int size=userListsSaveBefore.size();
		User user = new User();
		user.setUserId("guest");
		user.setPassword("123456");
		user.setAlias("Mr li");
		userDao.insert(user);
		List<User> userListsSaveAfter = userDao.selectAll();
		Assert.assertEquals(size+1, userListsSaveAfter.size());
	}
}