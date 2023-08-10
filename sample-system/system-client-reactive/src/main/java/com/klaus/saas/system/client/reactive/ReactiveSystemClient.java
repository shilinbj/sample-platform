package com.klaus.saas.system.client.reactive;

import com.klaus.saas.commons.model.DataResult;
import com.klaus.saas.system.model.vo.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import reactor.core.publisher.Mono;

/**
 * @author Klaus
 * @since 2023/8/10
 */
@HttpExchange
public interface ReactiveSystemClient {

	@GetExchange("/user/getByUsername")
	Mono<User> getByUsername(@RequestParam("username") String username);

//	@GetExchange("/verifyToken")
//	Mono<String> verifyToken(@RequestParam("token") String token);

	@GetMapping("/auth/authentication")
	Mono<DataResult<String>> authentication(@RequestParam("token") String token, @RequestParam("url") String url);

}
