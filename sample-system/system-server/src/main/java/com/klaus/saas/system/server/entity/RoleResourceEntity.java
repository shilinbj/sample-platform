package com.klaus.saas.system.server.entity;

import com.klaus.saas.commons.entity.BaseEntity;
import com.klaus.saas.commons.model.BaseVO;
import com.klaus.saas.system.server.vo.RoleResource;
import lombok.*;
import lombok.experimental.Accessors;
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
@Table(value = "sys_role_resource", schema = "ctitc")
public class RoleResourceEntity extends BaseEntity {

	private long roleId;
	private long resourceId;
	private String resource;

	@Override
	protected BaseVO convert() {
		return RoleResource.builder()
				.roleId(this.roleId)
				.build();
	}

}
