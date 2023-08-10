package com.klaus.saas.system.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

/**
 * @author Klaus
 * @since 2023/8/10
 */
@SpringBootApplication
@AutoConfiguration
@EnableWebFlux
public class SystemServer {

	public static void main(String[] args) {
		SpringApplication.run(SystemServer.class, args);
	}

}
