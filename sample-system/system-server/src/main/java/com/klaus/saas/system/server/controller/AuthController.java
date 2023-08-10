package com.klaus.saas.system.server.controller;

import com.klaus.saas.commons.enums.StatusCode;
import com.klaus.saas.commons.model.DataResult;
import com.klaus.saas.commons.model.Result;
import com.klaus.saas.system.server.service.AuthService;
import com.klaus.saas.system.server.vo.Resource;
import com.klaus.saas.system.server.vo.Role;
import com.klaus.saas.system.server.vo.RoleResource;
import com.klaus.saas.system.server.vo.UserRole;
import com.klaus.saas.system.server.vo.request.LoginRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 全部关于权限相关接口，包括login, logout, resource, role等
 *
 * @author Klaus
 * @since 2023/7/25
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("auth")
@Tag(name = "AuthController", description = "All of api of authentication and authorization.")
public class AuthController {

	private final AuthService authService;

	@PostMapping("login")
	@Operation(summary = "login", description = "login")
	@Parameter(name = "loginRequest", description = "username and password.")
	public Mono<DataResult<String>> login(@RequestBody @Valid LoginRequest loginRequest) {
		return authService.login(loginRequest);
	}

	@PostMapping("logout")
	@Operation(summary = "logout", description = "logout")
	public Mono<Result> logout() {
		return authService.logout().thenReturn(new Result(StatusCode.OK));
	}

	@GetMapping("authentication")
	@Operation(summary = "authentication", description = "token认证并鉴权")
	@Parameters(value = {
			@Parameter(name = "token", description = "token"),
			@Parameter(name = "url", description = "Request path")
	})
	public Mono<DataResult<String>> authentication(@RequestParam("token") String token, @RequestParam("url") String url) {
		return authService.authentication(token, url);
	}

	@PostMapping("saveResource")
	@Operation(summary = "saveResource", description = "Save resource.")
	@Parameter(name = "resource", description = "Resource infomation.")
	public Mono<Result> saveResource(@RequestBody Resource resource) {
		return authService.saveResource(resource).thenReturn(new Result(StatusCode.OK));
	}

	@PostMapping("modifyResource")
	@Operation(summary = "modifyResource", description = "Modify resource.")
	@Parameter(name = "resource", description = "Resource infomation.")
	public Mono<Result> modifyResource(@RequestBody Resource resource) {
		return authService.modifyResource(resource).thenReturn(new Result(StatusCode.OK));
	}

	@PostMapping("deleteResource")
	@Operation(summary = "deleteResource", description = "Delete resource.")
	@Parameter(name = "id", description = "Resource ID.")
	public Mono<Result> deleteResource(@RequestParam("id") Long id) {
		return authService.deleteResource(id).thenReturn(new Result(StatusCode.OK));
	}

	@GetMapping("getResource")
	@Operation(summary = "getResource", description = "根据服务ID、页面ID")
	public Mono<DataResult<List<Resource>>> getResource(@RequestBody Resource resource) {
		return authService.getResource(resource)
				.collectList()
				.map(DataResult::new);
	}

	@PostMapping("saveRole")
	@Operation(summary = "saveRole", description = "Save role infomation.")
	public Mono<Result> saveRole(@RequestBody Role role) {
		return authService.saveRole(role).thenReturn(new Result(StatusCode.OK));
	}

	@PostMapping("saveRoleResource")
	@Operation(summary = "saveRoleResource", description = "Save relation of role and resource.")
	public Mono<Result> saveRoleResource(@RequestBody RoleResource roleResource) {
		return authService.saveRoleResource(roleResource).thenReturn(new Result(StatusCode.OK));
	}

	@PostMapping("saveUserRole")
	@Operation(summary = "saveUserRole", description = "Save relation of user and role")
	public Mono<Result> saveUserRole(@RequestBody UserRole userRole) {
		return authService.saveUserRole(userRole).thenReturn(new Result(StatusCode.OK));
	}

	@PostMapping("initRedis")
	@Operation(summary = "initRedis", description = "Init redis")
	public Mono<Result> initRedis() {
		return authService.initRedis().thenReturn(new Result(StatusCode.OK));
	}

}
