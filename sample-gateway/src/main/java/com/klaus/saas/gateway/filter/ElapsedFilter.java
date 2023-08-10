package com.klaus.saas.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 统计每一次访问请求的响应时间
 *
 * @author shilin
 * @since 2018-06-06
 */
@Component
@Order(Integer.MAX_VALUE - 1)
@Slf4j
public class ElapsedFilter implements GlobalFilter {

	private static final String ATTR_ELAPSED_TIME_BEGIN = "elapsedTimeBegin";
	private static final String ATTR_ORIGINAL_PARAMS = "originalParams";

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		exchange.getAttributes().put(ATTR_ELAPSED_TIME_BEGIN, System.currentTimeMillis());
		return chain.filter(exchange).then(
				Mono.fromRunnable(() -> {
					Long startTime = exchange.getAttribute(ATTR_ELAPSED_TIME_BEGIN);
					String uri = exchange.getRequest().getURI().getRawPath();
					Object obj = exchange.getAttributes().get(ATTR_ORIGINAL_PARAMS);
					String params = obj == null ? "" : obj.toString();
					if (startTime != null) {
						String usetime = (System.currentTimeMillis() - startTime) + "ms";
						log.info(uri + ": " + usetime + " params: " + params);
					} else {
						log.info(uri + ": start time is null. params: " + params);
					}
				})
		);
	}

}
