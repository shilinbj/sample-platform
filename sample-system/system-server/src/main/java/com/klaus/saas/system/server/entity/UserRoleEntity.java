package com.klaus.saas.system.server.entity;

import com.klaus.saas.commons.entity.BaseEntity;
import com.klaus.saas.commons.model.BaseVO;
import com.klaus.saas.system.server.vo.UserRole;
import lombok.*;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author Klaus
 * @since 2023/7/27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(value = "sys_user_role", schema = "ctitc")
public class UserRoleEntity extends BaseEntity {

	private long userId;
	private long roleId;

	@Override
	protected BaseVO convert() {
		return UserRole.builder()
				.userId(this.userId)
				.build();
	}
}
