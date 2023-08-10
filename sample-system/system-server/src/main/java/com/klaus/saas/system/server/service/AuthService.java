package com.klaus.saas.system.server.service;

import com.klaus.saas.commons.enums.StatusCode;
import com.klaus.saas.commons.model.DataResult;
import com.klaus.saas.system.model.constants.SystemConstants;
import com.klaus.saas.system.model.vo.User;
import com.klaus.saas.system.server.constants.AuthConstants;
import com.klaus.saas.system.server.context.SessionContext;
import com.klaus.saas.system.server.dao.*;
import com.klaus.saas.system.server.entity.ResourceEntity;
import com.klaus.saas.system.server.entity.RoleEntity;
import com.klaus.saas.system.server.entity.UserEntity;
import com.klaus.saas.system.server.utils.JwtUtils;
import com.klaus.saas.system.server.vo.Resource;
import com.klaus.saas.system.server.vo.Role;
import com.klaus.saas.system.server.vo.RoleResource;
import com.klaus.saas.system.server.vo.UserRole;
import com.klaus.saas.system.server.vo.request.LoginRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.klaus.saas.commons.enums.StatusCode.*;

/**
 * @author Klaus
 * @since 2023/8/10
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

	private final RedisDao redisDao;
	private final AuthDao authDao;
	private final UserDao userDao;
	private final ResourceDao resourceDao;
	private final RoleDao roleDao;

	private final PasswordEncoder passwordEncoder;

	/**
	 * Login.
	 *
	 * @param loginRequest username and password
	 * @return JwtToken
	 */
	public Mono<DataResult<String>> login(LoginRequest loginRequest) {
		String username = loginRequest.getUsername();
		// Find user from redis.
		return redisDao.findUser(username)
				// If redis doesn't exist the user, then find from mysql.
				.switchIfEmpty(userDao.findByUsername(username)
						.map(UserEntity::<User>toVO)
						// save user to redis
						.flatMap(user -> redisDao.saveUser(user).thenReturn(user)))
				.flatMap(user -> {
					// Verify the password and user status
					if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
						return Mono.just(new DataResult<>(BAD_PASSWORD));
					} else if (SystemConstants.USER_STOPPED.equals(user.getStatus())) {
						return Mono.just(new DataResult<>(USER_STATE_STOPPED));
					} else if(SystemConstants.USER_LOCKED.equals(user.getStatus())) {
						return Mono.just(new DataResult<>(USER_STATE_LOCKED));
					}
					String token = JwtUtils.generateToken(username);
					// save token to redis
					return redisDao.saveToken(username, token).thenReturn(new DataResult<>(token));
				});
	}

	/**
	 * Logout.
	 *
	 * @return
	 */
	public Mono<Boolean> logout() {
		return SessionContext.username().flatMap(redisDao::deleteToken);
	}

	/**
	 * 认证和鉴权
	 * 1. 校验token合法性, false -> return UN_AUTH
	 * 2. true -> 比对redis中token, false -> return SESSION_TIMEOUT
	 * 3. true -> 判断该用户是否拥有当前请求url权限, false -> return FORBIDDEN
	 * 4. true -> return username
	 *
	 * @param token
	 * @param url
	 * @return
	 */
	public Mono<DataResult<String>> authentication(String token, String url) {
		boolean tokenValid = JwtUtils.verify(token);
		if (tokenValid) {
			String username = JwtUtils.getClaim(token, AuthConstants.CLAIM_USERNAME);
			return redisDao.findToken(username)
					.flatMap(t -> {
						if (t.equals(token)) {
							return redisDao.findUser(username)
									.flatMap(user -> redisDao.isUserResource(user.getId(), url))
									.map(isPass -> isPass ? new DataResult<>(username) : new DataResult<String>(StatusCode.FORBIDDEN));
						} else {
							return Mono.just(new DataResult<String>(StatusCode.SESSION_TIMEOUT));
						}
					})
					.defaultIfEmpty(new DataResult<>(StatusCode.SESSION_TIMEOUT));
		}
		return Mono.just(new DataResult<>(StatusCode.UN_AUTH));
	}

	public Mono<Resource> saveResource(Resource resource) {
		return SessionContext.userId()
				.flatMap(userId -> {
					ResourceEntity entity = new ResourceEntity(resource);
					entity.setCreatedBy(userId);
					return resourceDao.save(entity).map(ResourceEntity::toVO);
				});
	}

	public Mono<Void> modifyResource(Resource resource) {
		return resourceDao.modify(resource)
				.then(refreshResource(resource.getId()));
	}

	public Mono<Void> deleteResource(long id) {
		return resourceDao.deleteById(id)
				.then(refreshResource(id))
				.then();
	}

	public Flux<Resource> getResource(Resource resource) {
		return resourceDao.find(resource).map(ResourceEntity::toVO);
	}

	public Mono<Role> saveRole(Role role) {
		return roleDao.save(new RoleEntity(role)).map(RoleEntity::toVO);
	}

	public Mono<Void> saveRoleResource(RoleResource roleResource) {
		long roleId = roleResource.getRoleId();
		return authDao.deleteRoleResource(roleId)
				.then(authDao.saveRoleResource(roleResource))
				.thenMany(authDao.findUserIdByRoleId(roleId))
				.flatMap(this::refreshUserResource)
				.then();
	}

	public Mono<Void> saveUserRole(UserRole userRole) {
		return authDao.deleteUserRoleByUserId(userRole.getUserId())
				.then(authDao.saveUserRole(userRole))
				.then(refreshUserResource(userRole.getUserId()));
	}

	public Mono<Void> initRedis() {
		return redisDao.deleteAllUserResource()
				.thenMany(authDao.findAllUserResource())
				.flatMap(map -> redisDao.saveUserResource(Long.parseLong(map.get("user_id").toString()), map.get("resource").toString()))
				.then();
	}

	private Mono<Void> refreshResource(long id) {
		return authDao.findRoleIdByResourceId(id)
				.then(refreshRole(id));
	}

	private Mono<Void> refreshRole(long id) {
		return authDao.findUserIdByRoleId(id)
				.flatMap(this::refreshUserResource)
				.then();
	}

	private Mono<Void> refreshUserResource(Long userId) {
		return authDao.findUserResource(userId)
				.flatMap(resource -> redisDao.saveUserResource(userId, resource))
				.then();
	}

}
