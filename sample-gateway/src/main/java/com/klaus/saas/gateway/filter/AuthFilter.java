package com.klaus.saas.gateway.filter;

import cn.hutool.json.JSONUtil;
import com.klaus.saas.commons.enums.StatusCode;
import com.klaus.saas.commons.model.DataResult;
import com.klaus.saas.commons.model.Result;
import com.klaus.saas.system.client.reactive.ReactiveSystemClient;
import com.klaus.saas.system.model.constants.AuthConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
@RequiredArgsConstructor
//@Order(-9999)
public class AuthFilter implements GlobalFilter, Ordered {

	public static final String ATTR_USERNAME = "attr_username";

	private final ReactiveSystemClient reactiveSystemClient;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

		ServerHttpRequest request = exchange.getRequest();
		ServerHttpResponse response = exchange.getResponse();

		String url = request.getURI().getPath();

		// login请求放行
		// 不知道这算不算spring的bug
		// 如果filter不是实现Ordered接口，而是使用@Order注解的话，这里的url取出来的值不带service-id
		if ("/login".equalsIgnoreCase(url)) {
			return chain.filter(exchange);
		}

		// 从请求头中取出token
		String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

		// 未携带token
		if (StringUtils.isEmpty(token)) {
			return response.writeWith(Mono.just(getDataBuffer(StatusCode.UN_AUTH, response)));
		}

		// postman 请求时，会加Bearer前缀
		if (token.startsWith("Bearer")) {
			token = token.substring(7);
		}

		// verify the token
		Mono<DataResult<String>> username = reactiveSystemClient.authentication(token, url);

		return username
				.flatMap(dataResult -> {
					int code = dataResult.getCode();
					switch (code) {
						case 200:
							return chain.filter(setHttpHeader(exchange, dataResult.getItem()));
						case 409:
							return response.writeWith(Mono.just(getDataBuffer(StatusCode.SESSION_TIMEOUT, response)));
						case 403:
							return response.writeWith(Mono.just(getDataBuffer(StatusCode.FORBIDDEN, response)));
						default:
							return response.writeWith(Mono.just(getDataBuffer(StatusCode.UN_AUTH, response)));
					}
				});
	}

	/**
	 * 认证、鉴权通过后，将username放入httpheader中
	 *
	 * @param exchange
	 * @param username
	 * @return
	 */
	private ServerWebExchange setHttpHeader(ServerWebExchange exchange, String username) {
		ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate().headers(httpHeaders -> {
			httpHeaders.set(AuthConstants.HEADER_USERNAME, username);
		}).build();
//		exchange.getAttributes().put(ATTR_USERNAME, username);
		return exchange.mutate().request(serverHttpRequest).build();
	}

	private DataBuffer getDataBuffer(StatusCode statusCode, ServerHttpResponse response) {
		byte[] bytes = JSONUtil.toJsonStr(new Result(statusCode)).getBytes(StandardCharsets.UTF_8);
		return response.bufferFactory().wrap(bytes);
	}

	@Override
	public int getOrder() {
		return -9999;
	}
}
