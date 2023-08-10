package com.klaus.saas.system.server.config;

import com.klaus.saas.system.model.vo.User;
import com.klaus.saas.system.server.vo.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

	private final StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

	@Bean
	public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
		return new StringRedisTemplate(factory);
	}

	@Bean
	public RedisTemplate<String, User> userRedisTemplate(RedisConnectionFactory factory) {
		Jackson2JsonRedisSerializer<User> jsonSerializer = new Jackson2JsonRedisSerializer<>(User.class);
		RedisTemplate<String, User> template = new RedisTemplate<>();
		template.setKeySerializer(stringRedisSerializer);
		template.setValueSerializer(jsonSerializer);
		template.setConnectionFactory(factory);
		return template;
	}

	@Bean
	public ReactiveRedisTemplate<String, User> userRedisReactiveTemplate(ReactiveRedisConnectionFactory factory) {
		StringRedisSerializer keySerializer = new StringRedisSerializer();
		Jackson2JsonRedisSerializer<User> valueSerializer = new Jackson2JsonRedisSerializer<>(User.class);
		RedisSerializationContext.RedisSerializationContextBuilder<String, User> builder =
				RedisSerializationContext.newSerializationContext(keySerializer);
		RedisSerializationContext<String, User> context = builder.value(valueSerializer).build();
		return new ReactiveRedisTemplate<>(factory, context);
	}

	@Bean
	public ReactiveRedisTemplate<String, Resource> resourceRedisTemplate(ReactiveRedisConnectionFactory factory) {
		StringRedisSerializer keySerializer = new StringRedisSerializer();
		Jackson2JsonRedisSerializer<Resource> valueSerializer = new Jackson2JsonRedisSerializer<>(Resource.class);
		RedisSerializationContext.RedisSerializationContextBuilder<String, Resource> builder =
				RedisSerializationContext.newSerializationContext(keySerializer);
		RedisSerializationContext<String, Resource> context = builder.value(valueSerializer).build();
		return new ReactiveRedisTemplate<>(factory, context);
	}

}
