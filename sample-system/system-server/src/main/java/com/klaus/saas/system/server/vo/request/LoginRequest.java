package com.klaus.saas.system.server.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

	@NotBlank(message = "The username don’t allow empty.")
	@Schema(description = "username")
	private String username;

	@NotBlank(message = "The password don't allow empty.")
	@Schema(description = "password")
	private String password;

}
