package com.klaus.saas.system.server.service;

import com.klaus.saas.commons.entity.BaseEntity;
import com.klaus.saas.commons.enums.StatusCode;
import com.klaus.saas.commons.model.MyPage;
import com.klaus.saas.commons.model.PagingRequest;
import com.klaus.saas.commons.model.Result;
import com.klaus.saas.system.model.vo.User;
import com.klaus.saas.system.server.context.SessionContext;
import com.klaus.saas.system.server.dao.RedisDao;
import com.klaus.saas.system.server.dao.UserDao;
import com.klaus.saas.system.server.entity.UserEntity;
import com.klaus.saas.system.server.vo.ModifyPasswordRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
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

	private final UserDao userDao;
	private final RedisDao redisDao;
	private final PasswordEncoder passwordEncoder;

	public Mono<User> getByUsernameFromRedis(String username) {
		return redisDao.findUser(username).map(user -> {
			user.setPassword(null);
			return user;
		});
	}

	public Mono<MyPage<User>> getUserPage(PagingRequest<User> param) {
		MyPage<User> page = param.getMyPage();
		return Mono.zip(
				userDao.findCount(param.getParam()),
				userDao.findPage(param.getParam(), page).map(BaseEntity::<User>toVO).collectList(),
				(count, list) -> page.setList(list).setTotal(count)
		);
	}

	public Mono<Result> saveUser(User user) {
		return Mono.zip(
						SessionContext.userId(),
						// 判断username是否已存在
						userDao.findByUsername(user.getUsername()))
				.flatMap(objects -> {
					UserEntity t2 = objects.getT2();
					if (StringUtils.isNotEmpty(t2.getUsername())) {
						return Mono.just(new Result(-1, "该用户名已存在"));
					} else {
						long currentUserId = objects.getT1();
						UserEntity userEntity = new UserEntity(user);
						userEntity.setPassword(passwordEncoder.encode(user.getPassword()));
						userEntity.setCreatedBy(currentUserId);
						return userDao.save(userEntity).thenReturn(new Result(StatusCode.OK));
					}
				});
	}

	public Mono<Result> modifyPassword(ModifyPasswordRequest request) {
		String username = request.getUsername();
		return redisDao.findUser(username)
				.switchIfEmpty(userDao.findByUsername(username).map(BaseEntity::toVO))
				.flatMap(user -> {
					if (passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
						String encodePassword = passwordEncoder.encode(request.getNewPassword());
						return userDao.modifyPassword(username, encodePassword)
								.then(refreshRedis(username))
								.thenReturn(new Result(StatusCode.OK));
					} else {
						return Mono.just(new Result(-1, "原密码不正确"));
					}
				});
	}

	public Mono<Void> refreshRedis(String username) {
		if (StringUtils.isEmpty(username)) {
			return userDao.findAll()
					.map(BaseEntity::<User>toVO)
					.flatMap(redisDao::saveUser)
					.then();
		} else {
			return userDao.findByUsername(username)
					.map(BaseEntity::<User>toVO)
					.flatMap(redisDao::saveUser)
					.then();
		}
	}

}
