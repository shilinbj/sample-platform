package com.klaus.saas.system.server.entity;

import com.klaus.saas.commons.entity.BaseEntity;
import com.klaus.saas.system.server.vo.Resource;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author Klaus
 * @since 2023/8/10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@Table(value = "sys_resource", schema = "sample")
public class ResourceEntity extends BaseEntity {

	@Id
	private long id;
	@Column("name")
	private String name;
	@Column("resource")
	private String resource;
	@Column("page")
	private long page;
	@Column("service")
	private long service;
	@Column("type")
	private String type;
	@Column("status")
	private String status;
	@Column("sort")
	private int sort;

	public ResourceEntity(Resource resource) {
		this.id = resource.getId();
		this.name = resource.getName();
		this.resource = resource.getResource();
		this.page = resource.getPageId();
		this.service = resource.getServiceId();
		this.type = resource.getType();
		this.status = resource.getStatus();
		this.sort = resource.getSort();
	}

	@Override
	public Resource convert() {
		return Resource.builder()
				.id(this.id)
				.name(this.name)
				.resource(this.resource)
				.pageId(this.page)
				.serviceId(this.service)
				.type(this.type)
				.status(this.status)
				.sort(this.sort)
				.build();
	}

}
