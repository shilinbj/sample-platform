package com.klaus.saas.system.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author shilin
 * @since 2019-09-17
 */
@ConfigurationProperties(prefix = "token")
@Component
@Data
public class JwtProperties {

	/**
	 * jwt secret
	 */
	private String secret;
	/**
	 * jwt token expire time, time unit: minute
	 */
	private long tokenExpireTime;
	/**
	 * jwt token expire time in redis, time unit: minute
	 */
	private long redisExpireTime;

}
