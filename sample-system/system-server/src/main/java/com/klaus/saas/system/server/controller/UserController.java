package com.klaus.saas.system.server.controller;

import com.klaus.saas.commons.enums.StatusCode;
import com.klaus.saas.commons.model.DataResult;
import com.klaus.saas.commons.model.MyPage;
import com.klaus.saas.commons.model.PagingRequest;
import com.klaus.saas.commons.model.Result;
import com.klaus.saas.system.model.vo.User;
import com.klaus.saas.system.server.service.UserService;
import com.klaus.saas.system.server.vo.ModifyPasswordRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * @author Klaus
 * @since 2023/8/10
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("user")
@Tag(name = "UserController", description = "User controller")
public class UserController {

	private final UserService userService;

	/**
	 * Get User by username from redis.
	 *
	 * @param username
	 * @return
	 */
	@GetMapping("getByUsernameFromRedis")
	@Operation(summary = "getByUsernameFromRedis", description = "Get User by username from redis.")
	@Parameter(name = "username", description = "username")
	public Mono<User> getByUsernameFromRedis(@RequestParam("username") String username) {
		return userService.getByUsernameFromRedis(username);
	}

	/**
	 * Paging query user list.
	 *
	 * @param param
	 * @return
	 */
	@PostMapping("getPage")
	@Operation(summary = "getPage", description = "Paging query user list.")
	@Parameter(name = "request param", description = "Paging query request parameters, include user parameter and paging infomation.")
	public Mono<DataResult<MyPage<User>>> getPage(@RequestBody PagingRequest<User> param) {
		return userService.getUserPage(param).map(DataResult::new);
	}

	@PostMapping("save")
	@Operation(summary = "save", description = "Create new user.")
	@Parameter(name = "user", description = "User basic infomation")
	public Mono<Result> save(@RequestBody User user) {
		return userService.saveUser(user).thenReturn(new Result(StatusCode.OK));
	}

	@PostMapping("modifyPassword")
	@Operation(summary = "modifyPassword", description = "修改密码")
	@Parameter(name = "ModifyPasswordRequest", description = "Username, old password and new password.")
	public Mono<Result> modifyPassword(@RequestBody @Valid ModifyPasswordRequest request) {
		return userService.modifyPassword(request);
	}

}
