package com.klaus.saas.system.model.vo;

import com.klaus.saas.commons.model.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * @author Klaus
 * @since 2023/8/10
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@Tag(name = "User", description = "User basic infomation.")
public class User extends BaseVO {

	@Schema(description = "User ID, primary key, auto generate.")
	private long id;

	@NotBlank(message = "The username can't be empty.")
	@Schema(description = "username")
	private String username;

	@Schema(description = "name")
	private String name;

	@NotBlank(message = "The password can't be empty.")
	@Schema(description = "password")
	private String password;

	@NotBlank(message = "The sex can't be empty.")
	@Schema(description = "Sex")
	private String sex;

	@Schema(description = "Email")
	private String email;

	@Schema(description = "Mobile")
	private String mobile;

	@Schema(description = "User status")
	private String status;

}
