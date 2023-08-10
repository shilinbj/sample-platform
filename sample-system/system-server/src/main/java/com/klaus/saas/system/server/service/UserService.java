package com.klaus.saas.system.server.service;

import com.klaus.saas.system.model.vo.User;
import com.klaus.saas.system.server.dao.RedisDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * @author Klaus
 * @since 2023/8/10
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

	private final RedisDao redisDao;

	public Mono<User> getByUsernameFromRedis(String username) {
		return redisDao.findUser(username).map(user -> {
			user.setPassword(null);
			return user;
		});
	}


}
