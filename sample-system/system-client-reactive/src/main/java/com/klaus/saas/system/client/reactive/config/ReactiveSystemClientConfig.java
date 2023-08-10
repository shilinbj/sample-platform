package com.klaus.saas.system.client.reactive.config;

import com.klaus.saas.system.client.reactive.ReactiveSystemClient;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class ReactiveSystemClientConfig {

	private static final String SYSTEM_SERVICE_URL = "http://cloud-system/";

	@Bean
	public ReactiveSystemClient reactiveSystemClient(ReactorLoadBalancerExchangeFilterFunction lbFunction) {
		WebClient client = WebClient.builder().filter(lbFunction).baseUrl(SYSTEM_SERVICE_URL).build();
		HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(client)).build();
		return factory.createClient(ReactiveSystemClient.class);
	}

}
