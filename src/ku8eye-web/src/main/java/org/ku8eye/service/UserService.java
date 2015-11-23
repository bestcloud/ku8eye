package org.ku8eye.service;

import org.ku8eye.domain.User;
import org.ku8eye.mapping.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * @author jackchen
 *
 */
@Service
public class UserService {
	
	@Autowired
	private UserMapper userDao;
	/**
	 * find User by userid
	 * @param pUserId
	 * @return User
	 */
	public User getUserByUserId(String userId){
		return userDao.selectByPrimaryKey(userId);
	}
}
