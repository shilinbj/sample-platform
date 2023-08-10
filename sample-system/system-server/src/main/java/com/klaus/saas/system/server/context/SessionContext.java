package com.klaus.saas.system.server.context;

import com.klaus.saas.system.model.vo.User;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

/**
 * @author Klaus
 * @since 2020-12-24
 */
public class SessionContext {

	public static final String KEY_USER = "s_user";

	public static Mono<User> user() {
		return Mono.deferContextual(context -> context.get(KEY_USER));
	}

	public static Mono<Long> userId() {
		return Mono.deferContextual(context -> context.<Mono<User>>get(KEY_USER)).map(User::getId);
	}

	public static Mono<String> username() {
		return Mono.deferContextual(context -> context.<Mono<User>>get(KEY_USER)).map(User::getUsername);
	}

	public static Context putUser(Mono<User> user) {
		return Context.of(KEY_USER, user);
	}

}
