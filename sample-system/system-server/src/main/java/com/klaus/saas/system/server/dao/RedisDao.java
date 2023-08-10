package com.klaus.saas.system.server.dao;

import com.klaus.saas.system.model.vo.User;
import com.klaus.saas.system.server.vo.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static com.klaus.saas.system.server.constants.AuthConstants.*;

/**
 * @author Klaus
 * @since 2023/8/10
 */
@Repository
@Slf4j
@RequiredArgsConstructor
public class RedisDao {

	@Value("${user.redisExpireTime}")
	private long userExpireTime;

	@Value("${token.redisExpireTime}")
	private long tokenExpireTime;

	private final ReactiveStringRedisTemplate stringTemplate;
	private final ReactiveRedisTemplate<String, User> userTemplate;
	private final ReactiveRedisTemplate<String, Resource> resourceTemplate;

	public Mono<Boolean> existUser(String username) {
		return userTemplate.hasKey(USER_PRE + username);
	}

	public Mono<Boolean> saveUser(User user) {
		return userTemplate.opsForValue().set(USER_PRE + user.getUsername(), user, Duration.ofDays(userExpireTime));
//				.doOnSuccess(aBoolean -> log.info("save user to redis, username: {}", user.getUsername()));
	}

	public Mono<User> findUser(String username) {
		return userTemplate.opsForValue().get(USER_PRE + username);
	}

	public Mono<Boolean> saveToken(String username, String token) {
		return stringTemplate.opsForValue().set(BEARER_PREFIX + username, token, Duration.ofMinutes(tokenExpireTime));
//				.doOnSuccess(aBoolean -> log.info("save token to redis, username: {}", username));
	}

	public Mono<String> findToken(String username) {
		return stringTemplate.opsForValue().getAndExpire(BEARER_PREFIX + username, Duration.ofMinutes(tokenExpireTime));
	}

	public Mono<Boolean> deleteToken(String username) {
		return stringTemplate.opsForValue().delete(BEARER_PREFIX + username);
	}

	public Mono<Boolean> saveResource(Resource resource) {
		return resourceTemplate.opsForValue().set(RESOURCE_PRE + resource.getId(), resource);
	}

	public Mono<Resource> findResource(long id) {
		return resourceTemplate.opsForValue().get(RESOURCE_PRE + id);
	}

	public Mono<Long> deleteAllUserResource() {
		return stringTemplate.delete(stringTemplate.keys(AUTH_USER_RESOURCE_PRE + "*"));
	}

	public Mono<Long> deleteUserResource(Long userId) {
		return stringTemplate.delete(AUTH_USER_RESOURCE_PRE + userId);
	}

	public Mono<Long> saveUserResource(Long userId, String resource) {
		return stringTemplate.opsForSet().add(AUTH_USER_RESOURCE_PRE + userId, resource);
	}

	public Mono<Boolean> isUserResource(Long userId, String resource) {
		return stringTemplate.opsForSet().isMember(AUTH_USER_RESOURCE_PRE + userId, resource);
	}

}
