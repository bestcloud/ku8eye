package org.ku8eye.mapper;
import org.ku8eye.domain.User;
import java.util.List;

public interface UserMapper{
	void save(User user);

	void update(User user);

	int delete(int id);

	User findById(int id);

	User findByName(String name);

	List<User> findAll();
}