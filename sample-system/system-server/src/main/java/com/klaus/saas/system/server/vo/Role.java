package com.klaus.saas.system.server.vo;

import com.klaus.saas.commons.model.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * @author Klaus
 * @since 2023/7/27
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@Schema(name = "Resource", description = "资源基本信息")
public class Role extends BaseVO {

	private long id;
	private String name;
	private String status;

}
