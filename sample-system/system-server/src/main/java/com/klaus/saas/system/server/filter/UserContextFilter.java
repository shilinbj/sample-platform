package com.klaus.saas.system.server.filter;

import cn.hutool.json.JSONUtil;
import com.klaus.saas.commons.enums.StatusCode;
import com.klaus.saas.commons.model.Result;
import com.klaus.saas.system.model.vo.User;
import com.klaus.saas.system.server.constants.AuthConstants;
import com.klaus.saas.system.server.context.SessionContext;
import com.klaus.saas.system.server.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Set user filter.
 *
 * @author Klaus
 * @since 2023/8/10
 */
@Slf4j
@ConfigurationProperties(prefix = "interceptor")
@Component
@Order(1)
public class UserContextFilter implements WebFilter {

	@Autowired
	private UserService userService;

	private final List<PathPattern> pathPatternList = new ArrayList<>();

	@Setter
	private List<String> excludePath;

	@PostConstruct
	private void init() {
		excludePath.forEach(url -> pathPatternList.add(new PathPatternParser().parse(url)));
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		ServerHttpResponse response = exchange.getResponse();

		if (pathPatternList.stream().anyMatch(pathPattern -> pathPattern.matches(request.getPath().pathWithinApplication()))) {
			return chain.filter(exchange);
		}

		String username = request.getHeaders().getFirst(AuthConstants.CLAIM_USERNAME);

		if (StringUtils.isEmpty(username)) {
			return response.writeWith(unauth(response));
		}

		Mono<User> user = userService.getByUsernameFromRedis(username);
		return chain.filter(exchange).contextWrite(SessionContext.putUser(user));
	}

	private Mono<DataBuffer> unauth(ServerHttpResponse response) {
		byte[] bytes = JSONUtil.toJsonStr(new Result(StatusCode.UN_AUTH)).getBytes(StandardCharsets.UTF_8);
		return Mono.just(response.bufferFactory().wrap(bytes));
	}

}
