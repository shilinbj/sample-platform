package com.klaus.saas.system.server.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Klaus
 * @since 2023/8/10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModifyPasswordRequest {

	@NotBlank(message = "Username can't be empty.")
	private String username;
	@NotBlank(message = "Original password can't be empty.")
	private String oldPassword;
	@NotBlank(message = "New password can't be empty.")
	private String newPassword;

}
