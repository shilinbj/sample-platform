package com.klaus.saas.system.server;

import com.klaus.saas.system.server.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author Klaus
 * @since 2023/8/10
 */
@SpringBootTest
public class MyTest {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AuthService authService;

	@Test
	public void genPassword() {
		System.out.println(passwordEncoder.encode("admin"));
	}

}
