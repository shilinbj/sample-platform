package com.klaus.saas.system.server.vo;

import com.klaus.saas.commons.model.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "Resource", description = "Resource basic information.")
public class Resource extends BaseVO {

	@Schema(description = "ID, primary key, auto generate.")
	private long id;

	@Schema(description = "Resource name")
	private String name;

	@Schema(description = "URL")
	private String resource;

	@Schema(description = "Page ID")
	private Long pageId;

	@Schema(description = "Service ID")
	private Long serviceId;

	@Schema(description = "Resource type")
	private String type;

	@Schema(description = "Resource status")
	private String status;

	@Schema(description = "Sort")
	private int sort;

}
