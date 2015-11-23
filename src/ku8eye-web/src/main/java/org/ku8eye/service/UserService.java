package org.ku8eye.service;

import org.ku8eye.domain.User;
import org.ku8eye.mapping.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public User getUserByUserId(String userId){
		return userDao.selectByPrimaryKey(userId);
	}
}
