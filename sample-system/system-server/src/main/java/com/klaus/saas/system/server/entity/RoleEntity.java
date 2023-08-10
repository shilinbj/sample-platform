package com.klaus.saas.system.server.entity;

import com.klaus.saas.commons.entity.BaseEntity;
import com.klaus.saas.system.server.vo.Role;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
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
@Accessors(chain = true)
@Table(value = "sys_role", schema = "ctitc")
public class RoleEntity extends BaseEntity {

	@Id
	private long id;
	@Column("name")
	private String name;
	@Column("status")
	private String status;

	public RoleEntity(Role role) {
		this.id = role.getId();
		this.name = role.getName();
		this.status = role.getStatus();
	}

	@Override
	protected Role convert() {
		return Role.builder()
				.id(this.id)
				.name(this.name)
				.status(this.status)
				.build();
	}

}
